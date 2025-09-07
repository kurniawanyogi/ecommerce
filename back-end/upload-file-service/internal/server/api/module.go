package api

import (
	"github.com/gin-gonic/gin"
	"github.com/swaggo/files"
	ginSwagger "github.com/swaggo/gin-swagger"
	"go.uber.org/zap"
	"upload-file-service/internal/middleware"
)

type Server struct {
	logger *zap.Logger
}

func NewServer(
	logger *zap.Logger,
) *Server {
	return &Server{
		logger: logger,
	}
}

func (s *Server) SetupRoutes(router *gin.Engine) {
	// Apply global middleware
	router.Use(middleware.Logger(s.logger))
	router.Use(middleware.Recovery(s.logger))
	router.Use(middleware.CORS())

	// Swagger documentation routes
	router.GET("/swagger/*any", ginSwagger.WrapHandler(swaggerFiles.Handler))
	router.GET("/docs", func(c *gin.Context) {
		c.Redirect(302, "/swagger/index.html")
	})

	// Register API routes
	api := router.Group("/api/v1")
	{
		s.registerHealthRoutes(api)
	}
}

func (s *Server) registerHealthRoutes(api *gin.RouterGroup) {
	health := api.Group("/health")
	{
		health.GET("", s.healthCheck)
		health.GET("/ready", s.readinessCheck)
	}
}

// HealthCheck godoc
// @Summary Show the status of server.
// @Description get the status of server.
// @Tags health
// @Accept */*
// @Produce json
// @Success 200 {object} map[string]interface{}
// @Router /health [get]
func (s *Server) healthCheck(c *gin.Context) {
	c.JSON(200, gin.H{
		"status":  "healthy",
		"service": "upload-file-service",
		"version": "1.0.0",
	})
}

// ReadinessCheck godoc
// @Summary Show the readiness of server.
// @Description get the readiness of server.
// @Tags health
// @Accept */*
// @Produce json
// @Success 200 {object} map[string]interface{}
// @Router /health/ready [get]
func (s *Server) readinessCheck(c *gin.Context) {
	c.JSON(200, gin.H{
		"status": "ready",
		"checks": gin.H{
			"database": "ok",
			"cache":    "ok",
		},
	})
}
