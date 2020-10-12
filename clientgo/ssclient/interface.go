package ssclient

import (
	"log"

	pb "github.com/somnath67643/aurora-sizing/clientgo/baseproto"
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
	Close()
	Init()
	UseService(dbname string, closure func()) error
	LookupByName(prefix string, cmpType model.CmpType, nr int32) (<-chan string, error)
	LookupByType(prefix string, stype uint32, cmpType model.CmpType, nr int32) (<-chan string, error)
	GetObject(sname string) (*model.Object, error)
	GetObjectExt(sname string) (*model.ObjectExt, error)
	GetObjectMany(snames []string) (<-chan *model.Object, error)
	GetObjectManyExt(snames []string) (<-chan *model.ObjectExt, error)
	InsertRecord(metadata *pb.Metadata, cmdType pb.CmdType, nr int32) (*pb.CmdInsertResponse, error)
	RenameRecord(oldMetadata *pb.Metadata, newMetadata *pb.Metadata, cmdType pb.CmdType, nr int32) (*pb.CmdRenameResponse, error)
	UpdateRecord(oldMetadata *pb.Metadata, newMetadata *pb.Metadata, cmdType pb.CmdType, nr int32) (*pb.CmdUpdateResponse, error)
	DeleteRecord(metadata *pb.Metadata, cmdType pb.CmdType, nr int32, ignoreflags int32) (*pb.CmdDeleteResponse, error)
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
