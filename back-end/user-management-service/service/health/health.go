package health

import (
	"context"
	"github.com/jmoiron/sqlx"
	"user-management-service/common/logger"
	"user-management-service/model"
)

const (
	OK  = "OK"
	BAD = "BAD"
)

type IHealth interface {
	Check(ctx context.Context) model.HTTPResponse
}

type Health struct {
	Database *sqlx.DB
}

func NewHealth(database *sqlx.DB) *Health {
	return &Health{
		Database: database,
	}
}

func (s *Health) Check(ctx context.Context) model.HTTPResponse {
	var response = model.HTTPResponse{
		Database: OK,
	}

	// Check Database connection
	err := s.Database.PingContext(ctx)
	if err != nil {
		response.Database = BAD
		logger.Error(ctx, err.Error(), err, logger.Tag{Key: "logCtx", Value: ctx})
	}

	return response
}
