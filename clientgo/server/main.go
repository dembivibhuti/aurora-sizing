package main

import (
	"context"
	"flag"
	"fmt"
	"log"
	"math/rand"
	"net"

	pb "github.com/somnath67643/aurora-sizing/clientgo/baseproto"
	"google.golang.org/grpc"
)

var (
	port = flag.Int("port", 8080, "The server port")
)

func main() {
	flag.Parse()
	lis, err := net.Listen("tcp", fmt.Sprintf(":%d", *port))
	if err != nil {
		log.Fatal(err)
	}
	var opts []grpc.ServerOption
	grpcServer := grpc.NewServer(opts...)
	pb.RegisterObjServiceService(grpcServer, &pb.ObjServiceService{
		Connect:                      connect,
		GetObject:                    getObject,
		LookupByNameStream:           lookupByNameStream,
		GetObjectManyByNameStream:    getObjectManyByNameStream,
		GetObjectManyByNameExtStream: getObjectManyByNameExtStream,
	})
	grpcServer.Serve(lis)
}

func connect(ctx context.Context, in *pb.CmdConnect) (*pb.CmdConnectResponse, error) {
	log.Println(in.AppName)
	resp := &pb.CmdConnectResponse{
		MsgSize:     111,
		VerAndRev:   222,
		FeatureFlag: 333,
	}
	return resp, nil
}

func getObject(ctx context.Context, in *pb.CmdGetByName) (*pb.CmdGetByNameResponse, error) {
	resp := &pb.CmdGetByNameResponse{
		Response: &pb.CmdGetByNameResponse_Security{Security: getRandomBytes(rand.Intn(32 * 1024))},
	}
	return resp, nil
}

func lookupByNameStream(in *pb.CmdLookupByName, stream pb.ObjService_LookupByNameStreamServer) error {
	for i := 0; i < int(in.Count); i++ {
		resp := &pb.CmdLookupByNameResponseStream{
			SecurityName: fmt.Sprintf("%s_%d", in.SecurityNamePrefix, i),
		}
		if err := stream.Send(resp); err != nil {
			return err
		}
	}
	return nil
}

func lookupByTypeStream(in *pb.CmdNameLookupByType, stream pb.ObjService_LookupByTypeStreamServer) error {
	for i := 0; i < int(in.Count); i++ {
		resp := &pb.CmdNameLookupByTypeResponseStream{
			SecurityName: fmt.Sprintf("%s_%d", in.SecurityNamePrefix, i),
		}
		if err := stream.Send(resp); err != nil {
			return err
		}
	}
	return nil
}

func getObjectManyByNameStream(in *pb.CmdGetManyByName, stream pb.ObjService_GetObjectManyByNameStreamServer) error {
	for _, v := range in.SecurityNames {
		imsg := &pb.CmdGetManyByNameResponseStream_MsgOnSuccess{
			Mem: getRandomBytes(rand.Intn(1024 * 64)),
		}
		msg := &pb.CmdGetManyByNameResponseStream_MsgOnSuccess_{
			MsgOnSuccess: imsg,
		}
		resp := &pb.CmdGetManyByNameResponseStream{
			MessageResponse: msg,
		}
		log.Println("Sending Response for: ", v)
		if err := stream.Send(resp); err != nil {
			return err
		}
	}
	return nil
}

func getObjectManyByNameExtStream(in *pb.CmdGetManyByNameExt, stream pb.ObjService_GetObjectManyByNameExtStreamServer) error {
	for _, v := range in.SecurityNames {
		imsg := &pb.CmdGetManyByNameExtResponseStream_MsgOnSuccess{
			HasSucceeded: true,
			Metadata:     nil,
			Mem:          getRandomBytes(rand.Intn(64 * 1024)),
		}
		msg := &pb.CmdGetManyByNameExtResponseStream_MsgOnSuccess_{
			MsgOnSuccess: imsg,
		}
		log.Println("Sending Response (EXT) for: ", v)
		resp := &pb.CmdGetManyByNameExtResponseStream{
			RequestResponse: msg,
		}
		if err := stream.Send(resp); err != nil {
			return err
		}
	}
	return nil
}
