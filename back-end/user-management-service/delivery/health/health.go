package health

import (
	"github.com/gin-gonic/gin"
	"user-management-service/common"
	"user-management-service/model"
	"user-management-service/service/health"

	"net/http"
)

type IHealth interface {
	Check(c *gin.Context)
}

type Health struct {
	common common.IRegistry
	health health.IHealth
}

func NewHealth(common common.IRegistry, health health.IHealth) *Health {
	return &Health{
		common: common,
		health: health,
	}
}

func (h *Health) Check(c *gin.Context) {
	var (
		ctx     = c.Request.Context()
		status  = http.StatusOK
		message = http.StatusText(status)
	)

	c.JSON(http.StatusOK, model.Response{
		Status:  common.StatusSuccess,
		Data:    h.health.Check(ctx),
		Message: message,
	})
	return
}
