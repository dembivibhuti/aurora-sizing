// Code generated by protoc-gen-go. DO NOT EDIT.
// versions:
// 	protoc-gen-go v1.25.0-devel
// 	protoc        v3.13.0
// source: cmd_connect.proto

package baseproto

import (
	protoreflect "google.golang.org/protobuf/reflect/protoreflect"
	protoimpl "google.golang.org/protobuf/runtime/protoimpl"
	reflect "reflect"
	sync "sync"
)

const (
	// Verify that this generated code is sufficiently up-to-date.
	_ = protoimpl.EnforceVersion(20 - protoimpl.MinVersion)
	// Verify that runtime/protoimpl is sufficiently up-to-date.
	_ = protoimpl.EnforceVersion(protoimpl.MaxVersion - 20)
)

type CmdConnect struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	MsgSize        int32   `protobuf:"varint,1,opt,name=msg_size,json=msgSize,proto3" json:"msg_size,omitempty"`
	MessageType    CmdType `protobuf:"varint,2,opt,name=message_type,json=messageType,proto3,enum=org.anonymous.grpc.CmdType" json:"message_type,omitempty"`
	AppName        string  `protobuf:"bytes,3,opt,name=app_name,json=appName,proto3" json:"app_name,omitempty"`
	UserName       string  `protobuf:"bytes,4,opt,name=user_name,json=userName,proto3" json:"user_name,omitempty"`
	ClientVersion  int32   `protobuf:"varint,5,opt,name=client_version,json=clientVersion,proto3" json:"client_version,omitempty"`
	ClientRevision int32   `protobuf:"varint,6,opt,name=client_revision,json=clientRevision,proto3" json:"client_revision,omitempty"`
}

func (x *CmdConnect) Reset() {
	*x = CmdConnect{}
	if protoimpl.UnsafeEnabled {
		mi := &file_cmd_connect_proto_msgTypes[0]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *CmdConnect) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*CmdConnect) ProtoMessage() {}

func (x *CmdConnect) ProtoReflect() protoreflect.Message {
	mi := &file_cmd_connect_proto_msgTypes[0]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use CmdConnect.ProtoReflect.Descriptor instead.
func (*CmdConnect) Descriptor() ([]byte, []int) {
	return file_cmd_connect_proto_rawDescGZIP(), []int{0}
}

func (x *CmdConnect) GetMsgSize() int32 {
	if x != nil {
		return x.MsgSize
	}
	return 0
}

func (x *CmdConnect) GetMessageType() CmdType {
	if x != nil {
		return x.MessageType
	}
	return CmdType_CMD_UNDEFINED
}

func (x *CmdConnect) GetAppName() string {
	if x != nil {
		return x.AppName
	}
	return ""
}

func (x *CmdConnect) GetUserName() string {
	if x != nil {
		return x.UserName
	}
	return ""
}

func (x *CmdConnect) GetClientVersion() int32 {
	if x != nil {
		return x.ClientVersion
	}
	return 0
}

func (x *CmdConnect) GetClientRevision() int32 {
	if x != nil {
		return x.ClientRevision
	}
	return 0
}

type CmdConnectResponse struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	MsgSize     uint32 `protobuf:"varint,1,opt,name=msg_size,json=msgSize,proto3" json:"msg_size,omitempty"`
	VerAndRev   int32  `protobuf:"varint,2,opt,name=ver_and_rev,json=verAndRev,proto3" json:"ver_and_rev,omitempty"`
	FeatureFlag uint32 `protobuf:"varint,3,opt,name=feature_flag,json=featureFlag,proto3" json:"feature_flag,omitempty"`
}

func (x *CmdConnectResponse) Reset() {
	*x = CmdConnectResponse{}
	if protoimpl.UnsafeEnabled {
		mi := &file_cmd_connect_proto_msgTypes[1]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *CmdConnectResponse) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*CmdConnectResponse) ProtoMessage() {}

