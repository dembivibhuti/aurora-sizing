package org.anonymous.server;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import io.prometheus.client.Gauge;
import org.anonymous.domain.IndexRecDTO;
import org.anonymous.grpc.*;
import org.anonymous.grpc.ObjServiceGrpc.ObjServiceImplBase;
import org.anonymous.module.NearCachedObjectRepository;
import org.anonymous.module.ObjectRepository;
import org.anonymous.stats.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ObjServiceImpl extends ObjServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjServiceImpl.class);
    private static final Gauge getObjectGaugeTimer = Gauge.build().name("get_object_mw").help("Get Object on Middleware").labelNames("grpc_method").register();
    private static final Gauge getObjectExtGaugeTimer = Gauge.build().name("get_object_ext_mw").help("Get Object Ext on Middleware").labelNames("grpc_method").register();
    private static final Gauge lookupByNameObjectGaugeTimer = Gauge.build().name("lookup_by_name_mw").help("Lookup Object by Name on Middleware").labelNames("grpc_method").register();
    private static ObjectRepository objectRepository;
    private static NearCachedObjectRepository nearCachedObjectRepository;

    private static final boolean NO_DB = ("true".equals(System.getProperty("stubbed")));
    private static final boolean CACHED = ("true".equals(System.getProperty("usecache")));

    ObjServiceImpl(ObjectRepository objectRepositiory, NearCachedObjectRepository nearCachedObjectRepository) {
        ObjServiceImpl.objectRepository = objectRepositiory;
        ObjServiceImpl.nearCachedObjectRepository = nearCachedObjectRepository;
    }

    @Override
    public void connect(org.anonymous.grpc.CmdConnect request,
                        io.grpc.stub.StreamObserver<org.anonymous.grpc.CmdConnectResponse> responseObserver) {
        LOGGER.trace("got request connect()");
        long span = Statistics.connect.start();
        responseObserver.onNext(CmdConnectResponse.newBuilder().setMsgSize(1).setVerAndRev(-1).setFeatureFlag(1).build());
        responseObserver.onCompleted();
        Statistics.connect.stop(span);
    }

    // Used in test
    @Override
    public void lookupByName(CmdLookupByName request, StreamObserver<CmdLookupByNameResponse> responseObserver) {
        LOGGER.trace("got request lookupByName()");
        long span = Statistics.lookupByName.start();
        Gauge.Timer timer = lookupByNameObjectGaugeTimer.labels("lookup_by_name").startTimer();
        try {
            int limit = request.getCount();
            LOGGER.trace("limit = " + limit);
            int typeid = request.getGetType().getNumber();
            LOGGER.trace("typeid = " + typeid);
            String prefix = "";
            if (request.getSecurityNamePrefix() != null) {
                prefix = request.getSecurityNamePrefix();
            }

            LOGGER.trace("prefix = " + prefix);

            CmdLookupByNameResponse.Builder responseBuilder = CmdLookupByNameResponse.newBuilder();
            nearCachedObjectRepository.lookup(prefix, typeid, limit).stream().forEach(key -> responseBuilder.addSecurityNames(key));
            responseObserver.onNext(responseBuilder.build());
            timer.setDuration();
            responseObserver.onCompleted();
        } catch (Exception e) {
            LOGGER.error("Caught Exception in lookupByName()", e);
        } finally {

            Statistics.lookupByName.stop(span);
        }
    }

    // Used in test
    @Override
    public void lookupByNameStream(CmdLookupByName request, StreamObserver<CmdLookupByNameResponseStream> responseObserver) {
        LOGGER.trace("got request lookupByNameStream()");
        long start = System.currentTimeMillis();
        long span = Statistics.lookupByNameStream.start();
        Gauge.Timer timer = lookupByNameObjectGaugeTimer.labels("lookup_by_name").startTimer();
        try {
            int limit = request.getCount();
            int typeid = request.getGetType().getNumber();
            String prefix = "";
            if (request.getSecurityNamePrefix() != null) {
                prefix = request.getSecurityNamePrefix();
            }
            CmdLookupByNameResponseStream.Builder responseBuilder = CmdLookupByNameResponseStream.newBuilder();
            List<String> results = nearCachedObjectRepository.lookup(prefix, typeid, limit);
            results.stream().forEach(key -> responseObserver.onNext(responseBuilder.setSecurityName(key).build()));
            timer.setDuration();
            responseObserver.onCompleted();
            LOGGER.trace("elapsed time = " + (System.currentTimeMillis() - start) / 1000);
        } catch (Exception e) {
            LOGGER.error("Caught Exception in lookupByNameStream()", e);
        } finally {
            Statistics.lookupByNameStream.stop(span);
        }
    }

    @Override
    public void lookupByType(CmdNameLookupByType request, StreamObserver<CmdNameLookupByTypeResponse> responseObserver) {
        LOGGER.trace("got request lookupByType()");
        long span = Statistics.lookupByType.start();
        try {
            int limit = request.getCount();
            int typeid = request.getGetType().getNumber();
            int objectType = request.getSecurityType();
            String prefix = "";
            if (request.getSecurityNamePrefix() != null) {
                prefix = request.getSecurityNamePrefix();
            }
            CmdNameLookupByTypeResponse.Builder responseBuilder = CmdNameLookupByTypeResponse.newBuilder();
            objectRepository.lookupById(prefix, typeid, limit, objectType).stream().forEach(key -> responseBuilder.addSecurityNames(key));
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            LOGGER.error("Caught Exception in lookupByType()", e);
        } finally {
            Statistics.lookupByType.stop(span);
        }
    }

    @Override
    public void lookupByTypeStream(CmdNameLookupByType request, StreamObserver<CmdNameLookupByTypeResponseStream> responseObserver) {
        LOGGER.trace("got request lookupByTypeStream()");
        long span = Statistics.lookupByTypeStream.start();
        try {
            int limit = request.getCount();
            int typeid = request.getGetType().getNumber();
            int objectType = request.getSecurityType();
            String prefix = "";
            if (request.getSecurityNamePrefix() != null) {
                prefix = request.getSecurityNamePrefix();
            }

            CmdNameLookupByTypeResponseStream.Builder responseBuilder = CmdNameLookupByTypeResponseStream.newBuilder();
            objectRepository.lookupById(prefix, typeid, limit, objectType).stream().forEach(key -> responseObserver.onNext(responseBuilder.setSecurityName(key).build()));
            responseObserver.onCompleted();

        } catch (Exception e) {
            LOGGER.error("Caught Exception in lookupByTypeStream()", e);
        } finally {
            Statistics.lookupByTypeStream.stop(span);
        }
    }

    @Override
    public void getObject(CmdGetByName request, StreamObserver<CmdGetByNameResponse> responseObserver) {
        LOGGER.trace("got request getObject()");
        long span = Statistics.getObject.start();
        Gauge.Timer timer = getObjectGaugeTimer.labels("get_object").startTimer();
        try {
            CmdGetByNameResponse response;

            Optional<byte[]> arrayContainsMem = objectRepository.getMemByKeyInBytes(request.getSecurityName());
            if (arrayContainsMem.isPresent()) {
                response = CmdGetByNameResponse.newBuilder().setSecurity(ByteString.copyFrom(arrayContainsMem.get())).build();
            } else {
                LOGGER.error("Object Doesn't exist");
                response = CmdGetByNameResponse.newBuilder().setErrorType(ErrorType.ERR_OBJECT_NOT_FOUND).build();
            }

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            LOGGER.error("Caught Exception in getObject()", e);
        } finally {
            timer.setDuration();
            Statistics.getObject.stop(span);
        }
    }

    @Override
    public void getObjectManyByName(CmdGetManyByName request, StreamObserver<CmdGetManyByNameResponse> responseObserver) {
        LOGGER.trace("got request getObjectManyByName()");
        long span = Statistics.getObjectManyByName.start();
        try {
            List<ByteString> secMemList = objectRepository.getManyMemByName(request.getSecurityNamesList());
            CmdGetManyByNameResponse.Builder responseBuilder = CmdGetManyByNameResponse.newBuilder();
            int totalRows = 0;
            for (ByteString mem : secMemList) {
                totalRows++;
                CmdGetManyByNameResponse.RequestResponse.MsgOnSuccess msgOnSuccess = CmdGetManyByNameResponse.RequestResponse.MsgOnSuccess.newBuilder().setMem(mem).build();
                CmdGetManyByNameResponse.RequestResponse requestResponse = CmdGetManyByNameResponse.RequestResponse.newBuilder().setMsgOnSuccess(msgOnSuccess).build();
                responseBuilder.addRequestResponse(requestResponse);
            }
            responseBuilder.setSecurityCount(totalRows);
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            LOGGER.error("Caught Exception in getObjectManyByName()", e);
        } finally {
            Statistics.getObjectManyByName.stop(span);
        }
    }

    @Override
    public void getObjectManyByNameStream(CmdGetManyByName request, StreamObserver<CmdGetManyByNameResponseStream> responseObserver) {
        LOGGER.trace("got request getObjectManyByNameStream()");
        long span = Statistics.getObjectManyByNameStream.start();
        try {
            List<ByteString> secMemList = objectRepository.getManyMemByName(request.getSecurityNamesList());
            for (ByteString mem : secMemList) {
                CmdGetManyByNameResponseStream.MsgOnSuccess msgOnSuccess = CmdGetManyByNameResponseStream.MsgOnSuccess.newBuilder().setMem(mem).build();
                CmdGetManyByNameResponseStream requestResponse = CmdGetManyByNameResponseStream.newBuilder().setMsgOnSuccess(msgOnSuccess).build();
                responseObserver.onNext(requestResponse);
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            LOGGER.error("Caught Exception in getObjectManyByNameStream()", e);
        } finally {
            Statistics.getObjectManyByNameStream.stop(span);
        }
    }

    @Override
    public void getObjectManyByNameExt(CmdGetManyByNameExt request, StreamObserver<CmdGetManyByNameExtResponse> responseObserver) {
        LOGGER.trace("got request getObjectManyByNameExt()");
        long span = Statistics.getObjectManyByNameExt.start();
        try {
            List<CmdGetManyByNameExtResponse.ResponseMessage> responseMessageList = objectRepository.getManyFullSecurities(request.getSecurityNamesList());
            CmdGetManyByNameExtResponse.Builder responseBuilder = CmdGetManyByNameExtResponse.newBuilder();
            int totalRows = 0;
            for (CmdGetManyByNameExtResponse.ResponseMessage sdb : responseMessageList) {
                totalRows++;
                responseBuilder.addResp(sdb);
            }
            responseBuilder.setCount(totalRows);
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            LOGGER.error("Caught Exception in getObjectManyByNameExt()", e);
        } finally {
            Statistics.getObjectManyByNameExt.stop(span);
        }
    }

    @Override
    public void getObjectManyByNameExtStream(CmdGetManyByNameExt request, StreamObserver<CmdGetManyByNameExtResponseStream> responseObserver) {
        LOGGER.trace("got request getObjectManyByNameExtStream()");
        long span = Statistics.getObjectManyByNameExtStream.start();
        try {
            List<CmdGetManyByNameExtResponseStream> responseMessageList = objectRepository.getManySDBByNameStream(request.getSecurityNamesList());
            responseMessageList.forEach(responseObserver::onNext);
            responseObserver.onCompleted();
        } catch (Exception e) {
            LOGGER.trace("Caught Exception in getObjectManyByNameExtStream()", e);
        } finally {
            Statistics.getObjectManyByNameExtStream.stop(span);
        }
    }

    @Override
    public void getObjectExt(CmdGetByNameExt request, StreamObserver<CmdGetByNameExtResponse> responseObserver) {
        LOGGER.trace("got request getObjectExt()");
        long span = Statistics.getObjectExt.start();
        Gauge.Timer timer = getObjectExtGaugeTimer.labels("get_object_ext").startTimer();
        try {
            CmdGetByNameExtResponse response;
            //async(request, responseObserver, timer, span); Does not help much
            sync(request, responseObserver, timer, span);
        } catch (Exception e) {
            LOGGER.error("Caught Exception in getObjectExt()", e);
        }
    }

    @Override
    public void getIndexMsgByName(CmdMsgIndexGetByName request, StreamObserver<CmdMsgIndexGetByNameResponse> responseObserver) {
        LOGGER.trace("got request getIndexObject()");
        try {
            CmdMsgIndexGetByNameResponse response = objectRepository.getIndexObjectsFromCSV(request.getIndexId(), request.getSecurityName());
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            LOGGER.error("Caught Exception in getIndexObjectByName()", e);
        }
    }

    @Override
    public void getIndexMsgManyByNameExtStream(CmdMsgIndexGetManyByNameExt request, StreamObserver<CmdMsgIndexGetManyByNameResponseStream> responseObserver) {
        LOGGER.trace("got request getIndexMsgManyByNameExtStream()");
        try {
            List<CmdMsgIndexGetManyByNameResponseStream> responseMessageList = objectRepository.getIdxManyByNameStreamFromCSV(request.getIndexId(), request.getSecurityNameList());
            responseMessageList.forEach(responseObserver::onNext);
            responseObserver.onCompleted();
        } catch (Exception e) {
            LOGGER.trace("Caught Exception in getIndexMsgManyByNameExtStream()", e);
        }
    }

    @Override
    public void getIndexRecordInBatches(CmdMsgIndexGetByNameByLimit request, StreamObserver<CmdMsgIndexGetByNameByLimitResponse> responseObserver) {
        LOGGER.trace("got request getIndexRecordInBatches()");
        try {
            int offsetStartFromHere = 0;
            int limitBatchSize = 100;

            while (true) {
                List<CmdMsgIndexGetByNameByLimitResponse> responseMessageList = objectRepository.indexRecordsInBatch(offsetStartFromHere, limitBatchSize, request.getTableName());
                responseMessageList.forEach(responseObserver::onNext);
                if (responseMessageList.size() < limitBatchSize) {
                    break;
                }
                offsetStartFromHere += limitBatchSize;
            }

            responseObserver.onCompleted();
        } catch (Exception e) {
            LOGGER.trace("Caught Exception in getIndexRecordInBatches()", e);
        }
    }

    @Override
    public void getIndexRecordMany(CmdMsgIndexGetByNameWithClient request, StreamObserver<CmdMsgIndexGetByNameWithClientResponse> responseObserver) {
        CmdMsgIndexGetByNameWithClientResponse.MsgOnSuccess.Builder msgOnSuccess =
                CmdMsgIndexGetByNameWithClientResponse.MsgOnSuccess
                        .newBuilder();

        /*nearCachedObjectRepository.getIndexRecordManyFromRedis(request.getRecordName(), request.getTableName())
                .stream()
                .map(IndexRecDTO::toCmdMsgIndexGetByNameWithClientResponse)
                .forEach(msgOnSuccess::addIndexRecords);*/

        msgOnSuccess.addIndexRecords(nearCachedObjectRepository.getIndexRecord(request.getRecordName(), request.getTableName()).get().toCmdMsgIndexGetByNameWithClientResponse());
        responseObserver.onNext(CmdMsgIndexGetByNameWithClientResponse.newBuilder().setMsgOnSuccess(msgOnSuccess.build()).build());
        responseObserver.onCompleted();
    }

    // Used in test
    private void sync(CmdGetByNameExt request, StreamObserver<CmdGetByNameExtResponse> responseObserver, Gauge.Timer timer, long spanID) {
        CmdGetByNameExtResponse response;
        Optional<CmdGetByNameExtResponse.MsgOnSuccess> msgOnSuccess;
        if(NO_DB) {
            msgOnSuccess = objectRepository.getFullObjectStubbed(request.getSecurityName());
        } else {
            if(CACHED) {
                msgOnSuccess = nearCachedObjectRepository.getFullObject(request.getSecurityName());
            } else {
                msgOnSuccess = Optional.ofNullable(objectRepository.getFullObject(request.getSecurityName()).get().toCmdGetByNameExtResponseMsgOnSuccess());
            }
        }

        if (msgOnSuccess.isPresent()) {
            response = CmdGetByNameExtResponse.newBuilder().setMsgOnSuccess(msgOnSuccess.get()).build();
        } else {
            LOGGER.error("Object Doesn't exist");
            CmdGetByNameExtResponse.MsgOnFailure msgOnFailure = CmdGetByNameExtResponse.MsgOnFailure.newBuilder().setErrorType(ErrorType.ERR_OBJECT_NOT_FOUND).build();
            response = CmdGetByNameExtResponse.newBuilder().setMsgOnFailure(msgOnFailure).build();
        }
        responseObserver.onNext(response);
        timer.setDuration();
        responseObserver.onCompleted();
        Statistics.getObjectExt.stop(spanID);
    }

    private void async(CmdGetByNameExt request, StreamObserver<CmdGetByNameExtResponse> responseObserver, Gauge.Timer timer, long spanId) {
        objectRepository.getFullObjectAsync(request.getSecurityName()).whenComplete((answer, error) -> {
            CmdGetByNameExtResponse response;

            if (error != null) {
                LOGGER.error("Object Doesn't exist", error);
                CmdGetByNameExtResponse.MsgOnFailure msgOnFailure = CmdGetByNameExtResponse.MsgOnFailure.newBuilder().setErrorType(ErrorType.ERR_OBJECT_NOT_FOUND).build();
                response = CmdGetByNameExtResponse.newBuilder().setMsgOnFailure(msgOnFailure).build();
            } else if (answer.isPresent()) {
                response = CmdGetByNameExtResponse.newBuilder().setMsgOnSuccess(answer.get()).build();
            } else {
                LOGGER.error("Object Doesn't exist");
                CmdGetByNameExtResponse.MsgOnFailure msgOnFailure = CmdGetByNameExtResponse.MsgOnFailure.newBuilder().setErrorType(ErrorType.ERR_OBJECT_NOT_FOUND).build();
                response = CmdGetByNameExtResponse.newBuilder().setMsgOnFailure(msgOnFailure).build();
            }
            responseObserver.onNext(response);
            timer.setDuration();
            responseObserver.onCompleted();
            Statistics.getObjectExt.stop(spanId);
        });
    }
}