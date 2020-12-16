package main

import (
	"flag"
	"fmt"
	"log"
	"math/rand"
	"net/http"
	"sort"
	"sync"
	"time"

	"github.com/prometheus/client_golang/prometheus"
	"github.com/prometheus/client_golang/prometheus/promhttp"
	"github.com/somnath67643/aurora-sizing/clientgo/ssclient"
	"github.com/somnath67643/aurora-sizing/clientgo/ssclient/model"
)

var (
	serverAddr     = flag.String("sa", "localhost:8080", "the server port")
	promScrapePort = flag.String("promscrapeport", ":8080", "the prometheus scrape port")
)

func main() {
	flag.Parse()
	rand.Seed(time.Now().UnixNano())

	metrics := model.NewMetrics()
	metrics.Register(prometheus.DefaultRegisterer)
	var wg sync.WaitGroup
	pattern := randDigit(3)
	mode := 2
	for i := 0; i < 500; i++ {
		wg.Add(1)
		go func() {
			defer wg.Done()
			for { // infinite for loop
				if mode == 1 {
					pattern = startTest(metrics, pattern)
				} else {
					startTest2(metrics)
				}
			}
		}()
	}

	startMetricsServer(*promScrapePort)
	wg.Wait()
	//ssMain(sscl)
}

func startTest(metrics *model.Metrics, pattern string) string {
	sscl := ssclient.NewSSClient(*serverAddr, ssclient.GRPC, metrics)
	defer sscl.Close()
	//sscl.EnableMetrics(":9090")
	return pairityWithSaral(sscl, pattern)
}

func startTest2(metrics *model.Metrics) {
	sscl := ssclient.NewSSClient(*serverAddr, ssclient.GRPC, metrics)
	defer sscl.Close()
	//sscl.EnableMetrics(":9090")
	pairityWithSaralVersion2(sscl)
}

func pairityWithSaral(scl model.SSClient, pattern string) string {
	var n int32 = 75 // send a huge number for lookup

	/*i := 1
	  for i < 3000 {
	      resp, err := scl.GetObjectExt("232574-46439-1-1326302")
	      if err != nil {
	          log.Println(err)
	          return // retry to create a new connection
	      }
	      _ = resp
	      i += 1
	  }*/

	res, err := scl.LookupByName(pattern, model.GET_GREATER, n)
	if err != nil {
		log.Println(err)
	} else {
		var keys []string
		for key := range res {
			keys = append(keys, key)
		}
		keysLen := len(keys)
		if keysLen < 75 {
			log.Printf("got less than 75 recs in lookup. Got %d, Pattern %s", keysLen, pattern)
			return randDigit(3)
		}

		i := 1
		var key string
		for i < 400 {
			for _, key = range keys {
				resp, err := scl.GetObjectExt(key)
				if err != nil {
					log.Println(err)
					return randDigit(3) // retry to create a new connection
				}
				_ = resp
				i += 1
			}
		}
		return key
	}
	return randDigit(3)
}

//func pairityWithSaral(scl model.SSClient, pattern string) string {
//	var n int32 = 100 // send a huge number for lookup
//	res, err := scl.LookupByName("testSec-"+randDigit(5), model.GET_GREATER, n)
//	if err != nil {
//		log.Fatal(err)
//	}
//	for k := range res {
//		//fmt.Println("Lookup: ", k)
//		resp, err := scl.GetObject(k)
//		if err != nil {
//			log.Fatal(err)
//		}
//		fmt.Print("Get Object Mem By Name Response: ", resp)
//		fmt.Println("================")
//
//		respCh, err := scl.GetIndexMsgByName(k, "test_table")
//		fmt.Print("Get Index Object By Name : ", respCh)
//		fmt.Println("================")
//	}
//
//}

func pairityWithSaralVersion2(scl model.SSClient) {
	respCh, err := scl.GetIndexRecordMany("", "Table_TT")
	if err != nil {
		log.Fatal(err)
	}
	for _, k := range respCh {
		fmt.Print("Get Index Object In Batches : ", k)
		fmt.Println("================")
	}
}

func startMetricsServer(addr string) {
	http.Handle("/metrics", promhttp.HandlerFor(
		prometheus.DefaultGatherer,
		promhttp.HandlerOpts{
			EnableOpenMetrics: true,
		},
	))
	go func() {
		log.Fatal(http.ListenAndServe(addr, nil)) // Start the http server for prometheus
	}()
}

func runNTimesWithArg(n int, fn func(n int32), arg int32) {
	for i := 0; i < n; i++ {
		fn(arg)
	}
}

func StringWithCharset(length int, charset string) string {
	b := make([]byte, length)
	for i := range b {
		b[i] = charset[rand.Intn(len(charset))]
	}
	return string(b)
}

func randDigit(n int) string {
	return StringWithCharset(n, "123456789")
}

