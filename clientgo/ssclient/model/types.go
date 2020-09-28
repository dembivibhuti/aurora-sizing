package model

type CmpType int

const (
	GET_FIRST CmpType = iota
	GET_LAST
	GET_EQUAL
	GET_LESS
	GET_LE
	GET_GREATER
	GET_GE
	GET_NEXT
	GET_PREV
)

type Metadata struct {
	SecurityName string
	SecurityType int32
	UpdateCount  int32
	DateCreated  int32
	TimeUpdated  int32
	DbIdUpdated  int32
	LastTxnId    int32
	VersionInfo  int32
}

type Object struct {
	Mem []byte
}

type ObjectExt struct {
	MetaData Metadata
	Mem      []byte
}
