package kafka

import (
	"context"
	"github.com/Shopify/sarama"
	"time"
)

type SyncPublisher struct {
	producer sarama.SyncProducer
}

func NewSyncPublisher(
	brokers []string,
	config *sarama.Config,
) (*SyncPublisher, error) {
	producer, err := sarama.NewSyncProducer(brokers, config)
	if err != nil {
		return nil, err
	}

	return &SyncPublisher{producer: producer}, nil
}

func (sp *SyncPublisher) Publish(ctx context.Context, topic Topic, message IMessage) (int32, int64, error) {
	messageHeaders := message.GetHeaders(ctx)
	headers := make([]sarama.RecordHeader, 0, len(messageHeaders))
	for key, value := range messageHeaders {
		headers = append(headers, sarama.RecordHeader{
			Key:   []byte(key),
			Value: []byte(value),
		})
	}
	value, err := message.GetValue()
	if err != nil {
		return 0, 0, err
	}

	msg := &sarama.ProducerMessage{
		Topic:     topic.String(),
		Value:     sarama.StringEncoder(value),
		Headers:   headers,
		Metadata:  message.GetMeta(),
		Timestamp: time.Now().UTC(),
	}
	partition, offset, err := sp.producer.SendMessage(msg)
	if err != nil {
		return 0, 0, err
	}

	return partition, offset, nil
}
