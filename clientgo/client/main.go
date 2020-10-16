package main

import (
	"flag"
	"fmt"
	"log"
	"math/rand"
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
	rand.Seed(time.Now().UnixNano())
	sscl := ssclient.NewSSClient(*serverAddr, ssclient.GRPC)
	defer sscl.Close()
	sscl.EnableMetrics(":9090")
	ssMain(sscl)
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
			start := time.Now()
			res, err := scl.LookupByName("test", model.GET_GREATER, 100) //  250GB/32KB = 7812500
			if err != nil {
				log.Fatal(err)
			}
			secnames := []string{}
			fmt.Println("LookupByName Response:")
			for k := range res {
				secnames = append(secnames, k)
				fmt.Printf("Security: %s\n", k)
			}
			fmt.Printf("LookupByName/No of Records: %d, Time Taken: %s\n", len(secnames), time.Since(start))
			fmt.Println("======================================================")
		}

		lookupWithGetObject := func(n int32) {
			s3start := time.Now()
			res, err := scl.LookupByName("test", model.GET_GREATER, 100)
			if err != nil {
				log.Fatal(err)
			}
			timesPerGet := []time.Duration{}
			fmt.Println("GetObject Response: SUCCESS = 0; FAILURE = 1;")
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
			s3elapsed := time.Since(s3start)
			sort.Slice(timesPerGet, func(i, j int) bool {
				return timesPerGet[i].Nanoseconds() < timesPerGet[j].Nanoseconds()
			})
			fmt.Printf("LookupByName/%d->GetObject->Individual --- [Total: %s] Min: %s, Max: %s, Med: %s, p99: %s\n", len(timesPerGet), s3elapsed.String(), timesPerGet[0].String(), timesPerGet[len(timesPerGet)-1].String(), timesPerGet[len(timesPerGet)/2], timesPerGet[(len(timesPerGet)*99)/100])
			fmt.Println("======================================================")
		}

		sectype := []int{}
		lookupWithGetMany := func(n int32) {
			start := time.Now()
			res, err := scl.LookupByName("test", model.GET_GREATER, 100)
			if err != nil {
				log.Fatal(err)
			}
			fmt.Println("GetObjectManyExt Response:")
			secnames := []string{}
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
				res, err := scl.LookupByType("test", uint32(stype), model.GET_GREATER, 100)
				if err != nil {
					log.Fatal(err)
				}
				secnames := []string{}
				for k := range res {
					secnames = append(secnames, k)
					fmt.Printf("Lookup By Type: %d | Security Name: %s \n", stype, k)
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

		increments := []int32{1} //, 10}//, 100, 1_000, 10_000, 100_000, 1_000_000} // go can have numebers 100 -> 1_0_0
		for _, i := range increments {
			runNTimesWithArg(1, lookupOnly, i)
			runNTimesWithArg(1, lookupWithGetObject, i)
			runNTimesWithArg(1, lookupWithGetMany, i)
			runNTimesWithArg(1, lookupByType, i)
			runNTimesWithArg(1, transaction, i)
		}
	})
}
