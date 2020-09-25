// Code generated by protoc-gen-go-grpc. DO NOT EDIT.

package baseproto

import (
	context "context"
	grpc "google.golang.org/grpc"
	codes "google.golang.org/grpc/codes"
	status "google.golang.org/grpc/status"
)

// This is a compile-time assertion to ensure that this generated file
// is compatible with the grpc package it is being compiled against.
const _ = grpc.SupportPackageIsVersion7

// ObjServiceClient is the client API for ObjService service.
//
// For semantics around ctx use and closing/ending streaming RPCs, please refer to https://pkg.go.dev/google.golang.org/grpc/?tab=doc#ClientConn.NewStream.
type ObjServiceClient interface {
	Connect(ctx context.Context, in *CmdConnect, opts ...grpc.CallOption) (*CmdConnectResponse, error)
	ConnectExt(ctx context.Context, in *CmdConnectExt, opts ...grpc.CallOption) (*CmdConnectExtResponse, error)
	LookupByName(ctx context.Context, in *CmdLookupByName, opts ...grpc.CallOption) (*CmdLookupByNameResponse, error)
	LookupByType(ctx context.Context, in *CmdNameLookupByType, opts ...grpc.CallOption) (*CmdNameLookupByTypeResponse, error)
	GetObject(ctx context.Context, in *CmdGetByName, opts ...grpc.CallOption) (*CmdGetByNameResponse, error)
	GetObjectManyByName(ctx context.Context, in *CmdGetManyByName, opts ...grpc.CallOption) (*CmdGetManyByNameResponse, error)
	GetObjectManyByNameExt(ctx context.Context, in *CmdGetManyByNameExt, opts ...grpc.CallOption) (*CmdGetManyByNameExtResponse, error)
	ChangeInitData(ctx context.Context, in *CmdChangeInitData, opts ...grpc.CallOption) (*CmdChangeInitDataResponse, error)
	ChangeInitDataExt(ctx context.Context, in *CmdChangeInitDataExt, opts ...grpc.CallOption) (*CmdChangeInitDataExtResponse, error)
}

type objServiceClient struct {
	cc grpc.ClientConnInterface
}

func NewObjServiceClient(cc grpc.ClientConnInterface) ObjServiceClient {
	return &objServiceClient{cc}
}

var objServiceConnectStreamDesc = &grpc.StreamDesc{
	StreamName: "connect",
}

func (c *objServiceClient) Connect(ctx context.Context, in *CmdConnect, opts ...grpc.CallOption) (*CmdConnectResponse, error) {
	out := new(CmdConnectResponse)
	err := c.cc.Invoke(ctx, "/org.anonymous.grpc.ObjService/connect", in, out, opts...)
	if err != nil {
		return nil, err
	}
	return out, nil
}

var objServiceConnectExtStreamDesc = &grpc.StreamDesc{
	StreamName: "connect_ext",
}

func (c *objServiceClient) ConnectExt(ctx context.Context, in *CmdConnectExt, opts ...grpc.CallOption) (*CmdConnectExtResponse, error) {
	out := new(CmdConnectExtResponse)
	err := c.cc.Invoke(ctx, "/org.anonymous.grpc.ObjService/connect_ext", in, out, opts...)
	if err != nil {
		return nil, err
	}
	return out, nil
}

var objServiceLookupByNameStreamDesc = &grpc.StreamDesc{
	StreamName: "lookup_by_name",
}

func (c *objServiceClient) LookupByName(ctx context.Context, in *CmdLookupByName, opts ...grpc.CallOption) (*CmdLookupByNameResponse, error) {
	out := new(CmdLookupByNameResponse)
	err := c.cc.Invoke(ctx, "/org.anonymous.grpc.ObjService/lookup_by_name", in, out, opts...)
	if err != nil {
		return nil, err
	}
	return out, nil
}

var objServiceLookupByTypeStreamDesc = &grpc.StreamDesc{
	StreamName: "lookup_by_type",
}

func (c *objServiceClient) LookupByType(ctx context.Context, in *CmdNameLookupByType, opts ...grpc.CallOption) (*CmdNameLookupByTypeResponse, error) {
	out := new(CmdNameLookupByTypeResponse)
	err := c.cc.Invoke(ctx, "/org.anonymous.grpc.ObjService/lookup_by_type", in, out, opts...)
	if err != nil {
		return nil, err
	}
	return out, nil
}

var objServiceGetObjectStreamDesc = &grpc.StreamDesc{
	StreamName: "get_object",
}

func (c *objServiceClient) GetObject(ctx context.Context, in *CmdGetByName, opts ...grpc.CallOption) (*CmdGetByNameResponse, error) {
	out := new(CmdGetByNameResponse)
	err := c.cc.Invoke(ctx, "/org.anonymous.grpc.ObjService/get_object", in, out, opts...)
	if err != nil {
		return nil, err
	}
	return out, nil
}

