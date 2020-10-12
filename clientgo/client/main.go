package main

import (
	"flag"
	"fmt"
	"log"
	"math/rand"
	"sort"
	"time"

	pb "github.com/somnath67643/aurora-sizing/clientgo/baseproto"
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

func ssMain(scl ssclient.SSClient) {
	scl.UseService("tdmsqa_nyc_bm_lta3", func() {

		lookupOnly := func(n int32) {
			start := time.Now()
			res, err := scl.LookupByName("testSec--"+randDigit(5), model.GET_GREATER, n) //  250GB/32KB = 7812500
			if err != nil {
				log.Fatal(err)
			}
			secnames := []string{}
			for k := range res {
				secnames = append(secnames, k)
			}
			fmt.Printf("LookupByName/%d: %s\n", len(secnames), time.Since(start))
		}

		lookupWithGetObject := func(n int32) {
			s3start := time.Now()
			res, err := scl.LookupByName("testSec--"+randDigit(5), model.GET_GREATER, n)
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
			s3elapsed := time.Since(s3start)
			sort.Slice(timesPerGet, func(i, j int) bool {
				return timesPerGet[i].Nanoseconds() < timesPerGet[j].Nanoseconds()
			})
			fmt.Printf("LookupByName/%d->GetObj->Individual --- [Total: %s] Min: %s, Max: %s, Med: %s, p99: %s\n", len(timesPerGet), s3elapsed.String(), timesPerGet[0].String(), timesPerGet[len(timesPerGet)-1].String(), timesPerGet[len(timesPerGet)/2], timesPerGet[(len(timesPerGet)*99)/100])
		}

		lookupWithGetMany := func(n int32) {
			start := time.Now()
			res, err := scl.LookupByName("testSec--"+randDigit(5), model.GET_GREATER, n)
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
			fmt.Printf("LookupByName/%d->GetObjectManyExt: %s\n", len(secnames), time.Since(start).String())
		}

		insertRecord := func(n int32) {
			metadata := &pb.Metadata{
				SecurityName: "testSec-10",
				SecurityType: 1,
				UpdateCount:  1,
				DateCreated:  1,
				TimeUpdate:   "0001-01-01 00:00:00",
				DbIdUpdated:  1,
				LastTxnId:    1,
				VersionInfo:  1,
			}
			res, err := scl.InsertRecord(metadata, pb.CmdType_CMD_INSERT, n)
			if err != nil {
				log.Fatal(err)
			}
			fmt.Println("%v", res)
		}

		renameRecord := func(n int32) {
			oldMetadata := &pb.Metadata{
				SecurityName: "testSec-10",
				SecurityType: 1,
				UpdateCount:  1,
				DateCreated:  1,
				TimeUpdate:   "0001-01-01 00:00:00",
				DbIdUpdated:  1,
				LastTxnId:    1,
				VersionInfo:  1,
			}
			newMetadata := &pb.Metadata{
				SecurityName: "testSec-11",
				SecurityType: 1,
				UpdateCount:  1,
				DateCreated:  1,
				TimeUpdate:   "0001-01-01 00:00:00",
				DbIdUpdated:  1,
				LastTxnId:    1,
				VersionInfo:  1,
			}
			res, err := scl.RenameRecord(oldMetadata, newMetadata, pb.CmdType_CMD_RENAME, n)
			if err != nil {
				log.Fatal(err)
			}
			fmt.Println("%v", res)
		}

		updateRecord := func(n int32) {
			oldMetadata := &pb.Metadata{
				SecurityName: "testSec-11",
				SecurityType: 1,
				UpdateCount:  1,
				DateCreated:  1,
				TimeUpdate:   "0001-01-01 00:00:00",
				DbIdUpdated:  1,
				LastTxnId:    1,
				VersionInfo:  1,
			}
			newMetadata := &pb.Metadata{
				SecurityName: "testSec-11",
				SecurityType: 1,
				UpdateCount:  1,
				DateCreated:  1,
				TimeUpdate:   "1111-11-11 00:00:00",
				DbIdUpdated:  1,
				LastTxnId:    1,
				VersionInfo:  1,
			}
			res, err := scl.UpdateRecord(oldMetadata, newMetadata, pb.CmdType_CMD_UPDATE, n)
			if err != nil {
				log.Fatal(err)
			}
			fmt.Println("%v", res)
		}

		deleteRecord := func(n int32) {
			metadata := &pb.Metadata{
				SecurityName: "testSec-11",
				SecurityType: 1,
				UpdateCount:  1,
				DateCreated:  1,
				TimeUpdate:   "1111-11-11 00:00:00",
				DbIdUpdated:  1,
				LastTxnId:    1,
				VersionInfo:  1,
			}
			res, err := scl.DeleteRecord(metadata, pb.CmdType_CMD_DELETE, n, 1)
			if err != nil {
				log.Fatal(err)
			}
			fmt.Println("%v", res)
		}

		increments := []int32{1, 10, 100, 1_000, 10_000, 100_000, 1_000_000} // go can have numebers 100 -> 1_0_0
		for _, i := range increments {
			runNTimesWithArg(3, lookupOnly, i)
			runNTimesWithArg(3, lookupWithGetObject, i)
			runNTimesWithArg(3, lookupWithGetMany, i)
			runNTimesWithArg(1, insertRecord, i)
			runNTimesWithArg(1, renameRecord, i)
			runNTimesWithArg(1, updateRecord, i)
			runNTimesWithArg(1, deleteRecord, i)
		}
	})
}
