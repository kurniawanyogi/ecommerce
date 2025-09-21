package handler

import (
	"bytes"
	"github.com/gin-gonic/gin"
	"go.uber.org/zap"
	"io"
	"net/http"
	"upload-file-service/internal/application/file-upload/model"
	"upload-file-service/internal/application/file-upload/service"
)

type FileUploadHandler struct {
	service service.FileUploadService
	logger  *zap.Logger
}

func NewFileUploadHandler(service service.FileUploadService, logger *zap.Logger) *FileUploadHandler {
	return &FileUploadHandler{
		service: service,
		logger:  logger,
	}
}

func (h *FileUploadHandler) UploadFile(c *gin.Context) {
	fileHeader, err := c.FormFile("file")
	if err != nil {
		h.logger.Error("Invalid file", zap.Error(err))
		c.JSON(http.StatusBadRequest, gin.H{"error": "file is required"})
		return
	}

	file, err := fileHeader.Open()
	if err != nil {
		h.logger.Error("Invalid file", zap.Error(err))
		c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to open file"})
		return
	}
	defer file.Close()

	buf := new(bytes.Buffer)
	if _, err := io.Copy(buf, file); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "failed to read file"})
		return
	}

	fileName := c.PostForm("file_name")
	if fileName == "" {
		fileName = fileHeader.Filename
	}

	contentType := c.PostForm("content_type")
	if contentType == "" {
		contentType = fileHeader.Header.Get("Content-Type")
	}

	publicURL, path, err := h.service.Upload(fileName, contentType, buf.Bytes())
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "upload failed", "detail": err.Error()})
		return
	}

	c.JSON(http.StatusOK, model.UploadResponse{
		PublicURL: publicURL,
		Path:      path,
	})
}

func (h *FileUploadHandler) RegisterRoutes(api *gin.RouterGroup) {
	users := api.Group("/uploads")
	{
		users.POST("", h.UploadFile)
	}
}
