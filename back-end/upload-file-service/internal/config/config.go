package config

import (
	"time"

	"github.com/spf13/viper"
)

const (
	DefaultStartTimeout = 15 * time.Second
	DefaultStopTimeout  = 10 * time.Second
)

type Config struct {
	Server ServerConfig `mapstructure:"api"`
	Logger LoggerConfig `mapstructure:"logger"`
	Worker WorkerConfig `mapstructure:"worker"`
}

type ServerConfig struct {
	Host         string        `mapstructure:"host"`
	Port         int           `mapstructure:"port"`
	ReadTimeout  time.Duration `mapstructure:"read_timeout"`
	WriteTimeout time.Duration `mapstructure:"write_timeout"`
	IdleTimeout  time.Duration `mapstructure:"idle_timeout"`
}

type LoggerConfig struct {
	Level      string `mapstructure:"level"`
	Format     string `mapstructure:"format"`
	OutputPath string `mapstructure:"output_path"`
}

type WorkerConfig struct {
	Concurrency          int           `mapstructure:"concurrency"`
	PaymentCheckInterval time.Duration `mapstructure:"payment_check_interval"`
	RetryMaxAttempts     int           `mapstructure:"retry_max_attempts"`
	RetryDelay           time.Duration `mapstructure:"retry_delay"`
}

func NewConfig() (*Config, error) {
	viper.SetConfigName("config")
	viper.SetConfigType("yaml")
	viper.AddConfigPath(".")
	viper.AddConfigPath("./config")

	viper.SetDefault("api.host", "localhost")
	viper.SetDefault("api.port", 8882)
	viper.SetDefault("api.read_timeout", "10s")
	viper.SetDefault("api.write_timeout", "10s")
	viper.SetDefault("api.idle_timeout", "60s")

	viper.SetDefault("logger.level", "info")
	viper.SetDefault("logger.format", "json")
	viper.SetDefault("logger.output_path", "stdout")

	viper.SetDefault("worker.concurrency", 10)
	viper.SetDefault("worker.payment_check_interval", "5m")
	viper.SetDefault("worker.retry_max_attempts", 3)
	viper.SetDefault("worker.retry_delay", "30s")

	viper.AutomaticEnv()

	if err := viper.ReadInConfig(); err != nil {
		if _, ok := err.(viper.ConfigFileNotFoundError); !ok {
			return nil, err
		}
	}

	var config Config
	if err := viper.Unmarshal(&config); err != nil {
		return nil, err
	}

	return &config, nil
}
