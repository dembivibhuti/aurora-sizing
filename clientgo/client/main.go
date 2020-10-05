package main

import (
	"flag"
	"fmt"
	"log"

	"github.com/somnath67643/aurora-sizing/clientgo/ssclient"
	"github.com/somnath67643/aurora-sizing/clientgo/ssclient/model"
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
	err := scl.UseService("tdmsqa_nyc_bm_lta3", func() {
		// LookupByName - streaming
		res, err := scl.LookupByName("test", model.GET_EQUAL, 10) //  250GB/32KB = 7812500
		if err != nil {
			log.Fatal(err)
		}
		secnames := []string{}
		for k := range res {
			secnames = append(secnames, k)
			// GetObject - SingleRPC
			respObj, err := scl.GetObject(k)
			if err != nil {
				log.Println(err)
				continue
			}
			fmt.Printf("SecName: %s -- SecBytesLen: %d\n", k, len(respObj.Mem))
		}
		mch, err := scl.GetObjectMany(secnames)
		if err != nil {
			log.Fatal(err)
		}
		for k := range mch {
			fmt.Println("GetObjectMany SecLen: ", len(k.Mem))
		}
	})

	if err != nil {
		log.Fatal(err)
	}
}
