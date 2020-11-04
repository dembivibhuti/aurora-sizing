package ssclient

import (
	"log"

	"github.com/somnath67643/aurora-sizing/clientgo/ssclient/clgrpc"
	"github.com/somnath67643/aurora-sizing/clientgo/ssclient/model"
)

type ClientType int

const (
	GRPC ClientType = iota
	TCP
	BSTREAM
)

func NewSSClient(addr string, typ ClientType) model.SSClient {
	var cl model.SSClient
	switch typ {
	case GRPC:
		cl = clgrpc.NewSSClient(addr, metrics *model.Metrics)
	//case TCP:
	//cl = cltcp.NewSSClient(addr)
	//case BSTREAM:
	//cl = clbstream.NewSSClient(addr)
	default:
		log.Fatal("Invalid ClientType")
	}
	return cl
}
