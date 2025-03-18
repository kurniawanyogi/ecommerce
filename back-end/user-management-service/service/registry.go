package service

import (
	"user-management-service/service/health"
	"user-management-service/service/user"
)

type IRegistry interface {
	GetHealth() health.IHealth
	GetUserService() user.IUserService
}

type Registry struct {
	health      health.IHealth
	userService user.IUserService
}

func NewRegistry(
	health health.IHealth,
	userService user.IUserService,
) *Registry {
	return &Registry{
		health:      health,
		userService: userService,
	}
}

func (r *Registry) GetHealth() health.IHealth {
	return r.health
}

func (r *Registry) GetUserService() user.IUserService {
	return r.userService
}
