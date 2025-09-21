package file_upload

import (
	"go.uber.org/fx"
	"upload-file-service/internal/application/file-upload/handler"
	"upload-file-service/internal/application/file-upload/service"
)

var Module = fx.Options(
	fx.Provide(
		service.NewFileUploadService,
		handler.NewFileUploadHandler,
	),
)
