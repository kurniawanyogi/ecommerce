package common

import "errors"

const (
	StatusSuccess = "success"
	StatusFail    = "fail"
	StatusError   = "error"

	Timezone        = "TIMEZONE"
	DefaultTimeZone = "Asia/Jakarta"

	XRequestIdHeader = "x-request-id"
)

var (
	ErrDataNotFound = errors.New("data not found")
	ErrInvalidToken = errors.New("invalid token")
)
