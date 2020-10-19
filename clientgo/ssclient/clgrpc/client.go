package clgrpc

import (
	"context"
	"fmt"
	"io"
	"log"
	"net/http"
	"strconv"
	"time"

	"google.golang.org/grpc"

	gprom "github.com/grpc-ecosystem/go-grpc-prometheus"
	"github.com/prometheus/client_golang/prometheus"
	"github.com/prometheus/client_golang/prometheus/promhttp"
	pb "github.com/somnath67643/aurora-sizing/clientgo/baseproto"
	"github.com/somnath67643/aurora-sizing/clientgo/ssclient/model"
)

type SSClient struct {
	client  pb.ObjServiceClient
	conn    *grpc.ClientConn
	metrics *model.Metrics
}

func timeTaken(msg string, t time.Time) {
	//fmt.Println(msg, " : ", time.Since(t))
}

func mustGetConn(addr string) *grpc.ClientConn {
	var opts []grpc.DialOption
	opts = append(opts, grpc.WithInsecure())
	opts = append(opts, grpc.WithUnaryInterceptor(gprom.UnaryClientInterceptor))
	opts = append(opts, grpc.WithStreamInterceptor(gprom.StreamClientInterceptor))
	gprom.EnableClientHandlingTimeHistogram()
	//	opts = append(opts, grpc.WithBlock())
	conn, err := grpc.Dial(addr, opts...)
	if err != nil {
		log.Fatal(err)
	}
	return conn
}

func NewSSClient(addr string) *SSClient {
	conn := mustGetConn(addr)
	return &SSClient{
		client:  pb.NewObjServiceClient(conn),
		conn:    conn,
		metrics: model.NewMetrics(),
	}
}

func (s *SSClient) Close() {
	s.conn.Close()
}

func (s *SSClient) Init() {
	// don't need any initialization here
}