func (x *CmdConnectResponse) ProtoReflect() protoreflect.Message {
	mi := &file_cmd_connect_proto_msgTypes[1]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use CmdConnectResponse.ProtoReflect.Descriptor instead.
func (*CmdConnectResponse) Descriptor() ([]byte, []int) {
	return file_cmd_connect_proto_rawDescGZIP(), []int{1}
}

func (x *CmdConnectResponse) GetMsgSize() uint32 {
	if x != nil {
		return x.MsgSize
	}
	return 0
}

func (x *CmdConnectResponse) GetVerAndRev() int32 {
	if x != nil {
		return x.VerAndRev
	}
	return 0
}

func (x *CmdConnectResponse) GetFeatureFlag() uint32 {
	if x != nil {
		return x.FeatureFlag
	}
	return 0
}

var File_cmd_connect_proto protoreflect.FileDescriptor

var file_cmd_connect_proto_rawDesc = []byte{
	0x0a, 0x11, 0x63, 0x6d, 0x64, 0x5f, 0x63, 0x6f, 0x6e, 0x6e, 0x65, 0x63, 0x74, 0x2e, 0x70, 0x72,
	0x6f, 0x74, 0x6f, 0x12, 0x12, 0x6f, 0x72, 0x67, 0x2e, 0x61, 0x6e, 0x6f, 0x6e, 0x79, 0x6d, 0x6f,
	0x75, 0x73, 0x2e, 0x67, 0x72, 0x70, 0x63, 0x1a, 0x0e, 0x63, 0x6d, 0x64, 0x5f, 0x74, 0x79, 0x70,
	0x65, 0x2e, 0x70, 0x72, 0x6f, 0x74, 0x6f, 0x22, 0xef, 0x01, 0x0a, 0x0a, 0x43, 0x6d, 0x64, 0x43,
	0x6f, 0x6e, 0x6e, 0x65, 0x63, 0x74, 0x12, 0x19, 0x0a, 0x08, 0x6d, 0x73, 0x67, 0x5f, 0x73, 0x69,
	0x7a, 0x65, 0x18, 0x01, 0x20, 0x01, 0x28, 0x05, 0x52, 0x07, 0x6d, 0x73, 0x67, 0x53, 0x69, 0x7a,
	0x65, 0x12, 0x3e, 0x0a, 0x0c, 0x6d, 0x65, 0x73, 0x73, 0x61, 0x67, 0x65, 0x5f, 0x74, 0x79, 0x70,
	0x65, 0x18, 0x02, 0x20, 0x01, 0x28, 0x0e, 0x32, 0x1b, 0x2e, 0x6f, 0x72, 0x67, 0x2e, 0x61, 0x6e,
	0x6f, 0x6e, 0x79, 0x6d, 0x6f, 0x75, 0x73, 0x2e, 0x67, 0x72, 0x70, 0x63, 0x2e, 0x43, 0x6d, 0x64,
	0x54, 0x79, 0x70, 0x65, 0x52, 0x0b, 0x6d, 0x65, 0x73, 0x73, 0x61, 0x67, 0x65, 0x54, 0x79, 0x70,
	0x65, 0x12, 0x19, 0x0a, 0x08, 0x61, 0x70, 0x70, 0x5f, 0x6e, 0x61, 0x6d, 0x65, 0x18, 0x03, 0x20,
	0x01, 0x28, 0x09, 0x52, 0x07, 0x61, 0x70, 0x70, 0x4e, 0x61, 0x6d, 0x65, 0x12, 0x1b, 0x0a, 0x09,
	0x75, 0x73, 0x65, 0x72, 0x5f, 0x6e, 0x61, 0x6d, 0x65, 0x18, 0x04, 0x20, 0x01, 0x28, 0x09, 0x52,
	0x08, 0x75, 0x73, 0x65, 0x72, 0x4e, 0x61, 0x6d, 0x65, 0x12, 0x25, 0x0a, 0x0e, 0x63, 0x6c, 0x69,
	0x65, 0x6e, 0x74, 0x5f, 0x76, 0x65, 0x72, 0x73, 0x69, 0x6f, 0x6e, 0x18, 0x05, 0x20, 0x01, 0x28,
	0x05, 0x52, 0x0d, 0x63, 0x6c, 0x69, 0x65, 0x6e, 0x74, 0x56, 0x65, 0x72, 0x73, 0x69, 0x6f, 0x6e,
	0x12, 0x27, 0x0a, 0x0f, 0x63, 0x6c, 0x69, 0x65, 0x6e, 0x74, 0x5f, 0x72, 0x65, 0x76, 0x69, 0x73,
	0x69, 0x6f, 0x6e, 0x18, 0x06, 0x20, 0x01, 0x28, 0x05, 0x52, 0x0e, 0x63, 0x6c, 0x69, 0x65, 0x6e,
	0x74, 0x52, 0x65, 0x76, 0x69, 0x73, 0x69, 0x6f, 0x6e, 0x22, 0x72, 0x0a, 0x12, 0x43, 0x6d, 0x64,
	0x43, 0x6f, 0x6e, 0x6e, 0x65, 0x63, 0x74, 0x52, 0x65, 0x73, 0x70, 0x6f, 0x6e, 0x73, 0x65, 0x12,
	0x19, 0x0a, 0x08, 0x6d, 0x73, 0x67, 0x5f, 0x73, 0x69, 0x7a, 0x65, 0x18, 0x01, 0x20, 0x01, 0x28,
	0x0d, 0x52, 0x07, 0x6d, 0x73, 0x67, 0x53, 0x69, 0x7a, 0x65, 0x12, 0x1e, 0x0a, 0x0b, 0x76, 0x65,
	0x72, 0x5f, 0x61, 0x6e, 0x64, 0x5f, 0x72, 0x65, 0x76, 0x18, 0x02, 0x20, 0x01, 0x28, 0x05, 0x52,
	0x09, 0x76, 0x65, 0x72, 0x41, 0x6e, 0x64, 0x52, 0x65, 0x76, 0x12, 0x21, 0x0a, 0x0c, 0x66, 0x65,
	0x61, 0x74, 0x75, 0x72, 0x65, 0x5f, 0x66, 0x6c, 0x61, 0x67, 0x18, 0x03, 0x20, 0x01, 0x28, 0x0d,
	0x52, 0x0b, 0x66, 0x65, 0x61, 0x74, 0x75, 0x72, 0x65, 0x46, 0x6c, 0x61, 0x67, 0x42, 0x3c, 0x50,
	0x01, 0x5a, 0x38, 0x67, 0x69, 0x74, 0x68, 0x75, 0x62, 0x2e, 0x63, 0x6f, 0x6d, 0x2f, 0x73, 0x6f,
	0x6d, 0x6e, 0x61, 0x74, 0x68, 0x36, 0x37, 0x36, 0x34, 0x33, 0x2f, 0x61, 0x75, 0x72, 0x6f, 0x72,
	0x61, 0x2d, 0x73, 0x69, 0x7a, 0x69, 0x6e, 0x67, 0x2f, 0x63, 0x6c, 0x69, 0x65, 0x6e, 0x74, 0x67,
	0x6f, 0x2f, 0x62, 0x61, 0x73, 0x65, 0x70, 0x72, 0x6f, 0x74, 0x6f, 0x62, 0x06, 0x70, 0x72, 0x6f,
	0x74, 0x6f, 0x33,
}

var (
	file_cmd_connect_proto_rawDescOnce sync.Once
	file_cmd_connect_proto_rawDescData = file_cmd_connect_proto_rawDesc
)

func file_cmd_connect_proto_rawDescGZIP() []byte {
	file_cmd_connect_proto_rawDescOnce.Do(func() {
		file_cmd_connect_proto_rawDescData = protoimpl.X.CompressGZIP(file_cmd_connect_proto_rawDescData)
	})
	return file_cmd_connect_proto_rawDescData
}

var file_cmd_connect_proto_msgTypes = make([]protoimpl.MessageInfo, 2)
var file_cmd_connect_proto_goTypes = []interface{}{
	(*CmdConnect)(nil),         // 0: org.anonymous.grpc.CmdConnect
	(*CmdConnectResponse)(nil), // 1: org.anonymous.grpc.CmdConnectResponse
	(CmdType)(0),               // 2: org.anonymous.grpc.CmdType
}
var file_cmd_connect_proto_depIdxs = []int32{
	2, // 0: org.anonymous.grpc.CmdConnect.message_type:type_name -> org.anonymous.grpc.CmdType
	1, // [1:1] is the sub-list for method output_type
	1, // [1:1] is the sub-list for method input_type
	1, // [1:1] is the sub-list for extension type_name
	1, // [1:1] is the sub-list for extension extendee
	0, // [0:1] is the sub-list for field type_name
}

func init() { file_cmd_connect_proto_init() }
func file_cmd_connect_proto_init() {
	if File_cmd_connect_proto != nil {
		return
	}
	file_cmd_type_proto_init()
	if !protoimpl.UnsafeEnabled {
		file_cmd_connect_proto_msgTypes[0].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*CmdConnect); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
		file_cmd_connect_proto_msgTypes[1].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*CmdConnectResponse); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
	}
	type x struct{}
	out := protoimpl.TypeBuilder{
		File: protoimpl.DescBuilder{
			GoPackagePath: reflect.TypeOf(x{}).PkgPath(),
			RawDescriptor: file_cmd_connect_proto_rawDesc,
			NumEnums:      0,
			NumMessages:   2,
			NumExtensions: 0,
			NumServices:   0,
		},
		GoTypes:           file_cmd_connect_proto_goTypes,
		DependencyIndexes: file_cmd_connect_proto_depIdxs,
		MessageInfos:      file_cmd_connect_proto_msgTypes,
	}.Build()
	File_cmd_connect_proto = out.File
	file_cmd_connect_proto_rawDesc = nil
	file_cmd_connect_proto_goTypes = nil
	file_cmd_connect_proto_depIdxs = nil
}
