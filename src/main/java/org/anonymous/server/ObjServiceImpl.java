package org.anonymous.server;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import org.anonymous.grpc.CmdGetByName;
import org.anonymous.grpc.CmdGetByNameResponse;
import org.anonymous.grpc.ErrorType;
import org.anonymous.grpc.ObjServiceGrpc;
import org.anonymous.module.ObjectRepository;
import org.anonymous.util.TimeKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ObjServiceImpl extends ObjServiceGrpc.ObjServiceImplBase {
    private static Logger LOGGER= LoggerFactory.getLogger(ObjServiceImpl.class);
    private static ObjectRepository objectRepository;
    ObjServiceImpl(ObjectRepository objectRepository) {
        this.objectRepository = objectRepository;

    }

    @Override
    public void getObject(CmdGetByName request, StreamObserver<CmdGetByNameResponse> responseObserver) {
        System.out.println(request);
        TimeKeeper timeKeeper = new TimeKeeper();
        CmdGetByNameResponse response;

        byte[] arrayContainsMem = objectRepository.getMemBySecurityKey(request.getSecurityName(), timeKeeper);
        if (arrayContainsMem.length == 0) {
            LOGGER.error("Security Doesn't exist");
            response = CmdGetByNameResponse.newBuilder().setErrorType(ErrorType.ERR_OBJECT_NOT_FOUND).build();
        } else {
            response = CmdGetByNameResponse.newBuilder().setSecurity(ByteString.copyFrom(arrayContainsMem)).build();
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}