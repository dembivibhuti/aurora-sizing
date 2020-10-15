// Code generated by protoc-gen-go. DO NOT EDIT.
// versions:
// 	protoc-gen-go v1.25.0-devel
// 	protoc        v3.13.0
// source: cmd_trans_msg_response.proto

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

type TransMsgResponse struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	// Types that are assignable to RequestReponse:
	//	*TransMsgResponse_MsgOnFailure_
	//	*TransMsgResponse_MsgOnSuccess_
	RequestReponse isTransMsgResponse_RequestReponse `protobuf_oneof:"RequestReponse"`
}

func (x *TransMsgResponse) Reset() {
	*x = TransMsgResponse{}
	if protoimpl.UnsafeEnabled {
		mi := &file_cmd_trans_msg_response_proto_msgTypes[0]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *TransMsgResponse) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*TransMsgResponse) ProtoMessage() {}

func (x *TransMsgResponse) ProtoReflect() protoreflect.Message {
	mi := &file_cmd_trans_msg_response_proto_msgTypes[0]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use TransMsgResponse.ProtoReflect.Descriptor instead.
func (*TransMsgResponse) Descriptor() ([]byte, []int) {
	return file_cmd_trans_msg_response_proto_rawDescGZIP(), []int{0}
}

func (m *TransMsgResponse) GetRequestReponse() isTransMsgResponse_RequestReponse {
	if m != nil {
		return m.RequestReponse
	}
	return nil
}

func (x *TransMsgResponse) GetMsgOnFailure() *TransMsgResponse_MsgOnFailure {
	if x, ok := x.GetRequestReponse().(*TransMsgResponse_MsgOnFailure_); ok {
		return x.MsgOnFailure
	}
	return nil
}

func (x *TransMsgResponse) GetMsgOnSuccess() *TransMsgResponse_MsgOnSuccess {
	if x, ok := x.GetRequestReponse().(*TransMsgResponse_MsgOnSuccess_); ok {
		return x.MsgOnSuccess
	}
	return nil
}

type isTransMsgResponse_RequestReponse interface {
	isTransMsgResponse_RequestReponse()
}

type TransMsgResponse_MsgOnFailure_ struct {
	MsgOnFailure *TransMsgResponse_MsgOnFailure `protobuf:"bytes,1,opt,name=msg_on_failure,json=msgOnFailure,proto3,oneof"`
}

type TransMsgResponse_MsgOnSuccess_ struct {
	MsgOnSuccess *TransMsgResponse_MsgOnSuccess `protobuf:"bytes,2,opt,name=msg_on_success,json=msgOnSuccess,proto3,oneof"`
}

func (*TransMsgResponse_MsgOnFailure_) isTransMsgResponse_RequestReponse() {}

func (*TransMsgResponse_MsgOnSuccess_) isTransMsgResponse_RequestReponse() {}

type TransMsgResponse_MsgOnSuccess struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	// Types that are assignable to MessageResponse:
	//	*TransMsgResponse_MsgOnSuccess_SecSyncMessage_
	//	*TransMsgResponse_MsgOnSuccess_NotSecSyncMessage_
	MessageResponse isTransMsgResponse_MsgOnSuccess_MessageResponse `protobuf_oneof:"MessageResponse"`
}

func (x *TransMsgResponse_MsgOnSuccess) Reset() {
	*x = TransMsgResponse_MsgOnSuccess{}
	if protoimpl.UnsafeEnabled {
		mi := &file_cmd_trans_msg_response_proto_msgTypes[1]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *TransMsgResponse_MsgOnSuccess) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*TransMsgResponse_MsgOnSuccess) ProtoMessage() {}

func (x *TransMsgResponse_MsgOnSuccess) ProtoReflect() protoreflect.Message {
	mi := &file_cmd_trans_msg_response_proto_msgTypes[1]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use TransMsgResponse_MsgOnSuccess.ProtoReflect.Descriptor instead.
func (*TransMsgResponse_MsgOnSuccess) Descriptor() ([]byte, []int) {
	return file_cmd_trans_msg_response_proto_rawDescGZIP(), []int{0, 0}
}

func (m *TransMsgResponse_MsgOnSuccess) GetMessageResponse() isTransMsgResponse_MsgOnSuccess_MessageResponse {
	if m != nil {
		return m.MessageResponse
	}
	return nil
}

func (x *TransMsgResponse_MsgOnSuccess) GetSecSyncMessage() *TransMsgResponse_MsgOnSuccess_SecSyncMessage {
	if x, ok := x.GetMessageResponse().(*TransMsgResponse_MsgOnSuccess_SecSyncMessage_); ok {
		return x.SecSyncMessage
	}
	return nil
}

func (x *TransMsgResponse_MsgOnSuccess) GetNotSecSyncMessage() *TransMsgResponse_MsgOnSuccess_NotSecSyncMessage {
	if x, ok := x.GetMessageResponse().(*TransMsgResponse_MsgOnSuccess_NotSecSyncMessage_); ok {
		return x.NotSecSyncMessage
	}
	return nil
}

type isTransMsgResponse_MsgOnSuccess_MessageResponse interface {
	isTransMsgResponse_MsgOnSuccess_MessageResponse()
}

type TransMsgResponse_MsgOnSuccess_SecSyncMessage_ struct {
	SecSyncMessage *TransMsgResponse_MsgOnSuccess_SecSyncMessage `protobuf:"bytes,1,opt,name=sec_sync_message,json=secSyncMessage,proto3,oneof"`
}

type TransMsgResponse_MsgOnSuccess_NotSecSyncMessage_ struct {
	NotSecSyncMessage *TransMsgResponse_MsgOnSuccess_NotSecSyncMessage `protobuf:"bytes,2,opt,name=not_sec_sync_message,json=notSecSyncMessage,proto3,oneof"`
}

func (*TransMsgResponse_MsgOnSuccess_SecSyncMessage_) isTransMsgResponse_MsgOnSuccess_MessageResponse() {
}

func (*TransMsgResponse_MsgOnSuccess_NotSecSyncMessage_) isTransMsgResponse_MsgOnSuccess_MessageResponse() {
}

type TransMsgResponse_MsgOnFailure struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	// Types that are assignable to MessageResponse:
	//	*TransMsgResponse_MsgOnFailure_SecSyncMessage_
	//	*TransMsgResponse_MsgOnFailure_NotSecSyncMessage_
	MessageResponse isTransMsgResponse_MsgOnFailure_MessageResponse `protobuf_oneof:"MessageResponse"`
}

