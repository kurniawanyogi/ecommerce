package api

import (
	"go.uber.org/fx"
	file_upload "upload-file-service/internal/application/file-upload"
)

var Module = fx.Options(
	file_upload.Module,

	fx.Provide(NewServer),
)
