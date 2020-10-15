package clgrpc

import (
	pb "github.com/somnath67643/aurora-sizing/clientgo/baseproto"
	"github.com/somnath67643/aurora-sizing/clientgo/ssclient/model"
)

// GrpcTransactor implements the model.Transactor interface
type GrpcTransactor struct {
	transClient pb.TransactionServiceClient
	buffer      []*pb.CmdTransactionRequest
}

func (g *GrpcTransactor) Insert(metadata *model.Metadata, mem []byte) error {
	ins := &pb.CmdInsert{
		Metadata: convertModelMetadataToGrpcMetadata(metadata),
		Mem:      mem,
	}
	insMsg := &pb.CmdTransactionRequest_Insert{
		Insert: ins,
	}

	req := &pb.CmdTransactionRequest{
		TransSeq:       pb.TransType_INSERT,
		MessageRequest: insMsg,
	}

	g.buffer = append(g.buffer, req)
	return nil
}

func (g *GrpcTransactor) Rename(oldMetadata *model.Metadata, newMetadata *model.Metadata) error {
	rnm := &pb.CmdRenameData{
		OldMetadata: convertModelMetadataToGrpcMetadata(oldMetadata),
		NewMetadata: convertModelMetadataToGrpcMetadata(newMetadata),
	}
	rnmMsg := &pb.CmdTransactionRequest_Rename{
		Rename: rnm,
	}
	req := &pb.CmdTransactionRequest{
		TransSeq:       pb.TransType_RENAME,
		MessageRequest: rnmMsg,
	}
	g.buffer = append(g.buffer, req)
	return nil
}

func (g *GrpcTransactor) Update(oldMetadata *model.Metadata, newMetadata *model.Metadata, mem []byte) error {
	upd := &pb.CmdUpdate{
		OldMetadata: convertModelMetadataToGrpcMetadata(oldMetadata),
		NewMetadata: convertModelMetadataToGrpcMetadata(newMetadata),
		Mem:         mem,
	}
	updMsg := &pb.CmdTransactionRequest_Update{
		Update: upd,
	}
	req := &pb.CmdTransactionRequest{
		TransSeq:       pb.TransType_UPDATE,
		MessageRequest: updMsg,
	}
	g.buffer = append(g.buffer, req)
	return nil
}

func (g *GrpcTransactor) Delete(metadata *model.Metadata, ignoreflags int32) error {
	del := &pb.CmdDeleteData{
		Metadata:      convertModelMetadataToGrpcMetadata(metadata),
		IgnoreErrFlag: ignoreflags,
	}
	delMsg := &pb.CmdTransactionRequest_Delete{
		Delete: del,
	}
	req := &pb.CmdTransactionRequest{
		TransSeq:       pb.TransType_DELETE,
		MessageRequest: delMsg,
	}
	g.buffer = append(g.buffer, req)
	return nil
}

func (g *GrpcTransactor) End() (error, *model.TxnResp) {
	//TODO: (gakshit): impl
	// 1. Send a header ..
	//	1a. create a header -> len(buffer) + 2 (header + trailer + len)
	// 2. loop through the buffer and stream them
	// 3. Send a trailer ..
	return nil, nil
}
