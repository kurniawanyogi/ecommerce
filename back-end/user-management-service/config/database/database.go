package database

import (
	"fmt"
	"github.com/jmoiron/sqlx"
	"time"

	_ "github.com/denisenkom/go-mssqldb"
	_ "github.com/golang-migrate/migrate/v4/database/mysql"
	_ "github.com/golang-migrate/migrate/v4/database/postgres"
)

type Config struct {
	Driver   string `json:"driver" yaml:"driver"`
	Host     string `json:"host" yaml:"host"`
	Port     int    `json:"port" yaml:"port"`
	DBName   string `json:"dbName" yaml:"dbName"`
	User     string `json:"user" yaml:"user"`
	Password string `json:"password" yaml:"password"`
	SSLMode  string `json:"sslMode" yaml:"sslMode"`

	MaxOpenConnections    int `json:"maxOpenConnections" yaml:"maxOpenConnections"`
	MaxLifeTimeConnection int `json:"maxLifeTimeConnection" yaml:"maxLifeTimeConnection"` // Seconds
	MaxIdleConnections    int `json:"maxIdleConnections" yaml:"maxIdleConnections"`
	MaxIdleTimeConnection int `json:"maxIdleTimeConnection" yaml:"maxIdleTimeConnection"` // Seconds
}

func NewDB(config *Config) (*sqlx.DB, error) {
	dsn := GetDsn(config)

	conn, err := sqlx.Open(config.Driver, dsn)
	if err != nil {
		return nil, err
	}

	conn.SetConnMaxLifetime(time.Duration(config.MaxLifeTimeConnection) * time.Second)
	conn.SetMaxOpenConns(config.MaxOpenConnections)
	conn.SetMaxIdleConns(config.MaxIdleConnections)
	conn.SetConnMaxIdleTime(time.Duration(config.MaxIdleTimeConnection) * time.Second)

	if er := conn.Ping(); er != nil {
		return nil, er
	}

	return conn, nil
}

func GetDsn(config *Config) string {
	if config.Driver == "mysql" {
		return fmt.Sprintf(
			"%s:%s@(%s:%d)/%s?parseTime=true",
			config.User, config.Password, config.Host, config.Port, config.DBName,
		)
	} else if config.Driver == "postgres" {
		return fmt.Sprintf(
			"host=%s port=%d user=%s password=%s dbname=%s sslmode=%s",
			config.Host, config.Port, config.User, config.Password, config.DBName, config.SSLMode,
		)
	} else if config.Driver == "mssql" {
		return fmt.Sprintf(
			"server=%s;user id=%s;password=%s;port=%d;database=%s;",
			config.Host, config.User, config.Password, config.Port, config.DBName,
		)
	}
	return ""
}
