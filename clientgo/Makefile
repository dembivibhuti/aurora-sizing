testclient: proto
	go build -o testclient -v ./client/


proto:
	rm -f baseproto/*.go
	protoc --go_out=baseproto --go-grpc_out=baseproto --go_opt=paths=source_relative --go-grpc_opt=paths=source_relative -I ../src/main/proto/ ../src/main/proto/*.proto

testserver:
	go build -o testserver -v ./server/
clean:
	rm -rf testclient testserver
