package delivery

import (
	"user-management-service/delivery/health"
	"user-management-service/delivery/user"
)

type IRegistry interface {
	GetHealth() health.IHealth
	GetUserDelivery() user.IUserDelivery
}

type Registry struct {
	health       health.IHealth
	userDelivery user.IUserDelivery
}

func NewRegistry(
	health health.IHealth,
	userDelivery user.IUserDelivery,
) *Registry {
	return &Registry{
		health:       health,
		userDelivery: userDelivery,
	}
}

func (r *Registry) GetHealth() health.IHealth {
	return r.health
}

func (r *Registry) GetUserDelivery() user.IUserDelivery {
	return r.userDelivery
}
