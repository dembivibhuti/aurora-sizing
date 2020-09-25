package main

import (
	"flag"
	"fmt"

	"github.com/somnath67643/aurora-sizing/clientgo/ssclient"
)

var (
	serverAddr = flag.String("sa", "localhost:8080", "the server port")
)

func main() {
	flag.Parse()
	sscl := ssclient.NewSSClient(*serverAddr, ssclient.GRPC)
	defer sscl.Close()
	ssMain(sscl)
}

func ssMain(scl ssclient.SSClient) {
	scl.UseService("tdmsqa_nyc_bm_lta3", func() {
		fmt.Println("In closure")

	})
}
