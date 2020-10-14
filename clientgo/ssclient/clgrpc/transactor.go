package clgrpc

import (
	pb "github.com/somnath67643/aurora-sizing/clientgo/baseproto"
	"github.com/somnath67643/aurora-sizing/clientgo/ssclient/model"
)

// GrpcTransactor implements the ssclient.Transactor interface
type GrpcTransactor struct {
	transClient pb.TransactionServiceClient
}

func (g *GrpcTransactor) Insert(metadata *model.Metadata, mem []byte) error {
	return nil
}

func (g *GrpcTransactor) Rename(oldMetadata *model.Metadata, newMetadata *model.Metadata) error {
	return nil
}

func (g *GrpcTransactor) Update(oldMetadata *model.Metadata, newMetadata *model.Metadata, mem []byte) error {
	return nil
}

func (g *GrpcTransactor) Delete(metadata *model.Metadata, ignoreflags int32) error {
	return nil
}

func (g *GrpcTransactor) End() (error, *model.TxnResp) {
	return nil, nil
}