func (x *TransMsgResponse_MsgOnFailure) Reset() {
	*x = TransMsgResponse_MsgOnFailure{}
	if protoimpl.UnsafeEnabled {
		mi := &file_cmd_trans_msg_response_proto_msgTypes[2]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *TransMsgResponse_MsgOnFailure) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*TransMsgResponse_MsgOnFailure) ProtoMessage() {}

func (x *TransMsgResponse_MsgOnFailure) ProtoReflect() protoreflect.Message {
	mi := &file_cmd_trans_msg_response_proto_msgTypes[2]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use TransMsgResponse_MsgOnFailure.ProtoReflect.Descriptor instead.
func (*TransMsgResponse_MsgOnFailure) Descriptor() ([]byte, []int) {
	return file_cmd_trans_msg_response_proto_rawDescGZIP(), []int{0, 1}
}

func (m *TransMsgResponse_MsgOnFailure) GetMessageResponse() isTransMsgResponse_MsgOnFailure_MessageResponse {
	if m != nil {
		return m.MessageResponse
	}
	return nil
}

func (x *TransMsgResponse_MsgOnFailure) GetSecSyncMessage() *TransMsgResponse_MsgOnFailure_SecSyncMessage {
	if x, ok := x.GetMessageResponse().(*TransMsgResponse_MsgOnFailure_SecSyncMessage_); ok {
		return x.SecSyncMessage
	}
	return nil
}

func (x *TransMsgResponse_MsgOnFailure) GetNotSecSyncMessage() *TransMsgResponse_MsgOnFailure_NotSecSyncMessage {
	if x, ok := x.GetMessageResponse().(*TransMsgResponse_MsgOnFailure_NotSecSyncMessage_); ok {
		return x.NotSecSyncMessage
	}
	return nil
}

type isTransMsgResponse_MsgOnFailure_MessageResponse interface {
	isTransMsgResponse_MsgOnFailure_MessageResponse()
}

type TransMsgResponse_MsgOnFailure_SecSyncMessage_ struct {
	SecSyncMessage *TransMsgResponse_MsgOnFailure_SecSyncMessage `protobuf:"bytes,1,opt,name=sec_sync_message,json=secSyncMessage,proto3,oneof"`
}

type TransMsgResponse_MsgOnFailure_NotSecSyncMessage_ struct {
	NotSecSyncMessage *TransMsgResponse_MsgOnFailure_NotSecSyncMessage `protobuf:"bytes,2,opt,name=not_sec_sync_message,json=notSecSyncMessage,proto3,oneof"`
}

func (*TransMsgResponse_MsgOnFailure_SecSyncMessage_) isTransMsgResponse_MsgOnFailure_MessageResponse() {
}

func (*TransMsgResponse_MsgOnFailure_NotSecSyncMessage_) isTransMsgResponse_MsgOnFailure_MessageResponse() {
}

type TransMsgResponse_MsgOnSuccess_SecSyncMessage struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	MessageSize      uint32 `protobuf:"varint,1,opt,name=message_size,json=messageSize,proto3" json:"message_size,omitempty"`
	Ack              int32  `protobuf:"varint,2,opt,name=ack,proto3" json:"ack,omitempty"`
	SourceDbId       int32  `protobuf:"varint,3,opt,name=source_db_id,json=sourceDbId,proto3" json:"source_db_id,omitempty"`
	SourceTxnId      int32  `protobuf:"varint,4,opt,name=source_txn_id,json=sourceTxnId,proto3" json:"source_txn_id,omitempty"`
	DestinationTxnId int32  `protobuf:"varint,5,opt,name=destination_txn_id,json=destinationTxnId,proto3" json:"destination_txn_id,omitempty"`
}

func (x *TransMsgResponse_MsgOnSuccess_SecSyncMessage) Reset() {
	*x = TransMsgResponse_MsgOnSuccess_SecSyncMessage{}
	if protoimpl.UnsafeEnabled {
		mi := &file_cmd_trans_msg_response_proto_msgTypes[3]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *TransMsgResponse_MsgOnSuccess_SecSyncMessage) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*TransMsgResponse_MsgOnSuccess_SecSyncMessage) ProtoMessage() {}

func (x *TransMsgResponse_MsgOnSuccess_SecSyncMessage) ProtoReflect() protoreflect.Message {
	mi := &file_cmd_trans_msg_response_proto_msgTypes[3]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use TransMsgResponse_MsgOnSuccess_SecSyncMessage.ProtoReflect.Descriptor instead.
func (*TransMsgResponse_MsgOnSuccess_SecSyncMessage) Descriptor() ([]byte, []int) {
	return file_cmd_trans_msg_response_proto_rawDescGZIP(), []int{0, 0, 0}
}

func (x *TransMsgResponse_MsgOnSuccess_SecSyncMessage) GetMessageSize() uint32 {
	if x != nil {
		return x.MessageSize
	}
	return 0
}

func (x *TransMsgResponse_MsgOnSuccess_SecSyncMessage) GetAck() int32 {
	if x != nil {
		return x.Ack
	}
	return 0
}

func (x *TransMsgResponse_MsgOnSuccess_SecSyncMessage) GetSourceDbId() int32 {
	if x != nil {
		return x.SourceDbId
	}
	return 0
}

func (x *TransMsgResponse_MsgOnSuccess_SecSyncMessage) GetSourceTxnId() int32 {
	if x != nil {
		return x.SourceTxnId
	}
	return 0
}

func (x *TransMsgResponse_MsgOnSuccess_SecSyncMessage) GetDestinationTxnId() int32 {
	if x != nil {
		return x.DestinationTxnId
	}
	return 0
}

type TransMsgResponse_MsgOnSuccess_NotSecSyncMessage struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	MessageSize uint32 `protobuf:"varint,1,opt,name=message_size,json=messageSize,proto3" json:"message_size,omitempty"`
	Ack         int32  `protobuf:"varint,2,opt,name=ack,proto3" json:"ack,omitempty"`
	TxnId       int32  `protobuf:"varint,3,opt,name=txn_id,json=txnId,proto3" json:"txn_id,omitempty"`
}

