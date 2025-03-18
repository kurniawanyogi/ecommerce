package kafka

import (
	"context"
	"encoding/json"
	"time"
)

type IMessage interface {
	GetHeaders(ctx context.Context) map[string]string
	GetMeta() any
	GetValue() (string, error)
}

type EventName string

type MessageEvent struct {
	Name EventName `json:"name"`
}

type MessageMeta struct {
	Sender    string     `json:"sender"`
	SendingAt time.Time  `json:"sendingAt"`
	ExpiredAt *time.Time `json:"expiredAt"`
	Version   *string    `json:"version"`
}

type DataType string

const (
	JSON   DataType = "JSON"
	Byte   DataType = "BYTE"
	String DataType = "STRING"
)

type MessageBody[T any] struct {
	Type DataType `json:"type"`
	Data T        `json:"data"`
}

type Message[T any] struct {
	Event MessageEvent   `json:"event"`
	Meta  MessageMeta    `json:"meta"`
	Body  MessageBody[T] `json:"body"`
}

func NewMessage[T any](event MessageEvent, meta MessageMeta, bodyType DataType, body T) *Message[T] {
	return &Message[T]{
		Event: event,
		Meta:  meta,
		Body: MessageBody[T]{
			Type: bodyType,
			Data: body,
		},
	}
}

func (m *Message[T]) GetHeaders(ctx context.Context) map[string]string {
	headers := make(map[string]string, 1)
	headers["x-request-id"] = getValueAsString(ctx, "x-request-id")
	return headers
}

func (m *Message[T]) GetValue() (string, error) {
	b, err := json.Marshal(m)
	if err != nil {
		return "", err
	}

	return string(b), nil
}

func (m *Message[T]) GetMeta() any {
	return m.Meta
}

func getValueAsString(ctx context.Context, key string) string {
	val, ok := ctx.Value(key).(string)
	if ok {
		return val
	}

	return ""
}
