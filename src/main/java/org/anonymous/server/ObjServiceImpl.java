package org.anonymous.server;

import io.grpc.stub.StreamObserver;
import org.anonymous.grpc.*;
import org.anonymous.grpc.ObjServiceGrpc.ObjServiceImplBase;
import org.anonymous.module.ObjectRepository;
import org.anonymous.util.TimeKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            LOGGER.info("Caught Exception", e);
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
            LOGGER.info("Caught Exception", e);
        }
    }

    @Override
    public void lookupByTypeStream(CmdNameLookupByType request, StreamObserver<CmdNameLookupByTypeResponseStream> responseObserver) {
        // super.lookupByTypeStream(request, responseObserver);
    }
}