func (x *TransMsgResponse_MsgOnSuccess_NotSecSyncMessage) Reset() {
	*x = TransMsgResponse_MsgOnSuccess_NotSecSyncMessage{}
	if protoimpl.UnsafeEnabled {
		mi := &file_cmd_trans_msg_response_proto_msgTypes[4]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *TransMsgResponse_MsgOnSuccess_NotSecSyncMessage) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*TransMsgResponse_MsgOnSuccess_NotSecSyncMessage) ProtoMessage() {}

func (x *TransMsgResponse_MsgOnSuccess_NotSecSyncMessage) ProtoReflect() protoreflect.Message {
	mi := &file_cmd_trans_msg_response_proto_msgTypes[4]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use TransMsgResponse_MsgOnSuccess_NotSecSyncMessage.ProtoReflect.Descriptor instead.
func (*TransMsgResponse_MsgOnSuccess_NotSecSyncMessage) Descriptor() ([]byte, []int) {
	return file_cmd_trans_msg_response_proto_rawDescGZIP(), []int{0, 0, 1}
}

func (x *TransMsgResponse_MsgOnSuccess_NotSecSyncMessage) GetMessageSize() uint32 {
	if x != nil {
		return x.MessageSize
	}
	return 0
}

func (x *TransMsgResponse_MsgOnSuccess_NotSecSyncMessage) GetAck() int32 {
	if x != nil {
		return x.Ack
	}
	return 0
}

func (x *TransMsgResponse_MsgOnSuccess_NotSecSyncMessage) GetTxnId() int32 {
	if x != nil {
		return x.TxnId
	}
	return 0
}

type TransMsgResponse_MsgOnFailure_SecSyncMessage struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	MessageSize      uint32    `protobuf:"varint,1,opt,name=message_size,json=messageSize,proto3" json:"message_size,omitempty"`
	ErrorMessageType ErrorType `protobuf:"varint,2,opt,name=error_message_type,json=errorMessageType,proto3,enum=org.anonymous.grpc.ErrorType" json:"error_message_type,omitempty"`
	SourceDbId       int32     `protobuf:"varint,3,opt,name=source_db_id,json=sourceDbId,proto3" json:"source_db_id,omitempty"`
	SourceTxnId      int32     `protobuf:"varint,4,opt,name=source_txn_id,json=sourceTxnId,proto3" json:"source_txn_id,omitempty"`
	ErrorMessage     string    `protobuf:"bytes,5,opt,name=error_message,json=errorMessage,proto3" json:"error_message,omitempty"`
}

func (x *TransMsgResponse_MsgOnFailure_SecSyncMessage) Reset() {
	*x = TransMsgResponse_MsgOnFailure_SecSyncMessage{}
	if protoimpl.UnsafeEnabled {
		mi := &file_cmd_trans_msg_response_proto_msgTypes[5]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *TransMsgResponse_MsgOnFailure_SecSyncMessage) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*TransMsgResponse_MsgOnFailure_SecSyncMessage) ProtoMessage() {}

func (x *TransMsgResponse_MsgOnFailure_SecSyncMessage) ProtoReflect() protoreflect.Message {
	mi := &file_cmd_trans_msg_response_proto_msgTypes[5]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use TransMsgResponse_MsgOnFailure_SecSyncMessage.ProtoReflect.Descriptor instead.
func (*TransMsgResponse_MsgOnFailure_SecSyncMessage) Descriptor() ([]byte, []int) {
	return file_cmd_trans_msg_response_proto_rawDescGZIP(), []int{0, 1, 0}
}

func (x *TransMsgResponse_MsgOnFailure_SecSyncMessage) GetMessageSize() uint32 {
	if x != nil {
		return x.MessageSize
	}
	return 0
}

func (x *TransMsgResponse_MsgOnFailure_SecSyncMessage) GetErrorMessageType() ErrorType {
	if x != nil {
		return x.ErrorMessageType
	}
	return ErrorType_ERR_INVALID_ARGUMENTS
}

func (x *TransMsgResponse_MsgOnFailure_SecSyncMessage) GetSourceDbId() int32 {
	if x != nil {
		return x.SourceDbId
	}
	return 0
}

func (x *TransMsgResponse_MsgOnFailure_SecSyncMessage) GetSourceTxnId() int32 {
	if x != nil {
		return x.SourceTxnId
	}
	return 0
}

func (x *TransMsgResponse_MsgOnFailure_SecSyncMessage) GetErrorMessage() string {
	if x != nil {
		return x.ErrorMessage
	}
	return ""
}

type TransMsgResponse_MsgOnFailure_NotSecSyncMessage struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	MessageSize      uint32    `protobuf:"varint,1,opt,name=message_size,json=messageSize,proto3" json:"message_size,omitempty"`
	Ack              int32     `protobuf:"varint,2,opt,name=ack,proto3" json:"ack,omitempty"`
	TxnId            int32     `protobuf:"varint,3,opt,name=txn_id,json=txnId,proto3" json:"txn_id,omitempty"`
	ErrorMessageType ErrorType `protobuf:"varint,4,opt,name=error_message_type,json=errorMessageType,proto3,enum=org.anonymous.grpc.ErrorType" json:"error_message_type,omitempty"`
	ErrorMessage     string    `protobuf:"bytes,5,opt,name=error_message,json=errorMessage,proto3" json:"error_message,omitempty"`
}

func (x *TransMsgResponse_MsgOnFailure_NotSecSyncMessage) Reset() {
	*x = TransMsgResponse_MsgOnFailure_NotSecSyncMessage{}
	if protoimpl.UnsafeEnabled {
		mi := &file_cmd_trans_msg_response_proto_msgTypes[6]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *TransMsgResponse_MsgOnFailure_NotSecSyncMessage) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*TransMsgResponse_MsgOnFailure_NotSecSyncMessage) ProtoMessage() {}

