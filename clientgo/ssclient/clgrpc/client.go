package clgrpc

import (
	"context"
	"log"

	"google.golang.org/grpc"

	pb "github.com/somnath67643/aurora-sizing/clientgo/baseproto"
	"github.com/somnath67643/aurora-sizing/clientgo/ssclient/model"
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

func (s *SSClient) LookupByName(prefix string, cmpType model.CmpType, nr int32) (<-chan string, error) {
	ctx := context.Background()
	strmCl, err := s.client.LookupByNameStream(ctx, &pb.CmdLookupByName{
		Count:              nr,
		SecurityNamePrefix: prefix,
		GetType:            convertCmpTypeToGrpcGetType(cmpType),
	})
	if err != nil {
		return nil, err
	}
	ch := make(chan string)
	go func() {
		for {
			resp, err := strmCl.Recv()
			if err != nil { // also takes care of io.EOF
				break
			}
			ch <- resp.SecurityName
		}
		close(ch)
	}()
	return ch, nil
}

func (s *SSClient) LookupByType(prefix string, stype uint32, cmpType model.CmpType, nr int32) (<-chan string, error) {
	ctx := context.Background()
	strmCl, err := s.client.LookupByTypeStream(ctx, &pb.CmdNameLookupByType{
		GetType:            convertCmpTypeToGrpcGetType(cmpType),
		Count:              nr,
		SecurityType:       stype,
		SecurityNamePrefix: prefix,
	})
	if err != nil {
		return nil, err
	}
	ch := make(chan string)
	go func() {
		for {
			resp, err := strmCl.Recv()
			if err != nil {
				break
			}
			ch <- resp.SecurityName
		}
		close(ch)
	}()
	return ch, nil
}

func (s *SSClient) GetObject(sname string) (model.Object, error) {
	return model.Object{}, nil

}

func (s *SSClient) GetObjectMany(snames []string) (<-chan *model.Object, error) {
	return nil, nil
}

func (s *SSClient) GetObjectManyExt(snames []string) (<-chan *model.ObjectExt, error) {
	return nil, nil
}
