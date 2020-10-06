package main

import (
	"flag"
	"fmt"
	"log"
	"sort"
	"time"

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

func runNTimes(n int, fn func()) {
	for i := 0; i < n; i++ {
		fn()
	}
}

func ssMain(scl ssclient.SSClient) {
	scl.UseService("tdmsqa_nyc_bm_lta3", func() {
		// scenario 1 : LookupByName : 100
		scenario1 := func() {
			start := time.Now()
			res, err := scl.LookupByName("test", model.GET_GREATER, 100) //  250GB/32KB = 7812500
			if err != nil {
				log.Fatal(err)
			}
			secnames := []string{}
			for k := range res {
				secnames = append(secnames, k)
			}
			fmt.Println("LookupByName/100: ", time.Since(start))
		}

		// scenario 2: LookupByName: 0 // no limit
		scenario2 := func() {
			start := time.Now()
			res, err := scl.LookupByName("test", model.GET_GREATER, 0)
			if err != nil {
				log.Fatal(err)
			}
			secnames := []string{}
			for k := range res {
				secnames = append(secnames, k)
			}
			fmt.Println("LookupByName/0: ", time.Since(start))
		}

		// scnario 3: LookupByName: 100 -> GetObject
		scenario3 := func() {
			s3start := time.Now()
			res, err := scl.LookupByName("test", model.GET_GREATER, 100)
			if err != nil {
				log.Fatal(err)
			}
			timesPerGet := []time.Duration{}
			for k := range res {
				start := time.Now()
				resp, err := scl.GetObject(k)
				if err != nil {
					log.Fatal(err)
				}
				elapsed := time.Since(start)
				timesPerGet = append(timesPerGet, elapsed)
				_ = resp
			}
			fmt.Println("LookupByName/100->GetObj: ", time.Since(s3start))
			sort.Slice(timesPerGet, func(i, j int) bool {
				return timesPerGet[i].Nanoseconds() < timesPerGet[j].Nanoseconds()
			})
			fmt.Printf("LookupByName/100->GetObj->Ind --- Min: %s, Max: %s, Median: %s\n", timesPerGet[0].String(), timesPerGet[len(timesPerGet)-1].String(), timesPerGet[len(timesPerGet)/2])
		}

		scenario4 := func() {
			start := time.Now()
			res, err := scl.LookupByName("test", model.GET_GREATER, 100)
			if err != nil {
				log.Fatal(err)
			}
			secnames := []string{}
			for k := range res {
				secnames = append(secnames, k)
			}
			resp, err := scl.GetObjectManyExt(secnames)
			if err != nil {
				log.Fatal(err)
			}
			for obj := range resp {
				_ = obj
			}
			fmt.Println("LookupByName/100->GetObjectManyExt: ", time.Since(start))
		}

		runNTimes(3, scenario1)
		runNTimes(3, scenario2)
		runNTimes(3, scenario3)
		runNTimes(10, scenario4)

	})
}
