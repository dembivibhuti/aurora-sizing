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
	objSrvr := &objServer{}
	pb.RegisterObjServiceServer(grpcServer, objSrvr)
	//Connect:                      connect,
	//GetObject:                    getObject,
	//LookupByNameStream:           lookupByNameStream,
	//GetObjectManyByNameStream:    getObjectManyByNameStream,
	//GetObjectManyByNameExtStream: getObjectManyByNameExtStream,
	//})
	grpcServer.Serve(lis)
}

type objServer struct {
	pb.UnimplementedObjServiceServer
}

func (o *objServer) Connect(_ context.Context, in *pb.CmdConnect) (*pb.CmdConnectResponse, error) {
	log.Println(in.AppName)
	resp := &pb.CmdConnectResponse{
		MsgSize:     111,
		VerAndRev:   222,
		FeatureFlag: 333,
	}
	return resp, nil
}

func (o *objServer) ConnectExt(_ context.Context, _ *pb.CmdConnectExt) (*pb.CmdConnectExtResponse, error) {
	panic("not implemented") // TODO: Implement
}

func (o *objServer) LookupByName(_ context.Context, _ *pb.CmdLookupByName) (*pb.CmdLookupByNameResponse, error) {
	panic("not implemented") // TODO: Implement
}

func (o *objServer) LookupByNameStream(in *pb.CmdLookupByName, stream pb.ObjService_LookupByNameStreamServer) error {
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

func (o *objServer) LookupByType(_ context.Context, _ *pb.CmdNameLookupByType) (*pb.CmdNameLookupByTypeResponse, error) {
	panic("not implemented") // TODO: Implement
}

func (o *objServer) LookupByTypeStream(_ *pb.CmdNameLookupByType, _ pb.ObjService_LookupByTypeStreamServer) error {
	panic("not implemented") // TODO: Implement
}

func (o *objServer) GetObject(_ context.Context, in *pb.CmdGetByName) (*pb.CmdGetByNameResponse, error) {
	resp := &pb.CmdGetByNameResponse{
		Response: &pb.CmdGetByNameResponse_Security{Security: getRandomBytes(rand.Intn(32 * 1024))},
	}
	return resp, nil
}

func (o *objServer) GetObjectExt(_ context.Context, _ *pb.CmdGetByNameExt) (*pb.CmdGetByNameExtResponse, error) {
	panic("not implemented") // TODO: Implement
}

func (o *objServer) GetObjectManyByName(_ context.Context, _ *pb.CmdGetManyByName) (*pb.CmdGetManyByNameResponse, error) {
	panic("not implemented") // TODO: Implement
}

func (o *objServer) GetObjectManyByNameStream(in *pb.CmdGetManyByName, stream pb.ObjService_GetObjectManyByNameStreamServer) error {
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

func (o *objServer) GetObjectManyByNameExt(_ context.Context, _ *pb.CmdGetManyByNameExt) (*pb.CmdGetManyByNameExtResponse, error) {
	panic("not implemented") // TODO: Implement
}

func (o *objServer) GetObjectManyByNameExtStream(in *pb.CmdGetManyByNameExt, stream pb.ObjService_GetObjectManyByNameExtStreamServer) error {
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

func (o *objServer) ChangeInitData(_ context.Context, _ *pb.CmdChangeInitData) (*pb.CmdChangeInitDataResponse, error) {
	panic("not implemented") // TODO: Implement
}

func (o *objServer) ChangeInitDataExt(_ context.Context, _ *pb.CmdChangeInitDataExt) (*pb.CmdChangeInitDataExtResponse, error) {
	panic("not implemented") // TODO: Implement
}

func (o *objServer) DeleteData(_ context.Context, _ *pb.CmdDeleteData) (*pb.CmdDeleteDataResponse, error) {
	panic("not implemented") // TODO: Implement
}

func (o *objServer) RenameData(_ context.Context, _ *pb.CmdRenameData) (*pb.CmdRenameDataResponse, error) {
	panic("not implemented") // TODO: Implement
}

func (o *objServer) mustEmbedUnimplementedObjServiceServer() {
	panic("not implemented") // TODO: Implement
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
