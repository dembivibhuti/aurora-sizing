package model

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
