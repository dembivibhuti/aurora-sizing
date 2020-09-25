package clbstream

import (
	"net"
)

type SSClient struct {
	conn net.Conn
}

func mustGetConn(addr string) net.Conn {
	// connect with the tcp service
	// mfrw: TODO
	return nil
}

func NewSSClient(addr string) *SSClient {
	return &SSClient{
		conn: mustGetConn(addr),
	}
}

func (s *SSClient) Close() {
	s.conn.Close()
}

func (s *SSClient) Init() {
	// dont need anything here as of now
}

func (s *SSClient) UseService(dbname string, closure func()) error {
	return nil
}

func (s *SSClient) LookupByName(prefix string, cmpType string) ([]string, error) {
	return []string{}, nil
}

func (s *SSClient) LookupByType(prefix string, stype string, cmpType string) ([]string, error) {
	return []string{}, nil
}

func (s *SSClient) GetObject(sname string) {
}

func (s *SSClient) GetObjectMany(snames []string) {
}