func ssMain(scl model.SSClient) {
	scl.UseService("tdmsqa_nyc_bm_lta3", func() {

		lookupOnly := func(n int32) {
			secnames := make([]string, 0, n)
			start := time.Now()
			res, err := scl.LookupByName("testSec-"+randDigit(5), model.GET_GREATER, n) //  250GB/32KB = 7812500
			if err != nil {
				log.Fatal(err)
			}
			for k := range res {
				secnames = append(secnames, k)
			}
			fmt.Printf("LookupByName/No of Records: %d, Time Taken: %s\n", len(secnames), time.Since(start))
			fmt.Println("======================================================")
		}

		lookupWithGetObject := func(n int32) {
			lookupRes := make([]string, 0, n)
			timesPerGet := make([]time.Duration, 0, n)
			s3start := time.Now()
			res, err := scl.LookupByName("testSec-"+randDigit(5), model.GET_GREATER, n)
			if err != nil {
				log.Fatal(err)
			}
			for k := range res {
				lookupRes = append(lookupRes, k)
			}

			for _, k := range lookupRes {
				start := time.Now()
				resp, err := scl.GetObject(k)
				if err != nil {
					log.Fatal(err)
				}
				elapsed := time.Since(start)
				timesPerGet = append(timesPerGet, elapsed)
				_ = resp
			}
			s3elapsed := time.Since(s3start)
			sort.Slice(timesPerGet, func(i, j int) bool {
				return timesPerGet[i].Nanoseconds() < timesPerGet[j].Nanoseconds()
			})
			fmt.Printf("LookupByName/%d->GetObject->Individual --- [Total: %s] Min: %s, Max: %s, Med: %s, p99: %s\n", len(timesPerGet), s3elapsed.String(), timesPerGet[0].String(), timesPerGet[len(timesPerGet)-1].String(), timesPerGet[len(timesPerGet)/2], timesPerGet[(len(timesPerGet)*99)/100])
			fmt.Println("======================================================")
		}

		sectype := []int{}
		lookupWithGetMany := func(n int32) {
			secnames := make([]string, 0, n)
			start := time.Now()
			res, err := scl.LookupByName("testSec-"+randDigit(5), model.GET_GREATER, n)
			if err != nil {
				log.Fatal(err)
			}
			fmt.Println("GetObjectManyExt Response:")
			for k := range res {
				secnames = append(secnames, k)
			}
			resp, err := scl.GetObjectManyExt(secnames)
			if err != nil {
				log.Fatal(err)
			}
			for obj := range resp {
				sectype = append(sectype, int(obj.Metadata.SecurityType))
			}
			fmt.Printf("LookupByName/%d->GetObjectManyExt:%s\n", len(secnames), time.Since(start).String())
			fmt.Println("======================================================")
		}

		lookupByType := func(n int32) {
			start := time.Now()
			fmt.Println("Lookup by Type Response:")
			for _, stype := range sectype {
				res, err := scl.LookupByType("testSec-"+randDigit(5), uint32(stype), model.GET_GREATER, n)
				if err != nil {
					log.Fatal(err)
				}
				secnames := []string{}
				for k := range res {
					secnames = append(secnames, k)
					//fmt.Printf("Lookup By Type: %d | Security Name: %s \n", stype, k)
				}
			}
			fmt.Printf("LookupByType Time Taken:%s\n", time.Since(start).String())
			fmt.Println("======================================================")
		}

		transaction := func(n int32) {
			transactor := scl.BeginTxn()
			metadata := &model.Metadata{
				SecurityName: "testSec-10",
				SecurityType: 1,
				UpdateCount:  1,
				DateCreated:  1,
				TimeUpdated:  "0001-01-01 00:00:00",
				DbIdUpdated:  1,
				LastTxnId:    1,
				VersionInfo:  1,
			}
			mem := []byte{}
			transactor.Insert(metadata, mem)
			updateMetadata := &model.Metadata{
				SecurityName: "testSec-10",
				SecurityType: 1,
				UpdateCount:  1,
				DateCreated:  1,
				TimeUpdated:  "1111-11-11 00:00:00",
				DbIdUpdated:  1,
				LastTxnId:    1,
				VersionInfo:  1,
			}
			transactor.Update(metadata, updateMetadata, mem)
			renameMetadata := &model.Metadata{
				SecurityName: "testSec-11",
				SecurityType: 1,
				UpdateCount:  1,
				DateCreated:  1,
				TimeUpdated:  "1111-11-11 00:00:00",
				DbIdUpdated:  1,
				LastTxnId:    1,
				VersionInfo:  1,
			}
			transactor.Rename(updateMetadata, renameMetadata)
			transactor.Delete(renameMetadata, n)
			transactor.End()
			fmt.Println("======================================================")
		}

		increments := []int32{100, 1_000, 10_000} //, 100_000, 1_000_000} // go can have numebers 100 -> 1_0_0
		for {
			for _, i := range increments {
				runNTimesWithArg(1, lookupOnly, i)
				runNTimesWithArg(1, lookupWithGetObject, i)
				runNTimesWithArg(1, lookupWithGetMany, i)
				runNTimesWithArg(0, lookupByType, i)
				runNTimesWithArg(0, transaction, i)
			}
		}
	})
}
