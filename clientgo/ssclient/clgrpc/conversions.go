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