var objServiceGetObjectManyByNameStreamDesc = &grpc.StreamDesc{
	StreamName: "get_object_many_by_name",
}

func (c *objServiceClient) GetObjectManyByName(ctx context.Context, in *CmdGetManyByName, opts ...grpc.CallOption) (*CmdGetManyByNameResponse, error) {
	out := new(CmdGetManyByNameResponse)
	err := c.cc.Invoke(ctx, "/org.anonymous.grpc.ObjService/get_object_many_by_name", in, out, opts...)
	if err != nil {
		return nil, err
	}
	return out, nil
}

var objServiceGetObjectManyByNameExtStreamDesc = &grpc.StreamDesc{
	StreamName: "get_object_many_by_name_ext",
}

func (c *objServiceClient) GetObjectManyByNameExt(ctx context.Context, in *CmdGetManyByNameExt, opts ...grpc.CallOption) (*CmdGetManyByNameExtResponse, error) {
	out := new(CmdGetManyByNameExtResponse)
	err := c.cc.Invoke(ctx, "/org.anonymous.grpc.ObjService/get_object_many_by_name_ext", in, out, opts...)
	if err != nil {
		return nil, err
	}
	return out, nil
}

var objServiceChangeInitDataStreamDesc = &grpc.StreamDesc{
	StreamName: "change_init_data",
}

func (c *objServiceClient) ChangeInitData(ctx context.Context, in *CmdChangeInitData, opts ...grpc.CallOption) (*CmdChangeInitDataResponse, error) {
	out := new(CmdChangeInitDataResponse)
	err := c.cc.Invoke(ctx, "/org.anonymous.grpc.ObjService/change_init_data", in, out, opts...)
	if err != nil {
		return nil, err
	}
	return out, nil
}

var objServiceChangeInitDataExtStreamDesc = &grpc.StreamDesc{
	StreamName: "change_init_data_ext",
}

func (c *objServiceClient) ChangeInitDataExt(ctx context.Context, in *CmdChangeInitDataExt, opts ...grpc.CallOption) (*CmdChangeInitDataExtResponse, error) {
	out := new(CmdChangeInitDataExtResponse)
	err := c.cc.Invoke(ctx, "/org.anonymous.grpc.ObjService/change_init_data_ext", in, out, opts...)
	if err != nil {
		return nil, err
	}
	return out, nil
}

// ObjServiceService is the service API for ObjService service.
// Fields should be assigned to their respective handler implementations only before
// RegisterObjServiceService is called.  Any unassigned fields will result in the
// handler for that method returning an Unimplemented error.
type ObjServiceService struct {
	Connect                func(context.Context, *CmdConnect) (*CmdConnectResponse, error)
	ConnectExt             func(context.Context, *CmdConnectExt) (*CmdConnectExtResponse, error)
	LookupByName           func(context.Context, *CmdLookupByName) (*CmdLookupByNameResponse, error)
	LookupByType           func(context.Context, *CmdNameLookupByType) (*CmdNameLookupByTypeResponse, error)
	GetObject              func(context.Context, *CmdGetByName) (*CmdGetByNameResponse, error)
	GetObjectManyByName    func(context.Context, *CmdGetManyByName) (*CmdGetManyByNameResponse, error)
	GetObjectManyByNameExt func(context.Context, *CmdGetManyByNameExt) (*CmdGetManyByNameExtResponse, error)
	ChangeInitData         func(context.Context, *CmdChangeInitData) (*CmdChangeInitDataResponse, error)
	ChangeInitDataExt      func(context.Context, *CmdChangeInitDataExt) (*CmdChangeInitDataExtResponse, error)
}

