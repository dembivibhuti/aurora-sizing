package model

import "github.com/prometheus/client_golang/prometheus"

type CmpType int

type Metrics struct {
	GlookupByName     prometheus.Gauge
	GlookupByType     prometheus.Gauge
	GgetObject        prometheus.Gauge
	GgetObjectExt     prometheus.Gauge
	GgetObjectMany    prometheus.Gauge
	GgetObjectManyExt prometheus.Gauge
}

func NewMetrics() *Metrics {
	return &Metrics{
		GlookupByName:     prometheus.NewGauge(prometheus.GaugeOpts{Name: "lookup_by_name"}),
		GlookupByType:     prometheus.NewGauge(prometheus.GaugeOpts{Name: "lookup_by_type"}),
		GgetObject:        prometheus.NewGauge(prometheus.GaugeOpts{Name: "get_object"}),
		GgetObjectExt:     prometheus.NewGauge(prometheus.GaugeOpts{Name: "get_object_ext"}),
		GgetObjectMany:    prometheus.NewGauge(prometheus.GaugeOpts{Name: "get_object_many"}),
		GgetObjectManyExt: prometheus.NewGauge(prometheus.GaugeOpts{Name: "get_object_many_ext"}),
	}
}

func (m *Metrics) Register(r prometheus.Registerer) {
	r.MustRegister(
		m.GlookupByName,
		m.GlookupByType,
		m.GgetObject,
		m.GgetObjectExt,
		m.GgetObjectMany,
		m.GgetObjectManyExt,
	)
}

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
	UpdateCount  int64
	DateCreated  int32
	TimeUpdated  string
	DbIdUpdated  int32
	LastTxnId    int64
	VersionInfo  int32
}

type Object struct {
	Mem []byte
}

type ObjectExt struct {
	Metadata *Metadata
	Mem      []byte
}

type TxnResp struct {
	// TODO: (mfrw) leave it empty for now
}
