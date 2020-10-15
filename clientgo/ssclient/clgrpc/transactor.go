package clgrpc

import (
	"context"
	"fmt"
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
	rnm := &pb.CmdRename{
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
	del := &pb.CmdDelete{
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
	tClient, err := g.transClient.Transaction(context.Background())
	if err != nil {
		return err, nil
	}
	header := &pb.CmdHeader{
		TransName: "abc",
		TransType: 0,
	}

	headerMsg := &pb.CmdTransactionRequest_Header{
		Header: header,
	}
	req := &pb.CmdTransactionRequest{
		TransSeq:       pb.TransType_HEADER,
		MessageRequest: headerMsg,
	}
	err = tClient.Send(req)
	if err != nil {
		return err, nil
	}

	for _, val := range g.buffer {
		err = tClient.Send(val)
		if err != nil {
			return err, nil
		}
	}
	trailer := &pb.CmdTrailer{}
	trailerMsg := &pb.CmdTransactionRequest_Trailer{
		Trailer: trailer,
	}
	trailerReq := &pb.CmdTransactionRequest{
		TransSeq:       pb.TransType_TRAILER,
		MessageRequest: trailerMsg,
	}
	err = tClient.Send(trailerReq)
	if err != nil {
		return err, nil
	}
	resp, err := tClient.CloseAndRecv()
	fmt.Println(resp)
	if err != nil {
		return err, nil
	}

	return nil, &model.TxnResp{}
}
