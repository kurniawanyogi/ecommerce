package health

import (
	"context"
	"fmt"
	"github.com/DATA-DOG/go-sqlmock"
	"github.com/jmoiron/sqlx"
	"github.com/stretchr/testify/suite"
	"testing"
	"user-management-service/model"
)

type listMock struct {
	database sqlmock.Sqlmock
}
type HealthTestSuite struct {
	suite.Suite
	mocks         listMock
	HealthService IHealth
}

func TestHealthTestSuite(t *testing.T) {
	suite.Run(t, new(HealthTestSuite))
}

func (s *HealthTestSuite) SetupSuite() {
	fmt.Println("SetupSuite: HealthTestSuite")
}

func (s *HealthTestSuite) TearDownSuite() {
	fmt.Println("TearDownSuite: HealthTestSuite")
}

func (s *HealthTestSuite) SetupTest() {
	fmt.Println("SetupTest: HealthTestSuite")
	db, mockDB, err := sqlmock.New(sqlmock.MonitorPingsOption(true))
	s.NoError(err)
	database := sqlx.NewDb(db, "sqlmock")

	s.mocks = listMock{
		database: mockDB,
	}
	s.HealthService = NewHealth(database)
}

func (s *HealthTestSuite) TearDownTest() {
	fmt.Println("TearDownTest: HealthTestSuite")
}

func (s *HealthTestSuite) TestCheckHealth() {
	type (
		args struct {
			ctx context.Context
		}
		want struct {
			res model.HTTPResponse
		}

		testCase struct {
			name     string
			args     args
			mockFunc func(listMock *listMock, args args)
			want     want
		}
	)
	testCases := []testCase{
		{
			name: "success connect to database",
			args: args{
				ctx: context.Background(),
			},
			mockFunc: func(m *listMock, args args) {
				m.database.ExpectPing()
			},
			want: want{
				res: model.HTTPResponse{
					Database: OK,
				},
			},
		},
		{
			name: "failed connect to database",
			args: args{
				ctx: context.Background(),
			},
			mockFunc: func(m *listMock, args args) {
				m.database.ExpectPing().WillReturnError(fmt.Errorf("some error"))
			},
			want: want{
				res: model.HTTPResponse{
					Database: BAD,
				},
			},
		},
	}

	for _, tc := range testCases {
		s.Run(tc.name, func() {
			s.SetupTest()
			tc.mockFunc(&s.mocks, tc.args)

			response := s.HealthService.Check(tc.args.ctx)

			s.Equal(tc.want.res.Database, response.Database)
		})
	}
}
