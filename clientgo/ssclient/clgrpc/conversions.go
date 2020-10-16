package clgrpc

import (
	pb "github.com/somnath67643/aurora-sizing/clientgo/baseproto"
	"github.com/somnath67643/aurora-sizing/clientgo/ssclient/model"
)

func convertCmpTypeToGrpcGetType(t model.CmpType) pb.GetType {
	return pb.GetType(int32(t))
}

func convertGrpcGetTypeToCmpType(t pb.GetType) model.CmpType {
	return model.CmpType(int(t))
}

func convertModelMetadataToGrpcMetadata(m *model.Metadata) *pb.Metadata {
	return &pb.Metadata{
		SecurityName: m.SecurityName,
		SecurityType: m.SecurityType,
		UpdateCount:  m.UpdateCount,
		DateCreated:  m.DateCreated,
		TimeUpdate:   m.TimeUpdated,
		DbIdUpdated:  m.DbIdUpdated,
		LastTxnId:    m.LastTxnId,
		VersionInfo:  m.VersionInfo,
	}
}
func convertGrpcMetadataToModelMetadata(m *pb.Metadata) *model.Metadata {
	return &model.Metadata{
		SecurityName: m.SecurityName,
		SecurityType: m.SecurityType,
		UpdateCount:  m.UpdateCount,
		DateCreated:  m.DateCreated,
		TimeUpdated:  m.TimeUpdate,
		DbIdUpdated:  m.DbIdUpdated,
		LastTxnId:    m.LastTxnId,
		VersionInfo:  m.VersionInfo,
	}

}
