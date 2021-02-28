package org.anonymous.async.server;

import io.grpc.stub.StreamObserver;
import io.prometheus.client.Gauge;
import org.anonymous.async.db.Repository;
import org.anonymous.grpc.*;
import org.anonymous.grpc.ObjServiceGrpc.ObjServiceImplBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjServiceImpl2 extends ObjServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjServiceImpl2.class);
    private static final Gauge getObjectGaugeTimer = Gauge.build().name("get_object_mw").help("Get Object on Middleware").labelNames("grpc_method").register();
    private static final Gauge getObjectExtGaugeTimer = Gauge.build().name("get_object_ext_mw").help("Get Object Ext on Middleware").labelNames("grpc_method").register();
    private static final Gauge lookupByNameObjectGaugeTimer = Gauge.build().name("lookup_by_name_mw").help("Lookup Object by Name on Middleware").labelNames("grpc_method").register();
    private final Repository repository;

    ObjServiceImpl2(Repository repository) {
        this.repository = repository;
    }

    @Override
    public void connect(CmdConnect request,
                        StreamObserver<CmdConnectResponse> responseObserver) {
        responseObserver.onNext(CmdConnectResponse.newBuilder().setMsgSize(1).setVerAndRev(-1).setFeatureFlag(1).build());
        responseObserver.onCompleted();
    }

    // Used in test
    @Override
    public void lookupByName(CmdLookupByName request, StreamObserver<CmdLookupByNameResponse> responseObserver) {
        Gauge.Timer timer = lookupByNameObjectGaugeTimer.labels("lookup_by_name").startTimer();
        try {
            int limit = request.getCount();
            int typeid = request.getGetType().getNumber();
            String prefix = "";
            if (request.getSecurityNamePrefix() != null) {
                prefix = request.getSecurityNamePrefix();
            }
            repository.lookup(prefix, typeid, limit).thenAccept(keys -> {
                responseObserver.onNext(CmdLookupByNameResponse.newBuilder().addAllSecurityNames(keys).build());
                responseObserver.onCompleted();
                timer.setDuration();
            });
        } catch (Exception e) {
            LOGGER.error("Caught Exception in lookupByName()", e);
        } finally {

        }
    }

    // Used in test
    @Override
    public void lookupByNameStream(CmdLookupByName request, StreamObserver<CmdLookupByNameResponseStream> responseObserver) {
        Gauge.Timer timer = lookupByNameObjectGaugeTimer.labels("lookup_by_name").startTimer();
        try {
            int limit = request.getCount();
            int typeid = request.getGetType().getNumber();
            String prefix = "";
            if (request.getSecurityNamePrefix() != null) {
                prefix = request.getSecurityNamePrefix();
            }
            repository.lookup(prefix, typeid, limit).thenAccept(keys -> {
                keys.stream().forEach(key -> responseObserver.onNext(CmdLookupByNameResponseStream.newBuilder().setSecurityName(key).build()));
                responseObserver.onCompleted();
                timer.setDuration();
            });
        } catch (Exception e) {
            LOGGER.error("Caught Exception in lookupByNameStream()", e);
        } finally {

        }
    }

    @Override
    public void lookupByType(CmdNameLookupByType request, StreamObserver<CmdNameLookupByTypeResponse> responseObserver) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void lookupByTypeStream(CmdNameLookupByType request, StreamObserver<CmdNameLookupByTypeResponseStream> responseObserver) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void getObject(CmdGetByName request, StreamObserver<CmdGetByNameResponse> responseObserver) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void getObjectManyByName(CmdGetManyByName request, StreamObserver<CmdGetManyByNameResponse> responseObserver) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void getObjectManyByNameStream(CmdGetManyByName request, StreamObserver<CmdGetManyByNameResponseStream> responseObserver) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void getObjectManyByNameExt(CmdGetManyByNameExt request, StreamObserver<CmdGetManyByNameExtResponse> responseObserver) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void getObjectManyByNameExtStream(CmdGetManyByNameExt request, StreamObserver<CmdGetManyByNameExtResponseStream> responseObserver) {
        throw new UnsupportedOperationException();
    }

    // Used in test
    @Override
    public void getObjectExt(CmdGetByNameExt request, StreamObserver<CmdGetByNameExtResponse> responseObserver) {
        Gauge.Timer timer = getObjectExtGaugeTimer.labels("get_object_ext").startTimer();
        try {
            sync(request, responseObserver, timer);
        } catch (Exception e) {
            LOGGER.error("Caught Exception in getObjectExt()", e);
        }
    }

    @Override
    public void getIndexMsgByName(CmdMsgIndexGetByName request, StreamObserver<CmdMsgIndexGetByNameResponse> responseObserver) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void getIndexMsgManyByNameExtStream(CmdMsgIndexGetManyByNameExt request, StreamObserver<CmdMsgIndexGetManyByNameResponseStream> responseObserver) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void getIndexRecordInBatches(CmdMsgIndexGetByNameByLimit request, StreamObserver<CmdMsgIndexGetByNameByLimitResponse> responseObserver) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void getIndexRecordMany(CmdMsgIndexGetByNameWithClient request, StreamObserver<CmdMsgIndexGetByNameWithClientResponse> responseObserver) {
        throw new UnsupportedOperationException();
    }

    // Used in test
    private void sync(CmdGetByNameExt request, StreamObserver<CmdGetByNameExtResponse> responseObserver, Gauge.Timer timer) {
        repository.getFullObject(request.getSecurityName()).thenAccept(msgOnSuccess -> {
            if (msgOnSuccess.isPresent()) {
                responseObserver.onNext(CmdGetByNameExtResponse.newBuilder().setMsgOnSuccess(msgOnSuccess.get()).build());
            } else {
                CmdGetByNameExtResponse.MsgOnFailure msgOnFailure = CmdGetByNameExtResponse.MsgOnFailure.newBuilder().setErrorType(ErrorType.ERR_OBJECT_NOT_FOUND).build();
                responseObserver.onNext(CmdGetByNameExtResponse.newBuilder().setMsgOnFailure(msgOnFailure).build());
            }
            responseObserver.onCompleted();
            timer.setDuration();
        });
    }
}