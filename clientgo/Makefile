testclient: proto
	go build -o testclient -v ./client/


proto:
	rm -f baseproto/*.go
	protoc --go_out=baseproto --go-grpc_out=baseproto --go_opt=paths=source_relative --go-grpc_opt=paths=source_relative -I ../src/main/proto/ ../src/main/proto/*.proto

testserver:
	go build -o testserver -v ./server/
clean:
	rm -rf testclient testserver

image: proto clean
	@echo "Cross-Compiling explicitly for linux"
	env CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -a -v -tags netgo -ldflags '-extldflags "-static"' -o testclient ./client
	docker build -t mfrw/client:0.1 .
	rm testclient
