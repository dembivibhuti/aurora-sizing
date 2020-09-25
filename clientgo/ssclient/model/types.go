package model

type Metadata struct {
	mem []byte
}

type Object struct {
	mem []byte
}

type ObjectExt struct {
	metaData Metadata
	mem      []byte
}
