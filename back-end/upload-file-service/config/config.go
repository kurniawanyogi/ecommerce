package config

import (
	"fmt"
	"github.com/spf13/viper"
	"os"
	"reflect"
	"strconv"
	"strings"
)

var App AppConfig

type AppConfig struct {
	AppEnv      string `mapstructure:"APP_ENV"`
	AppName     string `mapstructure:"APP_NAME"`
	AppPort     uint   `mapstructure:"APP_PORT"`
	AppTimezone string `mapstructure:"APP_TIMEZONE"`
	AppApiKey   string `mapstructure:"APP_API_KEY"`
	SecretKey   string `mapstructure:"SECRET_KEY"`

	AppDebug              bool   `mapstructure:"APP_DEBUG"`
	LoggerDebug           bool   `mapstructure:"LOGGER_DEBUG"`
	ShutDownDelayInSecond uint64 `mapstructure:"SHUTDOWN_DELAY_IN_SECOND"`
}

func Init() {
	v := viper.New()

	// Bind environment variables
	v.AutomaticEnv()

	// Optional: you can make viper recognize underscore as separator for nested fields
	v.SetEnvPrefix("APP") // optional prefix
	v.SetEnvKeyReplacer(strings.NewReplacer(".", "_"))

	if err := v.Unmarshal(&App); err != nil {
		panic(fmt.Errorf("failed to bind env to config: %w", err))
	}

	// Optional: Set environment variables manually (if needed for compatibility)
	setEnvFromStruct(App)
}

func setEnvFromStruct(cfg interface{}) {
	val := reflect.ValueOf(cfg)
	typ := reflect.TypeOf(cfg)

	for i := 0; i < val.NumField(); i++ {
		field := val.Field(i)
		fieldType := typ.Field(i)
		envKey := fieldType.Tag.Get("mapstructure")

		if envKey == "" {
			continue
		}

		var strVal string
		switch field.Kind() {
		case reflect.String:
			strVal = field.String()
		case reflect.Int, reflect.Int64:
			strVal = strconv.FormatInt(field.Int(), 10)
		case reflect.Uint, reflect.Uint64:
			strVal = strconv.FormatUint(field.Uint(), 10)
		case reflect.Bool:
			strVal = strconv.FormatBool(field.Bool())
		default:
			continue
		}

		_ = os.Setenv(envKey, strVal)
	}
}
