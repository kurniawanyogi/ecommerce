package main

import (
	"context"
	"fmt"
	_ "github.com/gin-gonic/gin"
	"go.uber.org/fx"
	"os"
	"os/signal"
	"syscall"
	"upload-file-service/internal/config"
	"upload-file-service/internal/pkg/logger"
	"upload-file-service/internal/server/api"
)

func main() {
	app := fx.New(
		fx.Provide(
			config.NewConfig,
			logger.NewLogger,
		),
		api.Module,
		fx.Invoke(Run),
		fx.StartTimeout(config.DefaultStartTimeout),
		fx.StopTimeout(config.DefaultStopTimeout),
	)

	ctx := context.Background()
	if err := app.Start(ctx); err != nil {
		fmt.Fprintf(os.Stderr, "Failed to start application: %v\n", err)
		os.Exit(1)
	}

	// Setup graceful shutdown
	sigChan := make(chan os.Signal, 1)
	signal.Notify(sigChan, syscall.SIGINT, syscall.SIGTERM)

	// Wait for shutdown signal
	<-sigChan
	fmt.Println("\nReceived shutdown signal, stopping application gracefully...")

	if err := app.Stop(ctx); err != nil {
		fmt.Fprintf(os.Stderr, "Failed to stop application gracefully: %v\n", err)
		os.Exit(1)
	}

	fmt.Println("Application stopped successfully")
}
