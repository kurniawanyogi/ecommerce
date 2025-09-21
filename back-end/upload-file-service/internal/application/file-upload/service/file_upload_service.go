package service

import (
	"bytes"
	"context"
	"fmt"
	"go.uber.org/zap"
	"io"
	"net/http"
	"upload-file-service/internal/config"
)

type FileUploadService interface {
	Upload(fileName, contentType string, data []byte) (string, string, error)
}

type fileUploadService struct {
	logger      *zap.Logger
	httpClient  *http.Client
	supabaseURL string
	serviceKey  string
	bucket      string
}

func NewFileUploadService(logger *zap.Logger, cfg *config.Config) FileUploadService {
	return &fileUploadService{
		logger:      logger,
		httpClient:  &http.Client{},
		supabaseURL: cfg.Supabase.URL,
		serviceKey:  cfg.Supabase.ServiceKey,
		bucket:      cfg.Supabase.Bucket,
	}
}

func (s *fileUploadService) Upload(fileName, contentType string, data []byte) (string, string, error) {
	ctx := context.Background()
	path := "uploads/" + fileName
	url := fmt.Sprintf("%s/storage/v1/object/%s/%s", s.supabaseURL, s.bucket, path)

	req, err := http.NewRequestWithContext(ctx, http.MethodPost, url, bytes.NewReader(data))
	if err != nil {
		s.logger.Error("failed to create upload request", zap.Error(err))
		return "", "", err
	}

	req.Header.Set("Authorization", "Bearer "+s.serviceKey)
	req.Header.Set("Content-Type", contentType)
	req.Header.Set("x-upsert", "true")

	resp, err := s.httpClient.Do(req)
	if err != nil {
		s.logger.Error("failed to upload file", zap.Error(err))
		return "", "", err
	}
	defer resp.Body.Close()

	if resp.StatusCode >= 300 {
		body, _ := io.ReadAll(resp.Body)
		s.logger.Error("upload failed", zap.Int("status", resp.StatusCode), zap.String("response", string(body)))
		return "", "", fmt.Errorf("upload failed: %s", string(body))
	}

	publicURL := fmt.Sprintf("%s/storage/v1/object/public/%s/%s", s.supabaseURL, s.bucket, path)
	return publicURL, path, nil
}
