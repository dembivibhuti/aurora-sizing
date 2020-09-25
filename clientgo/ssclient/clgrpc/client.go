package clgrpc

import (
	"context"
	"log"

	"google.golang.org/grpc"

	pb "github.com/somnath67643/aurora-sizing/clientgo/baseproto"
)

type SSClient struct {
	client pb.ObjServiceClient
	conn   *grpc.ClientConn
}

func mustGetConn(addr string) *grpc.ClientConn {
	var opts []grpc.DialOption
	opts = append(opts, grpc.WithInsecure())
	opts = append(opts, grpc.WithBlock())
	conn, err := grpc.Dial(addr, opts...)
	if err != nil {
		log.Fatal(err)
	}
	return conn
}

func NewSSClient(addr string) *SSClient {
	conn := mustGetConn(addr)
	return &SSClient{
		client: pb.NewObjServiceClient(conn),
		conn:   conn,
	}
}

func (s *SSClient) Close() {
	s.conn.Close()
}

func (s *SSClient) Init() {
	// don't need any initialization here
}

func (s *SSClient) UseService(dbname string, closure func()) error {
	ctx := context.Background()
	_, err := s.client.Connect(ctx, &pb.CmdConnect{AppName: dbname})
	if err != nil {
		return err
	}
	closure() // call the closure here
	return nil
}

func (s *SSClient) LookupByName(prefix string, cmpType string) ([]string, error) {
	return []string{}, nil
}

func (s *SSClient) LookupByType(prefix string, stype string, cmpType string) ([]string, error) {
	return []string{}, nil
}

func (s *SSClient) GetObject(sname string) {
}

func (s *SSClient) GetObjectMany(snames []string) {
}
