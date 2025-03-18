package main

import (
	"context"
	"github.com/Shopify/sarama"
	"time"
	"user-management-service/cmd/http"
	"user-management-service/cmd/middleware"
	"user-management-service/common"
	"user-management-service/common/logger"
	"user-management-service/config"
	db "user-management-service/config/database"
	"user-management-service/delivery"
	httpDeliveryHealth "user-management-service/delivery/health"
	httpDeliveryUser "user-management-service/delivery/user"
	"user-management-service/kafka"
	"user-management-service/repository"
	repositoryUser "user-management-service/repository/user"
	"user-management-service/service"
	serviceHealth "user-management-service/service/health"
	serviceUser "user-management-service/service/user"
)

func main() {
	ctx := context.Background()
	// Start Init //
	loc, err := time.LoadLocation(config.LoadTimeZoneFromEnv())
	if err != nil {
		panic(err)
	}
	time.Local = loc
	// Configuration
	config.Init()

	// Logger
	logger.Init(logger.Config{
		AppName: config.Cold.AppName,
		Debug:   config.Hot.AppDebug,
	})

	database, err := db.NewDB(&db.Config{
		Driver:                config.Cold.DBMysqlDriver,
		Host:                  config.Cold.DBMysqlHost,
		Port:                  config.Cold.DBMysqlPort,
		DBName:                config.Cold.DBMysqlDBName,
		User:                  config.Cold.DBMysqlUser,
		Password:              config.Cold.DBMysqlPassword,
		SSLMode:               config.Cold.DBMysqlSSLMode,
		MaxOpenConnections:    config.Cold.DBMysqlMaxOpenConnections,
		MaxLifeTimeConnection: config.Cold.DBMysqlMaxLifeTimeConnection,
		MaxIdleConnections:    config.Cold.DBMysqlMaxIdleConnections,
		MaxIdleTimeConnection: config.Cold.DBMysqlMaxIdleTimeConnection,
	})
	if err != nil {
		panic(err)
	}

	masterTx := db.NewTransactionRunner(database)

	//kafka publisher
	kafkaBroadcasterConfig := sarama.NewConfig()
	kafkaBroadcasterConfig.Producer.RequiredAcks = sarama.NoResponse
	kafkaBroadcasterConfig.Producer.Timeout = time.Millisecond * time.Duration(config.Cold.KafkaTimeoutInMs)
	kafkaBroadcasterConfig.Producer.Return.Successes = true
	kafkaBroadcasterConfig.Producer.Retry.Max = config.Cold.KafkaProducerRetryBackoffInMs
	kafkaBroadcasterConfig.Producer.Retry.Backoff = time.Millisecond * time.Duration(config.Cold.KafkaProducerRetryBackoffInMs)
	kafkaBroadcasterConfig.Producer.Retry.BackoffFunc = func(retries, maxRetries int) time.Duration {
		return time.Duration(retries) * kafkaBroadcasterConfig.Producer.Retry.Backoff
	}
	kafkaPublisherSync, err := kafka.NewSyncPublisher(
		[]string{config.Cold.KafkaHost + ":" + config.Cold.KafkaPort},
		kafkaBroadcasterConfig,
	)
	if err != nil {
		panic(err)
	}

	kafkaPublisherAsync, err := kafka.NewAsyncPublisher(
		[]string{config.Cold.KafkaHost + ":" + config.Cold.KafkaPort},
		kafkaBroadcasterConfig,
	)
	if err != nil {
		panic(err)
	}

	// Registry
	commonValidator := common.NewValidator()
	commonRegistry := common.NewRegistry(
		common.WithValidator(commonValidator),
		common.AddPublisher("sync", kafkaPublisherSync),
		common.AddPublisher("async", kafkaPublisherAsync),
	)
	// End Init //

	// Start Repositories //
	userRepository := repositoryUser.NewUserRepository(commonRegistry, database)
	repositories := repository.NewRegistry(database, masterTx, userRepository)
	// End Repositories //

	// Start Services //
	userService := serviceUser.NewUserService(commonRegistry, repositories)
	healthService := serviceHealth.NewHealth(database)
	services := service.NewRegistry(
		healthService,
		userService,
	)
	// End Services //

	// Start Deliveries //
	healthDelivery := httpDeliveryHealth.NewHealth(commonRegistry, healthService)
	userDelivery := httpDeliveryUser.NewUserDelivery(commonRegistry, services)
	registryDelivery := delivery.NewRegistry(
		healthDelivery,
		userDelivery,
	)
	// End Deliveries //

	// Start Middleware
	middlewares := middleware.NewMiddleware(commonRegistry, services)
	// End Middleware

	// Start HTTP Server //
	httpServer := http.NewServer(
		commonRegistry,
		registryDelivery,
		middlewares,
	)

	httpServer.Serve(ctx)
	// End HTTP Server //
}
