package kafka

import (
	"context"
	"github.com/Shopify/sarama"
	"time"
)

type AsyncPublisher struct {
	producer sarama.AsyncProducer
}

func NewAsyncPublisher(
	brokers []string,
	config *sarama.Config,
) (*AsyncPublisher, error) {
	producer, err := sarama.NewAsyncProducer(brokers, config)
	if err != nil {
		return nil, err
	}

	return &AsyncPublisher{producer: producer}, nil
}

func (asp *AsyncPublisher) Publish(ctx context.Context, topic Topic, message IMessage) (int32, int64, error) {

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
	asp.producer.Input() <- msg

	return 0, 0, nil
}