func (s *ObjServiceService) connect(_ interface{}, ctx context.Context, dec func(interface{}) error, interceptor grpc.UnaryServerInterceptor) (interface{}, error) {
	in := new(CmdConnect)
	if err := dec(in); err != nil {
		return nil, err
	}
	if interceptor == nil {
		return s.Connect(ctx, in)
	}
	info := &grpc.UnaryServerInfo{
		Server:     s,
		FullMethod: "/org.anonymous.grpc.ObjService/Connect",
	}
	handler := func(ctx context.Context, req interface{}) (interface{}, error) {
		return s.Connect(ctx, req.(*CmdConnect))
	}
	return interceptor(ctx, in, info, handler)
}
func (s *ObjServiceService) connectExt(_ interface{}, ctx context.Context, dec func(interface{}) error, interceptor grpc.UnaryServerInterceptor) (interface{}, error) {
	in := new(CmdConnectExt)
	if err := dec(in); err != nil {
		return nil, err
	}
	if interceptor == nil {
		return s.ConnectExt(ctx, in)
	}
	info := &grpc.UnaryServerInfo{
		Server:     s,
		FullMethod: "/org.anonymous.grpc.ObjService/ConnectExt",
	}
	handler := func(ctx context.Context, req interface{}) (interface{}, error) {
		return s.ConnectExt(ctx, req.(*CmdConnectExt))
	}
	return interceptor(ctx, in, info, handler)
}
func (s *ObjServiceService) lookupByName(_ interface{}, ctx context.Context, dec func(interface{}) error, interceptor grpc.UnaryServerInterceptor) (interface{}, error) {
	in := new(CmdLookupByName)
	if err := dec(in); err != nil {
		return nil, err
	}
	if interceptor == nil {
		return s.LookupByName(ctx, in)
	}
	info := &grpc.UnaryServerInfo{
		Server:     s,
		FullMethod: "/org.anonymous.grpc.ObjService/LookupByName",
	}
	handler := func(ctx context.Context, req interface{}) (interface{}, error) {
		return s.LookupByName(ctx, req.(*CmdLookupByName))
	}
	return interceptor(ctx, in, info, handler)
}
func (s *ObjServiceService) lookupByType(_ interface{}, ctx context.Context, dec func(interface{}) error, interceptor grpc.UnaryServerInterceptor) (interface{}, error) {
	in := new(CmdNameLookupByType)
	if err := dec(in); err != nil {
		return nil, err
	}
	if interceptor == nil {
		return s.LookupByType(ctx, in)
	}
	info := &grpc.UnaryServerInfo{
		Server:     s,
		FullMethod: "/org.anonymous.grpc.ObjService/LookupByType",
	}
	handler := func(ctx context.Context, req interface{}) (interface{}, error) {
		return s.LookupByType(ctx, req.(*CmdNameLookupByType))
	}
	return interceptor(ctx, in, info, handler)
}
func (s *ObjServiceService) getObject(_ interface{}, ctx context.Context, dec func(interface{}) error, interceptor grpc.UnaryServerInterceptor) (interface{}, error) {
	in := new(CmdGetByName)
	if err := dec(in); err != nil {
		return nil, err
	}
	if interceptor == nil {
		return s.GetObject(ctx, in)
	}
	info := &grpc.UnaryServerInfo{
		Server:     s,
		FullMethod: "/org.anonymous.grpc.ObjService/GetObject",
	}
	handler := func(ctx context.Context, req interface{}) (interface{}, error) {
		return s.GetObject(ctx, req.(*CmdGetByName))
	}
	return interceptor(ctx, in, info, handler)
}
func (s *ObjServiceService) getObjectManyByName(_ interface{}, ctx context.Context, dec func(interface{}) error, interceptor grpc.UnaryServerInterceptor) (interface{}, error) {
	in := new(CmdGetManyByName)
	if err := dec(in); err != nil {
		return nil, err
	}
	if interceptor == nil {
		return s.GetObjectManyByName(ctx, in)
	}
	info := &grpc.UnaryServerInfo{
		Server:     s,
		FullMethod: "/org.anonymous.grpc.ObjService/GetObjectManyByName",
	}
	handler := func(ctx context.Context, req interface{}) (interface{}, error) {
		return s.GetObjectManyByName(ctx, req.(*CmdGetManyByName))
	}
	return interceptor(ctx, in, info, handler)
}
func (s *ObjServiceService) getObjectManyByNameExt(_ interface{}, ctx context.Context, dec func(interface{}) error, interceptor grpc.UnaryServerInterceptor) (interface{}, error) {
	in := new(CmdGetManyByNameExt)
	if err := dec(in); err != nil {
		return nil, err
	}
	if interceptor == nil {
		return s.GetObjectManyByNameExt(ctx, in)
	}
	info := &grpc.UnaryServerInfo{
		Server:     s,
		FullMethod: "/org.anonymous.grpc.ObjService/GetObjectManyByNameExt",
	}
	handler := func(ctx context.Context, req interface{}) (interface{}, error) {
		return s.GetObjectManyByNameExt(ctx, req.(*CmdGetManyByNameExt))
	}
	return interceptor(ctx, in, info, handler)
}
func (s *ObjServiceService) changeInitData(_ interface{}, ctx context.Context, dec func(interface{}) error, interceptor grpc.UnaryServerInterceptor) (interface{}, error) {
	in := new(CmdChangeInitData)
	if err := dec(in); err != nil {
		return nil, err
	}
	if interceptor == nil {
		return s.ChangeInitData(ctx, in)
	}
	info := &grpc.UnaryServerInfo{
		Server:     s,
		FullMethod: "/org.anonymous.grpc.ObjService/ChangeInitData",
	}
	handler := func(ctx context.Context, req interface{}) (interface{}, error) {
		return s.ChangeInitData(ctx, req.(*CmdChangeInitData))
	}
	return interceptor(ctx, in, info, handler)
}
func (s *ObjServiceService) changeInitDataExt(_ interface{}, ctx context.Context, dec func(interface{}) error, interceptor grpc.UnaryServerInterceptor) (interface{}, error) {
	in := new(CmdChangeInitDataExt)
	if err := dec(in); err != nil {
		return nil, err
	}
	if interceptor == nil {
		return s.ChangeInitDataExt(ctx, in)
	}
	info := &grpc.UnaryServerInfo{
		Server:     s,
		FullMethod: "/org.anonymous.grpc.ObjService/ChangeInitDataExt",
	}
	handler := func(ctx context.Context, req interface{}) (interface{}, error) {
		return s.ChangeInitDataExt(ctx, req.(*CmdChangeInitDataExt))
	}
	return interceptor(ctx, in, info, handler)
}

