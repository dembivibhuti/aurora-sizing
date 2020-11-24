package model

import "github.com/prometheus/client_golang/prometheus"

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

type Metrics struct {
	GlookupByName     *prometheus.GaugeVec
	GlookupByType     *prometheus.GaugeVec
	GgetObject        prometheus.Gauge
	GgetObjectExt     prometheus.Gauge
	GgetObjectMany    *prometheus.GaugeVec
	GgetObjectManyExt *prometheus.GaugeVec
}

func NewMetrics() *Metrics {
	return &Metrics{
		GlookupByName:     prometheus.NewGaugeVec(prometheus.GaugeOpts{Name: "lookup_by_name"}, []string{"number"}),
		GlookupByType:     prometheus.NewGaugeVec(prometheus.GaugeOpts{Name: "lookup_by_type"}, []string{"number"}),
		GgetObject:        prometheus.NewGauge(prometheus.GaugeOpts{Name: "get_object"}),
		GgetObjectExt:     prometheus.NewGauge(prometheus.GaugeOpts{Name: "get_object_ext"}),
		GgetObjectMany:    prometheus.NewGaugeVec(prometheus.GaugeOpts{Name: "get_object_many"}, []string{"number"}),
		GgetObjectManyExt: prometheus.NewGaugeVec(prometheus.GaugeOpts{Name: "get_object_many_ext"}, []string{"number"}),
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

type Record struct {
	Name string
	Time float64
}

type Record2 struct {
	SecName   string
	StringVal map[string]string
	DoubleVal map[string]float64
}

type TxnResp struct {
	// TODO: (mfrw) leave it empty for now
}
