package repository

import (
	"github.com/jmoiron/sqlx"
	"user-management-service/config/database"
	"user-management-service/repository/user"
)

// @Notice: Register your repositories here

type IRegistry interface {
	GetTx() *database.TransactionRunner
	GetUserRepository() user.IUserRepository
}

type registry struct {
	dbMaster       *sqlx.DB
	masterTx       *database.TransactionRunner
	userRepository user.IUserRepository
}

func NewRegistry(
	dbMaster *sqlx.DB,
	masterTx *database.TransactionRunner,
	userRepository user.IUserRepository,
) *registry {
	return &registry{
		dbMaster:       dbMaster,
		masterTx:       masterTx,
		userRepository: userRepository,
	}
}

func (r *registry) GetTx() *database.TransactionRunner {
	return r.masterTx
}

func (r registry) GetUserRepository() user.IUserRepository {
	return r.userRepository
}