func (s *SSClient) EnableMetrics(addr string) {
	s.metrics.Register(prometheus.DefaultRegisterer)
	http.Handle("/metrics", promhttp.HandlerFor(
		prometheus.DefaultGatherer,
		promhttp.HandlerOpts{
			EnableOpenMetrics: true,
		},
	))
	go func() {
		log.Fatal(http.ListenAndServe(addr, nil)) // Start the http server for prometheus
	}()
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
		guageTimer := prometheus.NewTimer(prometheus.ObserverFunc(func(v float64) {
			s.metrics.GlookupByName.With(prometheus.Labels{"number": strconv.Itoa(int(nr))}).Set(float64(v))
		})) // TODO: add more labels
		defer guageTimer.ObserveDuration()

		for {
			resp, err := strmCl.Recv()
			if err == io.EOF { // also takes care of io.EOF
				break
			}
			if err != nil {
				log.Println(err)
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
		guageTimer := prometheus.NewTimer(prometheus.ObserverFunc(func(v float64) {
			s.metrics.GlookupByType.With(prometheus.Labels{"number": strconv.Itoa(int(nr))}).Set(float64(v))
		})) // TODO: add more labels
		defer guageTimer.ObserveDuration()
		for {
			resp, err := strmCl.Recv()
			if err == io.EOF {
				break
			}
			if err != nil {
				log.Println(err)
				break
			}
			ch <- resp.SecurityName
		}
		close(ch)
	}()
	return ch, nil
}

func (s *SSClient) GetObject(sname string) (*model.Object, error) {
	ctx := context.Background()
	guageTime := prometheus.NewTimer(prometheus.ObserverFunc(s.metrics.GgetObject.Set))
	defer guageTime.ObserveDuration()
	resp, err := s.client.GetObject(ctx, &pb.CmdGetByName{SecurityName: sname})
	if err != nil {
		return nil, err
	}
	fmt.Printf("GetObject : %s | Response : %d\n", sname, resp.Status)
	var obj *model.Object
	switch v := resp.Response.(type) {
	case *pb.CmdGetByNameResponse_ErrorType:
		return nil, fmt.Errorf("Could not get Object")

	case *pb.CmdGetByNameResponse_Security:
		obj = &model.Object{Mem: v.Security}
	}

	return obj, nil
}

func (s *SSClient) GetObjectMany(snames []string) (<-chan *model.Object, error) {
	ctx := context.Background()
	strmCl, err := s.client.GetObjectManyByNameStream(ctx, &pb.CmdGetManyByName{
		SecurityCount: int32(len(snames)),
		SecurityNames: snames,
	})
	if err != nil {
		return nil, err
	}
	ch := make(chan *model.Object)
	go func() {
		defer timeTaken("GetObjectMany", time.Now())
		for {
			resp, err := strmCl.Recv()
			if err == io.EOF {
				break
			}
			if err != nil {
				log.Println(err)
				break
			}
			v := resp.GetMsgOnSuccess()
			if v == nil {
				continue
			}
			ch <- &model.Object{Mem: v.Mem}
		}
		close(ch)
	}()
	return ch, nil
}

func (s *SSClient) GetObjectManyExt(snames []string) (<-chan *model.ObjectExt, error) {
	ctx := context.Background()
	strmCl, err := s.client.GetObjectManyByNameExtStream(ctx, &pb.CmdGetManyByNameExt{
		Count:         uint32(len(snames)),
		SecurityNames: snames,
	})
	if err != nil {
		return nil, err
	}
	ch := make(chan *model.ObjectExt)
	go func() {
		defer timeTaken("GetObjectManyExt", time.Now())
		for {
			resp, err := strmCl.Recv()
			if err == io.EOF {
				break
			}
			if err != nil {
				log.Println(err)
				break
			}
			v := resp.GetMsgOnSuccess()
			fmt.Printf("Has Succeeded: %t | Metadata: %s \n", v.HasSucceeded, v.Metadata)
			if v == nil {
				continue
			}

			obj := &model.ObjectExt{
				Mem:      v.GetMem(),
				Metadata: convertGrpcMetadataToModelMetadata(v.Metadata),
			}
			ch <- obj
		}
		close(ch)

	}()
	return ch, nil
}

func (s *SSClient) GetObjectExt(sname string) (*model.ObjectExt, error) {
	ctx := context.Background()
	resp, err := s.client.GetObjectExt(ctx, &pb.CmdGetByNameExt{
		SecurityName: sname,
	})
	if err != nil {
		return nil, err
	}
	resp.GetMsgOnSuccess().GetMetadata()
	return &model.ObjectExt{
		Mem: resp.GetMsgOnSuccess().GetMem(),
	}, nil
}

func (s *SSClient) BeginTxn() model.Transactor {
	return &GrpcTransactor{
		transClient: pb.NewTransactionServiceClient(s.conn),
		buffer:      make([]*pb.CmdTransactionRequest, 0),
	}
}

/*
func (s *SSClient) InsertRecord(metadata *pb.Metadata, cmdType pb.CmdType, nr int32) (*pb.CmdInsertResponse, error) {
	ctx := context.Background()
	resp, err := s.transClient.InsertRecord(ctx, &pb.CmdInsert{
		MsgSize:  nr,
		MsgType:  cmdType,
		Metadata: metadata,
		MemSize:  5,
		Mem:      []byte{},
	})
	if err != nil {
		return nil, err
	}
	return resp, nil
}

func (s *SSClient) RenameRecord(oldMetadata *pb.Metadata, newMetadata *pb.Metadata, cmdType pb.CmdType, nr int32) (*pb.CmdRenameResponse, error) {
	ctx := context.Background()
	resp, err := s.transClient.RenameRecord(ctx, &pb.CmdRename{
		MsgSize:     nr,
		MsgType:     cmdType,
		OldMetadata: oldMetadata,
		NewMetadata: newMetadata,
	})
	if err != nil {
		return nil, err
	}
	return resp, nil
}

func (s *SSClient) UpdateRecord(oldMetadata *pb.Metadata, newMetadata *pb.Metadata, cmdType pb.CmdType, nr int32) (*pb.CmdUpdateResponse, error) {
	ctx := context.Background()
	resp, err := s.transClient.UpdateRecord(ctx, &pb.CmdUpdate{
		MsgSize:     nr,
		MsgType:     cmdType,
		OldMetadata: oldMetadata,
		NewMetadata: newMetadata,
		MemSize:     5,
		Mem:         []byte{},
	})
	if err != nil {
		return nil, err
	}
	return resp, nil
}

func (s *SSClient) DeleteRecord(metadata *pb.Metadata, cmdType pb.CmdType, nr int32, ignoreflags int32) (*pb.CmdDeleteResponse, error) {
	ctx := context.Background()
	resp, err := s.transClient.DeleteRecord(ctx, &pb.CmdDelete{
		MsgSize:       nr,
		MsgType:       cmdType,
		Metadata:      metadata,
		IgnoreErrFlag: ignoreflags,
	})
	if err != nil {
		return nil, err
	}
	return resp, nil
}
*/
