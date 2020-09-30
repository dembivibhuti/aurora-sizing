package org.anonymous.server;

import com.google.protobuf.ByteString;
import org.anonymous.grpc.*;
import org.anonymous.grpc.ObjServiceGrpc.ObjServiceImplBase;
import io.grpc.stub.StreamObserver;
import org.anonymous.module.ObjectRepository;
import org.anonymous.util.TimeKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class ObjServiceImpl extends ObjServiceImplBase {
    private static ObjectRepository objectRepository;
    private static Logger LOGGER= LoggerFactory.getLogger(ObjServiceImpl.class);

    ObjServiceImpl(ObjectRepository objectRepository) {
        this.objectRepository = objectRepository;
    }

    public void connect(CmdConnect request, StreamObserver<CmdConnectResponse> responseObserver) {

        System.out.println("yo......");
    }

    public void getObjectManyByName(CmdGetManyByName request, StreamObserver<CmdGetManyByNameResponse> responseObserver) {
        final TimeKeeper timekeeper = new TimeKeeper();
        try{
            List<ByteString> secMemList = objectRepository.getManyMemByName(request.getSecurityNameList(), timekeeper);
            CmdGetManyByNameResponse.Builder responseBuilder = CmdGetManyByNameResponse.newBuilder();
            int totalRows = 0;
            for (ByteString mem:secMemList) {
                totalRows++;
                CmdGetManyByNameResponse.RequestResponse.MsgOnSuccess msgOnSuccess = CmdGetManyByNameResponse.RequestResponse.MsgOnSuccess.newBuilder().setMem(mem).build();
                CmdGetManyByNameResponse.RequestResponse requestResponse = CmdGetManyByNameResponse.RequestResponse.newBuilder().setMsgOnSuccess(msgOnSuccess).build();
                responseBuilder.addRequestResponse(requestResponse);
            }
            responseBuilder.setSecurityCount(totalRows);
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            LOGGER.info("Caught Exception in getObjectManyByName()", e);
        }
    }

    public void getObjectManyByNameStream(CmdGetManyByName request, StreamObserver<CmdGetManyByNameResponseStream> responseObserver) {
        final TimeKeeper timekeeper = new TimeKeeper();
        try{
            List<ByteString> secMemList = objectRepository.getManyMemByName(request.getSecurityNameList(), timekeeper);
            for (ByteString mem:secMemList) {
                CmdGetManyByNameResponseStream.MsgOnSuccess msgOnSuccess = CmdGetManyByNameResponseStream.MsgOnSuccess.newBuilder().setMem(mem).build();
                CmdGetManyByNameResponseStream requestResponse = CmdGetManyByNameResponseStream.newBuilder().setMsgOnSuccess(msgOnSuccess).build();
                responseObserver.onNext(requestResponse);
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            LOGGER.info("Caught Exception in getObjectManyByNameStream()", e);
        }
    }
}