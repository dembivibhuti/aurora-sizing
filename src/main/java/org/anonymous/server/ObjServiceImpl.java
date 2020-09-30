package org.anonymous.server;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import org.anonymous.grpc.*;
import org.anonymous.grpc.ObjServiceGrpc.ObjServiceImplBase;
import org.anonymous.module.ObjectRepository;
import org.anonymous.util.TimeKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class ObjServiceImpl extends ObjServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjServiceImpl.class);
    private static ObjectRepository objectRepositiory;

    ObjServiceImpl(ObjectRepository objectRepositiory) {
        ObjServiceImpl.objectRepositiory = objectRepositiory;
    }

    @Override
    public void connect(org.anonymous.grpc.CmdConnect request,
                        io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdConnectResponse> responseObserver) {
        LOGGER.info("got request connect()");
        responseObserver.onNext(CmdConnectResponse.newBuilder().setMsgSize(1).setVerAndRev(-1).setFeatureFlag(1).build());
        responseObserver.onCompleted();
    }

    @Override
    public void lookupByName(CmdLookupByName request, StreamObserver<CmdLookupByNameResponse> responseObserver) {
        LOGGER.info("got request lookupByName()");
        final TimeKeeper timekeeper = new TimeKeeper();
        try {
            int limit = request.getCount();
            String prefix = "";
            if (request.getSecurityNamePrefix() != null) {
                prefix = request.getSecurityNamePrefix();
            }

            CmdLookupByNameResponse.Builder responseBuilder = CmdLookupByNameResponse.newBuilder();
            objectRepositiory.lookup(prefix, limit, timekeeper).stream().forEach(key -> responseBuilder.addSecurityNames(key));
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            LOGGER.info("Caught Exception in lookupByName()", e);
        }
    }

    @Override
    public void lookupByNameStream(CmdLookupByName request, StreamObserver<CmdLookupByNameResponseStream> responseObserver) {
        LOGGER.info("got request lookupByNameStream()");
        final TimeKeeper timekeeper = new TimeKeeper();
        try {
            int limit = request.getCount();
            String prefix = "";
            if (request.getSecurityNamePrefix() != null) {
                prefix = request.getSecurityNamePrefix();
            }
            CmdLookupByNameResponseStream.Builder responseBuilder = CmdLookupByNameResponseStream.newBuilder();
            objectRepositiory.lookup(prefix, limit, timekeeper).stream().forEach(key -> responseObserver.onNext(responseBuilder.setSecurityName(key).build()));
            responseObserver.onCompleted();

        } catch (Exception e) {
            LOGGER.info("Caught Exception in lookupByNameStream()", e);
        }
    }

    @Override
    public void lookupByType(CmdNameLookupByType request, StreamObserver<CmdNameLookupByTypeResponse> responseObserver) {
        LOGGER.info("got request lookupByType()");
        final TimeKeeper timekeeper = new TimeKeeper();
        try {
            int limit = request.getCount();
            String prefix = "";
            if (request.getSecurityNamePrefix() != null) {
                prefix = request.getSecurityNamePrefix();
            }
            int typeid = request.getGetType().getNumber();

            CmdNameLookupByTypeResponse.Builder responseBuilder = CmdNameLookupByTypeResponse.newBuilder();
            objectRepositiory.lookupById(prefix, typeid, limit, timekeeper).stream().forEach(key -> responseBuilder.addSecurityNames(key));
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            LOGGER.info("Caught Exception in lookupByType()", e);
        }
    }

    @Override
    public void lookupByTypeStream(CmdNameLookupByType request, StreamObserver<CmdNameLookupByTypeResponseStream> responseObserver) {
        LOGGER.info("got request lookupByTypeStream()");
        final TimeKeeper timekeeper = new TimeKeeper();
        try {
            int limit = request.getCount();
            String prefix = "";
            if (request.getSecurityNamePrefix() != null) {
                prefix = request.getSecurityNamePrefix();
            }
            int typeid = request.getGetType().getNumber();
            CmdNameLookupByTypeResponseStream.Builder responseBuilder = CmdNameLookupByTypeResponseStream.newBuilder();
            objectRepositiory.lookupById(prefix, typeid, limit, timekeeper).stream().forEach(key -> responseObserver.onNext(responseBuilder.setSecurityName(key).build()));
            responseObserver.onCompleted();

        } catch (Exception e) {
            LOGGER.info("Caught Exception in lookupByTypeStream()", e);
        }
    }

    @Override
    public void getObject(CmdGetByName request, StreamObserver<CmdGetByNameResponse> responseObserver) {
        LOGGER.info("got request getObject()");
        TimeKeeper timeKeeper = new TimeKeeper();
        try {
            CmdGetByNameResponse response;

            Optional<byte[]> arrayContainsMem = objectRepositiory.getMemByKeyInBytes(request.getSecurityName(), timeKeeper);
            if (arrayContainsMem.isPresent()) {
                response = CmdGetByNameResponse.newBuilder().setSecurity(ByteString.copyFrom(arrayContainsMem.get())).build();
            } else {
                LOGGER.error("Object Doesn't exist");
                response = CmdGetByNameResponse.newBuilder().setErrorType(ErrorType.ERR_OBJECT_NOT_FOUND).build();
            }

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            LOGGER.info("Caught Exception in getObject()", e);
        }
    }

    @Override
    public void getObjectExt(CmdGetByNameExt request, StreamObserver<CmdGetByNameExtResponse> responseObserver) {
        LOGGER.info("got request getObjectExt()");
        TimeKeeper timeKeeper = new TimeKeeper();
        try {
            CmdGetByNameExtResponse response;
            Optional<CmdGetByNameExtResponse.MsgOnSuccess> msgOnSuccess = objectRepositiory.getSDBRecordsByKey(request.getSecurityName(), timeKeeper);

            if(msgOnSuccess.isPresent()){
                response = CmdGetByNameExtResponse.newBuilder().setMsgOnSuccess(msgOnSuccess.get()).build();
            }else{
                LOGGER.error("Object Doesn't exist");
                CmdGetByNameExtResponse.MsgOnFailure msgOnFailure = CmdGetByNameExtResponse.MsgOnFailure.newBuilder().setErrorType(ErrorType.ERR_OBJECT_NOT_FOUND).build();
                response = CmdGetByNameExtResponse.newBuilder().setMsgOnFailure(msgOnFailure).build();
            }

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            LOGGER.info("Caught Exception in getObjectExt()", e);
        }
    }
}