func (x *TransMsgResponse_MsgOnFailure_NotSecSyncMessage) ProtoReflect() protoreflect.Message {
	mi := &file_cmd_trans_msg_response_proto_msgTypes[6]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use TransMsgResponse_MsgOnFailure_NotSecSyncMessage.ProtoReflect.Descriptor instead.
func (*TransMsgResponse_MsgOnFailure_NotSecSyncMessage) Descriptor() ([]byte, []int) {
	return file_cmd_trans_msg_response_proto_rawDescGZIP(), []int{0, 1, 1}
}

func (x *TransMsgResponse_MsgOnFailure_NotSecSyncMessage) GetMessageSize() uint32 {
	if x != nil {
		return x.MessageSize
	}
	return 0
}

func (x *TransMsgResponse_MsgOnFailure_NotSecSyncMessage) GetAck() int32 {
	if x != nil {
		return x.Ack
	}
	return 0
}

func (x *TransMsgResponse_MsgOnFailure_NotSecSyncMessage) GetTxnId() int32 {
	if x != nil {
		return x.TxnId
	}
	return 0
}

func (x *TransMsgResponse_MsgOnFailure_NotSecSyncMessage) GetErrorMessageType() ErrorType {
	if x != nil {
		return x.ErrorMessageType
	}
	return ErrorType_ERR_INVALID_ARGUMENTS
}

func (x *TransMsgResponse_MsgOnFailure_NotSecSyncMessage) GetErrorMessage() string {
	if x != nil {
		return x.ErrorMessage
	}
	return ""
}

var File_cmd_trans_msg_response_proto protoreflect.FileDescriptor

var file_cmd_trans_msg_response_proto_rawDesc = []byte{
	0x0a, 0x1c, 0x63, 0x6d, 0x64, 0x5f, 0x74, 0x72, 0x61, 0x6e, 0x73, 0x5f, 0x6d, 0x73, 0x67, 0x5f,
	0x72, 0x65, 0x73, 0x70, 0x6f, 0x6e, 0x73, 0x65, 0x2e, 0x70, 0x72, 0x6f, 0x74, 0x6f, 0x12, 0x12,
	0x6f, 0x72, 0x67, 0x2e, 0x61, 0x6e, 0x6f, 0x6e, 0x79, 0x6d, 0x6f, 0x75, 0x73, 0x2e, 0x67, 0x72,
	0x70, 0x63, 0x1a, 0x10, 0x65, 0x72, 0x72, 0x6f, 0x72, 0x5f, 0x74, 0x79, 0x70, 0x65, 0x2e, 0x70,
	0x72, 0x6f, 0x74, 0x6f, 0x22, 0xcd, 0x0b, 0x0a, 0x10, 0x54, 0x72, 0x61, 0x6e, 0x73, 0x4d, 0x73,
	0x67, 0x52, 0x65, 0x73, 0x70, 0x6f, 0x6e, 0x73, 0x65, 0x12, 0x59, 0x0a, 0x0e, 0x6d, 0x73, 0x67,
	0x5f, 0x6f, 0x6e, 0x5f, 0x66, 0x61, 0x69, 0x6c, 0x75, 0x72, 0x65, 0x18, 0x01, 0x20, 0x01, 0x28,
	0x0b, 0x32, 0x31, 0x2e, 0x6f, 0x72, 0x67, 0x2e, 0x61, 0x6e, 0x6f, 0x6e, 0x79, 0x6d, 0x6f, 0x75,
	0x73, 0x2e, 0x67, 0x72, 0x70, 0x63, 0x2e, 0x54, 0x72, 0x61, 0x6e, 0x73, 0x4d, 0x73, 0x67, 0x52,
	0x65, 0x73, 0x70, 0x6f, 0x6e, 0x73, 0x65, 0x2e, 0x4d, 0x73, 0x67, 0x4f, 0x6e, 0x46, 0x61, 0x69,
	0x6c, 0x75, 0x72, 0x65, 0x48, 0x00, 0x52, 0x0c, 0x6d, 0x73, 0x67, 0x4f, 0x6e, 0x46, 0x61, 0x69,
	0x6c, 0x75, 0x72, 0x65, 0x12, 0x59, 0x0a, 0x0e, 0x6d, 0x73, 0x67, 0x5f, 0x6f, 0x6e, 0x5f, 0x73,
	0x75, 0x63, 0x63, 0x65, 0x73, 0x73, 0x18, 0x02, 0x20, 0x01, 0x28, 0x0b, 0x32, 0x31, 0x2e, 0x6f,
	0x72, 0x67, 0x2e, 0x61, 0x6e, 0x6f, 0x6e, 0x79, 0x6d, 0x6f, 0x75, 0x73, 0x2e, 0x67, 0x72, 0x70,
	0x63, 0x2e, 0x54, 0x72, 0x61, 0x6e, 0x73, 0x4d, 0x73, 0x67, 0x52, 0x65, 0x73, 0x70, 0x6f, 0x6e,
	0x73, 0x65, 0x2e, 0x4d, 0x73, 0x67, 0x4f, 0x6e, 0x53, 0x75, 0x63, 0x63, 0x65, 0x73, 0x73, 0x48,
	0x00, 0x52, 0x0c, 0x6d, 0x73, 0x67, 0x4f, 0x6e, 0x53, 0x75, 0x63, 0x63, 0x65, 0x73, 0x73, 0x1a,
	0xa4, 0x04, 0x0a, 0x0c, 0x4d, 0x73, 0x67, 0x4f, 0x6e, 0x53, 0x75, 0x63, 0x63, 0x65, 0x73, 0x73,
	0x12, 0x6c, 0x0a, 0x10, 0x73, 0x65, 0x63, 0x5f, 0x73, 0x79, 0x6e, 0x63, 0x5f, 0x6d, 0x65, 0x73,
	0x73, 0x61, 0x67, 0x65, 0x18, 0x01, 0x20, 0x01, 0x28, 0x0b, 0x32, 0x40, 0x2e, 0x6f, 0x72, 0x67,
	0x2e, 0x61, 0x6e, 0x6f, 0x6e, 0x79, 0x6d, 0x6f, 0x75, 0x73, 0x2e, 0x67, 0x72, 0x70, 0x63, 0x2e,
	0x54, 0x72, 0x61, 0x6e, 0x73, 0x4d, 0x73, 0x67, 0x52, 0x65, 0x73, 0x70, 0x6f, 0x6e, 0x73, 0x65,
	0x2e, 0x4d, 0x73, 0x67, 0x4f, 0x6e, 0x53, 0x75, 0x63, 0x63, 0x65, 0x73, 0x73, 0x2e, 0x53, 0x65,
	0x63, 0x53, 0x79, 0x6e, 0x63, 0x4d, 0x65, 0x73, 0x73, 0x61, 0x67, 0x65, 0x48, 0x00, 0x52, 0x0e,
	0x73, 0x65, 0x63, 0x53, 0x79, 0x6e, 0x63, 0x4d, 0x65, 0x73, 0x73, 0x61, 0x67, 0x65, 0x12, 0x76,
	0x0a, 0x14, 0x6e, 0x6f, 0x74, 0x5f, 0x73, 0x65, 0x63, 0x5f, 0x73, 0x79, 0x6e, 0x63, 0x5f, 0x6d,
	0x65, 0x73, 0x73, 0x61, 0x67, 0x65, 0x18, 0x02, 0x20, 0x01, 0x28, 0x0b, 0x32, 0x43, 0x2e, 0x6f,
	0x72, 0x67, 0x2e, 0x61, 0x6e, 0x6f, 0x6e, 0x79, 0x6d, 0x6f, 0x75, 0x73, 0x2e, 0x67, 0x72, 0x70,
	0x63, 0x2e, 0x54, 0x72, 0x61, 0x6e, 0x73, 0x4d, 0x73, 0x67, 0x52, 0x65, 0x73, 0x70, 0x6f, 0x6e,
	0x73, 0x65, 0x2e, 0x4d, 0x73, 0x67, 0x4f, 0x6e, 0x53, 0x75, 0x63, 0x63, 0x65, 0x73, 0x73, 0x2e,
	0x4e, 0x6f, 0x74, 0x53, 0x65, 0x63, 0x53, 0x79, 0x6e, 0x63, 0x4d, 0x65, 0x73, 0x73, 0x61, 0x67,
	0x65, 0x48, 0x00, 0x52, 0x11, 0x6e, 0x6f, 0x74, 0x53, 0x65, 0x63, 0x53, 0x79, 0x6e, 0x63, 0x4d,
	0x65, 0x73, 0x73, 0x61, 0x67, 0x65, 0x1a, 0xb9, 0x01, 0x0a, 0x0e, 0x53, 0x65, 0x63, 0x53, 0x79,
	0x6e, 0x63, 0x4d, 0x65, 0x73, 0x73, 0x61, 0x67, 0x65, 0x12, 0x21, 0x0a, 0x0c, 0x6d, 0x65, 0x73,
	0x73, 0x61, 0x67, 0x65, 0x5f, 0x73, 0x69, 0x7a, 0x65, 0x18, 0x01, 0x20, 0x01, 0x28, 0x0d, 0x52,
	0x0b, 0x6d, 0x65, 0x73, 0x73, 0x61, 0x67, 0x65, 0x53, 0x69, 0x7a, 0x65, 0x12, 0x10, 0x0a, 0x03,
	0x61, 0x63, 0x6b, 0x18, 0x02, 0x20, 0x01, 0x28, 0x05, 0x52, 0x03, 0x61, 0x63, 0x6b, 0x12, 0x20,
	0x0a, 0x0c, 0x73, 0x6f, 0x75, 0x72, 0x63, 0x65, 0x5f, 0x64, 0x62, 0x5f, 0x69, 0x64, 0x18, 0x03,
	0x20, 0x01, 0x28, 0x05, 0x52, 0x0a, 0x73, 0x6f, 0x75, 0x72, 0x63, 0x65, 0x44, 0x62, 0x49, 0x64,
	0x12, 0x22, 0x0a, 0x0d, 0x73, 0x6f, 0x75, 0x72, 0x63, 0x65, 0x5f, 0x74, 0x78, 0x6e, 0x5f, 0x69,
	0x64, 0x18, 0x04, 0x20, 0x01, 0x28, 0x05, 0x52, 0x0b, 0x73, 0x6f, 0x75, 0x72, 0x63, 0x65, 0x54,
	0x78, 0x6e, 0x49, 0x64, 0x12, 0x2c, 0x0a, 0x12, 0x64, 0x65, 0x73, 0x74, 0x69, 0x6e, 0x61, 0x74,
	0x69, 0x6f, 0x6e, 0x5f, 0x74, 0x78, 0x6e, 0x5f, 0x69, 0x64, 0x18, 0x05, 0x20, 0x01, 0x28, 0x05,
	0x52, 0x10, 0x64, 0x65, 0x73, 0x74, 0x69, 0x6e, 0x61, 0x74, 0x69, 0x6f, 0x6e, 0x54, 0x78, 0x6e,
	0x49, 0x64, 0x1a, 0x5f, 0x0a, 0x11, 0x4e, 0x6f, 0x74, 0x53, 0x65, 0x63, 0x53, 0x79, 0x6e, 0x63,
	0x4d, 0x65, 0x73, 0x73, 0x61, 0x67, 0x65, 0x12, 0x21, 0x0a, 0x0c, 0x6d, 0x65, 0x73, 0x73, 0x61,
	0x67, 0x65, 0x5f, 0x73, 0x69, 0x7a, 0x65, 0x18, 0x01, 0x20, 0x01, 0x28, 0x0d, 0x52, 0x0b, 0x6d,
	0x65, 0x73, 0x73, 0x61, 0x67, 0x65, 0x53, 0x69, 0x7a, 0x65, 0x12, 0x10, 0x0a, 0x03, 0x61, 0x63,
	0x6b, 0x18, 0x02, 0x20, 0x01, 0x28, 0x05, 0x52, 0x03, 0x61, 0x63, 0x6b, 0x12, 0x15, 0x0a, 0x06,
	0x74, 0x78, 0x6e, 0x5f, 0x69, 0x64, 0x18, 0x03, 0x20, 0x01, 0x28, 0x05, 0x52, 0x05, 0x74, 0x78,
	0x6e, 0x49, 0x64, 0x42, 0x11, 0x0a, 0x0f, 0x4d, 0x65, 0x73, 0x73, 0x61, 0x67, 0x65, 0x52, 0x65,
	0x73, 0x70, 0x6f, 0x6e, 0x73, 0x65, 0x1a, 0xc9, 0x05, 0x0a, 0x0c, 0x4d, 0x73, 0x67, 0x4f, 0x6e,
	0x46, 0x61, 0x69, 0x6c, 0x75, 0x72, 0x65, 0x12, 0x6c, 0x0a, 0x10, 0x73, 0x65, 0x63, 0x5f, 0x73,
	0x79, 0x6e, 0x63, 0x5f, 0x6d, 0x65, 0x73, 0x73, 0x61, 0x67, 0x65, 0x18, 0x01, 0x20, 0x01, 0x28,
	0x0b, 0x32, 0x40, 0x2e, 0x6f, 0x72, 0x67, 0x2e, 0x61, 0x6e, 0x6f, 0x6e, 0x79, 0x6d, 0x6f, 0x75,
	0x73, 0x2e, 0x67, 0x72, 0x70, 0x63, 0x2e, 0x54, 0x72, 0x61, 0x6e, 0x73, 0x4d, 0x73, 0x67, 0x52,
	0x65, 0x73, 0x70, 0x6f, 0x6e, 0x73, 0x65, 0x2e, 0x4d, 0x73, 0x67, 0x4f, 0x6e, 0x46, 0x61, 0x69,
	0x6c, 0x75, 0x72, 0x65, 0x2e, 0x53, 0x65, 0x63, 0x53, 0x79, 0x6e, 0x63, 0x4d, 0x65, 0x73, 0x73,
	0x61, 0x67, 0x65, 0x48, 0x00, 0x52, 0x0e, 0x73, 0x65, 0x63, 0x53, 0x79, 0x6e, 0x63, 0x4d, 0x65,
	0x73, 0x73, 0x61, 0x67, 0x65, 0x12, 0x76, 0x0a, 0x14, 0x6e, 0x6f, 0x74, 0x5f, 0x73, 0x65, 0x63,
	0x5f, 0x73, 0x79, 0x6e, 0x63, 0x5f, 0x6d, 0x65, 0x73, 0x73, 0x61, 0x67, 0x65, 0x18, 0x02, 0x20,
	0x01, 0x28, 0x0b, 0x32, 0x43, 0x2e, 0x6f, 0x72, 0x67, 0x2e, 0x61, 0x6e, 0x6f, 0x6e, 0x79, 0x6d,
	0x6f, 0x75, 0x73, 0x2e, 0x67, 0x72, 0x70, 0x63, 0x2e, 0x54, 0x72, 0x61, 0x6e, 0x73, 0x4d, 0x73,
	0x67, 0x52, 0x65, 0x73, 0x70, 0x6f, 0x6e, 0x73, 0x65, 0x2e, 0x4d, 0x73, 0x67, 0x4f, 0x6e, 0x46,
	0x61, 0x69, 0x6c, 0x75, 0x72, 0x65, 0x2e, 0x4e, 0x6f, 0x74, 0x53, 0x65, 0x63, 0x53, 0x79, 0x6e,
	0x63, 0x4d, 0x65, 0x73, 0x73, 0x61, 0x67, 0x65, 0x48, 0x00, 0x52, 0x11, 0x6e, 0x6f, 0x74, 0x53,
	0x65, 0x63, 0x53, 0x79, 0x6e, 0x63, 0x4d, 0x65, 0x73, 0x73, 0x61, 0x67, 0x65, 0x1a, 0xeb, 0x01,
	0x0a, 0x0e, 0x53, 0x65, 0x63, 0x53, 0x79, 0x6e, 0x63, 0x4d, 0x65, 0x73, 0x73, 0x61, 0x67, 0x65,
	0x12, 0x21, 0x0a, 0x0c, 0x6d, 0x65, 0x73, 0x73, 0x61, 0x67, 0x65, 0x5f, 0x73, 0x69, 0x7a, 0x65,
	0x18, 0x01, 0x20, 0x01, 0x28, 0x0d, 0x52, 0x0b, 0x6d, 0x65, 0x73, 0x73, 0x61, 0x67, 0x65, 0x53,
	0x69, 0x7a, 0x65, 0x12, 0x4b, 0x0a, 0x12, 0x65, 0x72, 0x72, 0x6f, 0x72, 0x5f, 0x6d, 0x65, 0x73,
	0x73, 0x61, 0x67, 0x65, 0x5f, 0x74, 0x79, 0x70, 0x65, 0x18, 0x02, 0x20, 0x01, 0x28, 0x0e, 0x32,
	0x1d, 0x2e, 0x6f, 0x72, 0x67, 0x2e, 0x61, 0x6e, 0x6f, 0x6e, 0x79, 0x6d, 0x6f, 0x75, 0x73, 0x2e,
	0x67, 0x72, 0x70, 0x63, 0x2e, 0x45, 0x72, 0x72, 0x6f, 0x72, 0x54, 0x79, 0x70, 0x65, 0x52, 0x10,
	0x65, 0x72, 0x72, 0x6f, 0x72, 0x4d, 0x65, 0x73, 0x73, 0x61, 0x67, 0x65, 0x54, 0x79, 0x70, 0x65,
	0x12, 0x20, 0x0a, 0x0c, 0x73, 0x6f, 0x75, 0x72, 0x63, 0x65, 0x5f, 0x64, 0x62, 0x5f, 0x69, 0x64,
	0x18, 0x03, 0x20, 0x01, 0x28, 0x05, 0x52, 0x0a, 0x73, 0x6f, 0x75, 0x72, 0x63, 0x65, 0x44, 0x62,
	0x49, 0x64, 0x12, 0x22, 0x0a, 0x0d, 0x73, 0x6f, 0x75, 0x72, 0x63, 0x65, 0x5f, 0x74, 0x78, 0x6e,
	0x5f, 0x69, 0x64, 0x18, 0x04, 0x20, 0x01, 0x28, 0x05, 0x52, 0x0b, 0x73, 0x6f, 0x75, 0x72, 0x63,
	0x65, 0x54, 0x78, 0x6e, 0x49, 0x64, 0x12, 0x23, 0x0a, 0x0d, 0x65, 0x72, 0x72, 0x6f, 0x72, 0x5f,
	0x6d, 0x65, 0x73, 0x73, 0x61, 0x67, 0x65, 0x18, 0x05, 0x20, 0x01, 0x28, 0x09, 0x52, 0x0c, 0x65,
	0x72, 0x72, 0x6f, 0x72, 0x4d, 0x65, 0x73, 0x73, 0x61, 0x67, 0x65, 0x1a, 0xd1, 0x01, 0x0a, 0x11,
	0x4e, 0x6f, 0x74, 0x53, 0x65, 0x63, 0x53, 0x79, 0x6e, 0x63, 0x4d, 0x65, 0x73, 0x73, 0x61, 0x67,
	0x65, 0x12, 0x21, 0x0a, 0x0c, 0x6d, 0x65, 0x73, 0x73, 0x61, 0x67, 0x65, 0x5f, 0x73, 0x69, 0x7a,
	0x65, 0x18, 0x01, 0x20, 0x01, 0x28, 0x0d, 0x52, 0x0b, 0x6d, 0x65, 0x73, 0x73, 0x61, 0x67, 0x65,
	0x53, 0x69, 0x7a, 0x65, 0x12, 0x10, 0x0a, 0x03, 0x61, 0x63, 0x6b, 0x18, 0x02, 0x20, 0x01, 0x28,
	0x05, 0x52, 0x03, 0x61, 0x63, 0x6b, 0x12, 0x15, 0x0a, 0x06, 0x74, 0x78, 0x6e, 0x5f, 0x69, 0x64,
	0x18, 0x03, 0x20, 0x01, 0x28, 0x05, 0x52, 0x05, 0x74, 0x78, 0x6e, 0x49, 0x64, 0x12, 0x4b, 0x0a,
	0x12, 0x65, 0x72, 0x72, 0x6f, 0x72, 0x5f, 0x6d, 0x65, 0x73, 0x73, 0x61, 0x67, 0x65, 0x5f, 0x74,
	0x79, 0x70, 0x65, 0x18, 0x04, 0x20, 0x01, 0x28, 0x0e, 0x32, 0x1d, 0x2e, 0x6f, 0x72, 0x67, 0x2e,
	0x61, 0x6e, 0x6f, 0x6e, 0x79, 0x6d, 0x6f, 0x75, 0x73, 0x2e, 0x67, 0x72, 0x70, 0x63, 0x2e, 0x45,
	0x72, 0x72, 0x6f, 0x72, 0x54, 0x79, 0x70, 0x65, 0x52, 0x10, 0x65, 0x72, 0x72, 0x6f, 0x72, 0x4d,
	0x65, 0x73, 0x73, 0x61, 0x67, 0x65, 0x54, 0x79, 0x70, 0x65, 0x12, 0x23, 0x0a, 0x0d, 0x65, 0x72,
	0x72, 0x6f, 0x72, 0x5f, 0x6d, 0x65, 0x73, 0x73, 0x61, 0x67, 0x65, 0x18, 0x05, 0x20, 0x01, 0x28,
	0x09, 0x52, 0x0c, 0x65, 0x72, 0x72, 0x6f, 0x72, 0x4d, 0x65, 0x73, 0x73, 0x61, 0x67, 0x65, 0x42,
	0x11, 0x0a, 0x0f, 0x4d, 0x65, 0x73, 0x73, 0x61, 0x67, 0x65, 0x52, 0x65, 0x73, 0x70, 0x6f, 0x6e,
	0x73, 0x65, 0x42, 0x10, 0x0a, 0x0e, 0x52, 0x65, 0x71, 0x75, 0x65, 0x73, 0x74, 0x52, 0x65, 0x70,
	0x6f, 0x6e, 0x73, 0x65, 0x42, 0x3c, 0x50, 0x01, 0x5a, 0x38, 0x67, 0x69, 0x74, 0x68, 0x75, 0x62,
	0x2e, 0x63, 0x6f, 0x6d, 0x2f, 0x73, 0x6f, 0x6d, 0x6e, 0x61, 0x74, 0x68, 0x36, 0x37, 0x36, 0x34,
	0x33, 0x2f, 0x61, 0x75, 0x72, 0x6f, 0x72, 0x61, 0x2d, 0x73, 0x69, 0x7a, 0x69, 0x6e, 0x67, 0x2f,
	0x63, 0x6c, 0x69, 0x65, 0x6e, 0x74, 0x67, 0x6f, 0x2f, 0x62, 0x61, 0x73, 0x65, 0x70, 0x72, 0x6f,
	0x74, 0x6f, 0x62, 0x06, 0x70, 0x72, 0x6f, 0x74, 0x6f, 0x33,
}

var (
	file_cmd_trans_msg_response_proto_rawDescOnce sync.Once
	file_cmd_trans_msg_response_proto_rawDescData = file_cmd_trans_msg_response_proto_rawDesc
)

func file_cmd_trans_msg_response_proto_rawDescGZIP() []byte {
	file_cmd_trans_msg_response_proto_rawDescOnce.Do(func() {
		file_cmd_trans_msg_response_proto_rawDescData = protoimpl.X.CompressGZIP(file_cmd_trans_msg_response_proto_rawDescData)
	})
	return file_cmd_trans_msg_response_proto_rawDescData
}

var file_cmd_trans_msg_response_proto_msgTypes = make([]protoimpl.MessageInfo, 7)
var file_cmd_trans_msg_response_proto_goTypes = []interface{}{
	(*TransMsgResponse)(nil),                                // 0: org.anonymous.grpc.TransMsgResponse
	(*TransMsgResponse_MsgOnSuccess)(nil),                   // 1: org.anonymous.grpc.TransMsgResponse.MsgOnSuccess
	(*TransMsgResponse_MsgOnFailure)(nil),                   // 2: org.anonymous.grpc.TransMsgResponse.MsgOnFailure
	(*TransMsgResponse_MsgOnSuccess_SecSyncMessage)(nil),    // 3: org.anonymous.grpc.TransMsgResponse.MsgOnSuccess.SecSyncMessage
	(*TransMsgResponse_MsgOnSuccess_NotSecSyncMessage)(nil), // 4: org.anonymous.grpc.TransMsgResponse.MsgOnSuccess.NotSecSyncMessage
	(*TransMsgResponse_MsgOnFailure_SecSyncMessage)(nil),    // 5: org.anonymous.grpc.TransMsgResponse.MsgOnFailure.SecSyncMessage
	(*TransMsgResponse_MsgOnFailure_NotSecSyncMessage)(nil), // 6: org.anonymous.grpc.TransMsgResponse.MsgOnFailure.NotSecSyncMessage
	(ErrorType)(0), // 7: org.anonymous.grpc.ErrorType
}
var file_cmd_trans_msg_response_proto_depIdxs = []int32{
	2, // 0: org.anonymous.grpc.TransMsgResponse.msg_on_failure:type_name -> org.anonymous.grpc.TransMsgResponse.MsgOnFailure
	1, // 1: org.anonymous.grpc.TransMsgResponse.msg_on_success:type_name -> org.anonymous.grpc.TransMsgResponse.MsgOnSuccess
	3, // 2: org.anonymous.grpc.TransMsgResponse.MsgOnSuccess.sec_sync_message:type_name -> org.anonymous.grpc.TransMsgResponse.MsgOnSuccess.SecSyncMessage
	4, // 3: org.anonymous.grpc.TransMsgResponse.MsgOnSuccess.not_sec_sync_message:type_name -> org.anonymous.grpc.TransMsgResponse.MsgOnSuccess.NotSecSyncMessage
	5, // 4: org.anonymous.grpc.TransMsgResponse.MsgOnFailure.sec_sync_message:type_name -> org.anonymous.grpc.TransMsgResponse.MsgOnFailure.SecSyncMessage
	6, // 5: org.anonymous.grpc.TransMsgResponse.MsgOnFailure.not_sec_sync_message:type_name -> org.anonymous.grpc.TransMsgResponse.MsgOnFailure.NotSecSyncMessage
	7, // 6: org.anonymous.grpc.TransMsgResponse.MsgOnFailure.SecSyncMessage.error_message_type:type_name -> org.anonymous.grpc.ErrorType
	7, // 7: org.anonymous.grpc.TransMsgResponse.MsgOnFailure.NotSecSyncMessage.error_message_type:type_name -> org.anonymous.grpc.ErrorType
	8, // [8:8] is the sub-list for method output_type
	8, // [8:8] is the sub-list for method input_type
	8, // [8:8] is the sub-list for extension type_name
	8, // [8:8] is the sub-list for extension extendee
	0, // [0:8] is the sub-list for field type_name
}

func init() { file_cmd_trans_msg_response_proto_init() }
func file_cmd_trans_msg_response_proto_init() {
	if File_cmd_trans_msg_response_proto != nil {
		return
	}
	file_error_type_proto_init()
	if !protoimpl.UnsafeEnabled {
		file_cmd_trans_msg_response_proto_msgTypes[0].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*TransMsgResponse); i {
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
		file_cmd_trans_msg_response_proto_msgTypes[1].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*TransMsgResponse_MsgOnSuccess); i {
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
		file_cmd_trans_msg_response_proto_msgTypes[2].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*TransMsgResponse_MsgOnFailure); i {
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
		file_cmd_trans_msg_response_proto_msgTypes[3].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*TransMsgResponse_MsgOnSuccess_SecSyncMessage); i {
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
		file_cmd_trans_msg_response_proto_msgTypes[4].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*TransMsgResponse_MsgOnSuccess_NotSecSyncMessage); i {
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
		file_cmd_trans_msg_response_proto_msgTypes[5].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*TransMsgResponse_MsgOnFailure_SecSyncMessage); i {
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
		file_cmd_trans_msg_response_proto_msgTypes[6].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*TransMsgResponse_MsgOnFailure_NotSecSyncMessage); i {
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
	file_cmd_trans_msg_response_proto_msgTypes[0].OneofWrappers = []interface{}{
		(*TransMsgResponse_MsgOnFailure_)(nil),
		(*TransMsgResponse_MsgOnSuccess_)(nil),
	}
	file_cmd_trans_msg_response_proto_msgTypes[1].OneofWrappers = []interface{}{
		(*TransMsgResponse_MsgOnSuccess_SecSyncMessage_)(nil),
		(*TransMsgResponse_MsgOnSuccess_NotSecSyncMessage_)(nil),
	}
	file_cmd_trans_msg_response_proto_msgTypes[2].OneofWrappers = []interface{}{
		(*TransMsgResponse_MsgOnFailure_SecSyncMessage_)(nil),
		(*TransMsgResponse_MsgOnFailure_NotSecSyncMessage_)(nil),
	}
	type x struct{}
	out := protoimpl.TypeBuilder{
		File: protoimpl.DescBuilder{
			GoPackagePath: reflect.TypeOf(x{}).PkgPath(),
			RawDescriptor: file_cmd_trans_msg_response_proto_rawDesc,
			NumEnums:      0,
			NumMessages:   7,
			NumExtensions: 0,
			NumServices:   0,
		},
		GoTypes:           file_cmd_trans_msg_response_proto_goTypes,
		DependencyIndexes: file_cmd_trans_msg_response_proto_depIdxs,
		MessageInfos:      file_cmd_trans_msg_response_proto_msgTypes,
	}.Build()
	File_cmd_trans_msg_response_proto = out.File
	file_cmd_trans_msg_response_proto_rawDesc = nil
	file_cmd_trans_msg_response_proto_goTypes = nil
	file_cmd_trans_msg_response_proto_depIdxs = nil
}
