package common

import "errors"

const (
	StatusSuccess = "success"
	StatusFail    = "fail"
	StatusError   = "error"

	Timezone        = "TIMEZONE"
	DefaultTimeZone = "Asia/Jakarta"

	ConsulWatchInterval       = "CONSUL_WATCH_INTERVAL_SECONDS"
	DefaultLoadConsulInterval = 30

	XServiceNameHeader      = "x-service-name"
	XRequestIdHeader        = "x-request-id"
	XUserType               = "x-user-type"
	XUserDetail             = "x-user-detail"
	XApiKeyHeader           = "x-api-key"
	AuthorizationHeader     = "authorization"
	ContextBackground       = "ContextBackground"
	XRequestSignatureHeader = "x-request-signature"
	XRequestAtHeader        = "x-request-at"
	ContentTypeHeader       = "content-type"

	StatusUserActive   = "active"
	StatusUserInactive = "inactive"
)

var (
	ErrSQLQueryBuilder      = errors.New("error query builder")
	ErrSQLExec              = errors.New("error sql exec")
	ErrDataNotFound         = errors.New("data not found")
	ErrUsernameAlreadyTaken = errors.New("username already taken")
	ErrEmailAlreadyTaken    = errors.New("email already taken")
	ErrFailHashPassword     = errors.New("failed to hash password")
	ErrUserAlreadyDeleted   = errors.New("user already deleted")
	ErrInvalidPassword      = errors.New("invalid password")
	ErrInvalidToken         = errors.New("invalid token")
)
