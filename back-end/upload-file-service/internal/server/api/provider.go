package api

import "go.uber.org/fx"

var Module = fx.Options(
	// TODO Include all domain modules

	// API api
	fx.Provide(NewServer),
)
