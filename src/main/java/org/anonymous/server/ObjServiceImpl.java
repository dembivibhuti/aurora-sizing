package org.anonymous.server;

import org.anonymous.grpc.ObjServiceGrpc.ObjServiceImplBase;
import org.anonymous.grpc.CmdConnect;
import io.grpc.stub.StreamObserver;
import org.anonymous.grpc.CmdConnectResponse;

public class ObjServiceImpl extends ObjServiceImplBase {

    public void connect(CmdConnect request, StreamObserver<CmdConnectResponse> responseObserver) {

        System.out.println("yo......");
    }

}