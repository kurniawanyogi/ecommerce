package model

type UploadResponse struct {
	PublicURL string `json:"public_url"`
	Path      string `json:"path"`
}
