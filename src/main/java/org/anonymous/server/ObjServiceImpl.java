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

    private static ObjectRepository objectRepositiory;

    private static Logger LOGGER = LoggerFactory.getLogger(ObjServiceImpl.class);

    ObjServiceImpl(ObjectRepository objectRepositiory) {
        this.objectRepositiory = objectRepositiory;
    }

    @Override
    public void lookupByName(CmdLookupByName request, StreamObserver<CmdLookupByNameResponse> responseObserver) {
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
        //super.lookupByNameStream(request, responseObserver);
    }

    @Override
    public void lookupByType(CmdNameLookupByType request, StreamObserver<CmdNameLookupByTypeResponse> responseObserver) {
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
        LOGGER.info("got request in lookupByTypeStream()");
    }

    @Override
    public void getObject(CmdGetByName request, StreamObserver<CmdGetByNameResponse> responseObserver) {
        TimeKeeper timeKeeper = new TimeKeeper();
        try {
            CmdGetByNameResponse response;

            Optional<byte[]> arrayContainsMem = objectRepositiory.getMemByKeyInBytes(request.getSecurityName(), timeKeeper);
            if (arrayContainsMem.isPresent()) {
                response = CmdGetByNameResponse.newBuilder().setSecurity(ByteString.copyFrom(arrayContainsMem.get())).build();
            } else {
                LOGGER.error("Security Doesn't exist");
                response = CmdGetByNameResponse.newBuilder().setErrorType(ErrorType.ERR_OBJECT_NOT_FOUND).build();
            }

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            LOGGER.info("Caught Exception in getObject()", e);
        }
    }
}