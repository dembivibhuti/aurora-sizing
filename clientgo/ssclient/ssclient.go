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

type SSClient interface {
	model.Reader
	BeginTxn() model.Transactor
	Init()
	Close()
	UseService(dbname string, closure func()) error
}

func NewSSClient(addr string, typ ClientType) SSClient {
	var cl SSClient
	switch typ {
	case GRPC:
		cl = clgrpc.NewSSClient(addr)
	//case TCP:
	//cl = cltcp.NewSSClient(addr)
	//case BSTREAM:
	//cl = clbstream.NewSSClient(addr)
	default:
		log.Fatal("Invalid ClientType")
	}
	return cl
}