// RegisterObjServiceService registers a service implementation with a gRPC server.
func RegisterObjServiceService(s grpc.ServiceRegistrar, srv *ObjServiceService) {
	srvCopy := *srv
	if srvCopy.Connect == nil {
		srvCopy.Connect = func(context.Context, *CmdConnect) (*CmdConnectResponse, error) {
			return nil, status.Errorf(codes.Unimplemented, "method Connect not implemented")
		}
	}
	if srvCopy.ConnectExt == nil {
		srvCopy.ConnectExt = func(context.Context, *CmdConnectExt) (*CmdConnectExtResponse, error) {
			return nil, status.Errorf(codes.Unimplemented, "method ConnectExt not implemented")
		}
	}
	if srvCopy.LookupByName == nil {
		srvCopy.LookupByName = func(context.Context, *CmdLookupByName) (*CmdLookupByNameResponse, error) {
			return nil, status.Errorf(codes.Unimplemented, "method LookupByName not implemented")
		}
	}
	if srvCopy.LookupByType == nil {
		srvCopy.LookupByType = func(context.Context, *CmdNameLookupByType) (*CmdNameLookupByTypeResponse, error) {
			return nil, status.Errorf(codes.Unimplemented, "method LookupByType not implemented")
		}
	}
	if srvCopy.GetObject == nil {
		srvCopy.GetObject = func(context.Context, *CmdGetByName) (*CmdGetByNameResponse, error) {
			return nil, status.Errorf(codes.Unimplemented, "method GetObject not implemented")
		}
	}
	if srvCopy.GetObjectManyByName == nil {
		srvCopy.GetObjectManyByName = func(context.Context, *CmdGetManyByName) (*CmdGetManyByNameResponse, error) {
			return nil, status.Errorf(codes.Unimplemented, "method GetObjectManyByName not implemented")
		}
	}
	if srvCopy.GetObjectManyByNameExt == nil {
		srvCopy.GetObjectManyByNameExt = func(context.Context, *CmdGetManyByNameExt) (*CmdGetManyByNameExtResponse, error) {
			return nil, status.Errorf(codes.Unimplemented, "method GetObjectManyByNameExt not implemented")
		}
	}
	if srvCopy.ChangeInitData == nil {
		srvCopy.ChangeInitData = func(context.Context, *CmdChangeInitData) (*CmdChangeInitDataResponse, error) {
			return nil, status.Errorf(codes.Unimplemented, "method ChangeInitData not implemented")
		}
	}
	if srvCopy.ChangeInitDataExt == nil {
		srvCopy.ChangeInitDataExt = func(context.Context, *CmdChangeInitDataExt) (*CmdChangeInitDataExtResponse, error) {
			return nil, status.Errorf(codes.Unimplemented, "method ChangeInitDataExt not implemented")
		}
	}
	sd := grpc.ServiceDesc{
		ServiceName: "org.anonymous.grpc.ObjService",
		Methods: []grpc.MethodDesc{
			{
				MethodName: "connect",
				Handler:    srvCopy.connect,
			},
			{
				MethodName: "connect_ext",
				Handler:    srvCopy.connectExt,
			},
			{
				MethodName: "lookup_by_name",
				Handler:    srvCopy.lookupByName,
			},
			{
				MethodName: "lookup_by_type",
				Handler:    srvCopy.lookupByType,
			},
			{
				MethodName: "get_object",
				Handler:    srvCopy.getObject,
			},
			{
				MethodName: "get_object_many_by_name",
				Handler:    srvCopy.getObjectManyByName,
			},
			{
				MethodName: "get_object_many_by_name_ext",
				Handler:    srvCopy.getObjectManyByNameExt,
			},
			{
				MethodName: "change_init_data",
				Handler:    srvCopy.changeInitData,
			},
			{
				MethodName: "change_init_data_ext",
				Handler:    srvCopy.changeInitDataExt,
			},
		},
		Streams:  []grpc.StreamDesc{},
		Metadata: "obj_svc.proto",
	}

	s.RegisterService(&sd, nil)
}
