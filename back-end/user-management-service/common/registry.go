package common

import (
	"sync"
	"user-management-service/kafka"

	"github.com/go-playground/validator/v10"
)

// IRegistry TODO Implement redis etc here
type IRegistry interface {
	GetValidator() *validator.Validate
	GetPublisher(name string) kafka.IPublisher
}

type registry struct {
	mu        *sync.Mutex
	validator *validator.Validate
	publisher map[string]kafka.IPublisher
}

func WithValidator(validator *validator.Validate) Option {
	return func(s *registry) {
		s.mu.Lock()
		defer s.mu.Unlock()

		s.validator = validator
	}
}

func AddPublisher(name string, publisher kafka.IPublisher) Option {
	return func(s *registry) {
		s.mu.Lock()
		defer s.mu.Unlock()

		if s.publisher == nil {
			s.publisher = make(map[string]kafka.IPublisher)
		}
		s.publisher[name] = publisher
	}
}

type Option func(r *registry)

func NewRegistry(
	options ...Option,
) IRegistry {
	registry := &registry{mu: &sync.Mutex{}}

	for _, option := range options {
		option(registry)
	}

	return registry
}

func (r *registry) GetValidator() *validator.Validate {
	return r.validator
}

func (r *registry) GetPublisher(name string) kafka.IPublisher {
	if publisher, exist := r.publisher[name]; exist {
		return publisher
	}
	return nil
}
