package org.anonymous.server;

import org.anonymous.grpc.ObjectServiceGrpc.ObjectServiceImplBase;
import org.anonymous.grpc.ObjectRequest;
import io.grpc.stub.StreamObserver;
import org.anonymous.grpc.ObjectResponse;

public class ObjectServiceImpl extends ObjectServiceImplBase {

    public void exists(ObjectRequest request, StreamObserver<ObjectResponse> responseObserver) {
        ObjectResponse response = ObjectResponse.newBuilder().setFound(true).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}