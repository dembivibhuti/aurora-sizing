package model

import (
	pb "github.com/somnath67643/aurora-sizing/clientgo/baseproto"
)

type SSClient interface {
	Reader
	BeginTxn() Transactor
	Init()
	Close()
	UseService(dbname string, closure func()) error
	EnableMetrics(addr string)
}

type Reader interface {
	LookupByName(prefix string, cmpType CmpType, nr int32) (<-chan string, error)
	LookupByType(prefix string, stype uint32, cmpType CmpType, nr int32) (<-chan string, error)
	GetObject(sname string) (*Object, error)
	GetObjectExt(sname string) (*ObjectExt, error)
	GetObjectMany(snames []string) (<-chan *Object, error)
	GetObjectManyExt(snames []string) (<-chan *ObjectExt, error)
	GetIndexMsgByName(sname string, indexName string) (*Record2, error)
	GetIndexManyByNameStream([]string, string) (<-chan *Record2, error)
	GetIndexRecordInBatches(tableName string) (<-chan *Record2, error)
	GetIndexRecordMany(sname string, tableName string) ([]*pb.IndexRecord, error)
}

type Transactor interface {
	Insert(metadata *Metadata, mem []byte) error
	Rename(oldMetadata *Metadata, newMetadata *Metadata) error
	Update(oldMetadata *Metadata, newMetadata *Metadata, mem []byte) error
	Delete(metadata *Metadata, ignoreflags int32) error
	End() (error, *TxnResp) // Dude that calls the rpc
}
