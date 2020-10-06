package org.anonymous.server;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import org.anonymous.grpc.*;
import org.anonymous.grpc.ObjServiceGrpc.ObjServiceImplBase;
import org.anonymous.module.ObjectRepository;
import org.anonymous.util.TimeKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class ObjServiceImpl extends ObjServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjServiceImpl.class);
    private static ObjectRepository objectRepository;

    ObjServiceImpl(ObjectRepository objectRepositiory) {
        ObjServiceImpl.objectRepository = objectRepositiory;
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
        try {
            int limit = request.getCount();
            int typeid = request.getGetType().getNumber();
            String prefix = "";
            if (request.getSecurityNamePrefix() != null) {
                prefix = request.getSecurityNamePrefix();
            }

            CmdLookupByNameResponse.Builder responseBuilder = CmdLookupByNameResponse.newBuilder();
            objectRepository.lookup(prefix, typeid, limit).stream().forEach(key -> responseBuilder.addSecurityNames(key));
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            LOGGER.info("Caught Exception in lookupByName()", e);
        }
    }

    @Override
    public void lookupByNameStream(CmdLookupByName request, StreamObserver<CmdLookupByNameResponseStream> responseObserver) {
        LOGGER.info("got request lookupByNameStream()");
        try {
            int limit = request.getCount();
            int typeid = request.getGetType().getNumber();
            String prefix = "";
            if (request.getSecurityNamePrefix() != null) {
                prefix = request.getSecurityNamePrefix();
            }
            CmdLookupByNameResponseStream.Builder responseBuilder = CmdLookupByNameResponseStream.newBuilder();
            objectRepository.lookup(prefix,typeid, limit).stream().forEach(key -> responseObserver.onNext(responseBuilder.setSecurityName(key).build()));
            responseObserver.onCompleted();

        } catch (Exception e) {
            LOGGER.info("Caught Exception in lookupByNameStream()", e);
        }
    }

    @Override
    public void lookupByType(CmdNameLookupByType request, StreamObserver<CmdNameLookupByTypeResponse> responseObserver) {
        LOGGER.info("got request lookupByType()");
        try {
            int limit = request.getCount();
            int typeid = request.getGetType().getNumber();
            int objectType= request.getSecurityType();
            String prefix = "";
            if (request.getSecurityNamePrefix() != null) {
                prefix = request.getSecurityNamePrefix();
            }
            CmdNameLookupByTypeResponse.Builder responseBuilder = CmdNameLookupByTypeResponse.newBuilder();
            objectRepository.lookupById(prefix, typeid, limit, objectType).stream().forEach(key -> responseBuilder.addSecurityNames(key));
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            LOGGER.info("Caught Exception in lookupByType()", e);
        }
    }

    @Override
    public void lookupByTypeStream(CmdNameLookupByType request, StreamObserver<CmdNameLookupByTypeResponseStream> responseObserver) {
        LOGGER.info("got request lookupByTypeStream()");

        try {
            int limit = request.getCount();
            int typeid = request.getGetType().getNumber();
            int objectType= request.getSecurityType();
            String prefix = "";
            if (request.getSecurityNamePrefix() != null) {
                prefix = request.getSecurityNamePrefix();
            }

            CmdNameLookupByTypeResponseStream.Builder responseBuilder = CmdNameLookupByTypeResponseStream.newBuilder();
            objectRepository.lookupById(prefix, typeid, limit, objectType).stream().forEach(key -> responseObserver.onNext(responseBuilder.setSecurityName(key).build()));
            responseObserver.onCompleted();

        } catch (Exception e) {
            LOGGER.info("Caught Exception in lookupByTypeStream()", e);
        }
    }

    @Override
    public void getObject(CmdGetByName request, StreamObserver<CmdGetByNameResponse> responseObserver) {
        LOGGER.info("got request getObject()");

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
            LOGGER.info("Caught Exception in getObject()", e);
        }
    }

    @Override
    public void getObjectManyByName(CmdGetManyByName request, StreamObserver<CmdGetManyByNameResponse> responseObserver) {
        LOGGER.info("got request getObjectManyByName()");

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
            LOGGER.info("Caught Exception in getObjectManyByName()", e);
        }
    }

    @Override
    public void getObjectManyByNameStream(CmdGetManyByName request, StreamObserver<CmdGetManyByNameResponseStream> responseObserver) {
        LOGGER.info("got request getObjectManyByNameStream()");

        try {
            List<ByteString> secMemList = objectRepository.getManyMemByName(request.getSecurityNamesList());
            for (ByteString mem : secMemList) {
                CmdGetManyByNameResponseStream.MsgOnSuccess msgOnSuccess = CmdGetManyByNameResponseStream.MsgOnSuccess.newBuilder().setMem(mem).build();
                CmdGetManyByNameResponseStream requestResponse = CmdGetManyByNameResponseStream.newBuilder().setMsgOnSuccess(msgOnSuccess).build();
                responseObserver.onNext(requestResponse);
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            LOGGER.info("Caught Exception in getObjectManyByNameStream()", e);
        }
    }

    @Override
    public void getObjectManyByNameExt(CmdGetManyByNameExt request, StreamObserver<CmdGetManyByNameExtResponse> responseObserver) {
        LOGGER.info("got request getObjectManyByNameExt()");

        try {
            List<CmdGetManyByNameExtResponse.ResponseMessage> responseMessageList = objectRepository.getManySDBByName(request.getSecurityNamesList());
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
            LOGGER.info("Caught Exception in getObjectManyByNameExt()", e);
        }
    }

    @Override
    public void getObjectManyByNameExtStream(CmdGetManyByNameExt request, StreamObserver<CmdGetManyByNameExtResponseStream> responseObserver) {
        LOGGER.info("got request getObjectManyByNameExtStream()");

        try {
            List<CmdGetManyByNameExtResponseStream> responseMessageList = objectRepository.getManySDBByNameStream(request.getSecurityNamesList());
            responseMessageList.forEach(responseObserver::onNext);
            responseObserver.onCompleted();
        } catch (Exception e) {
            LOGGER.info("Caught Exception in getObjectManyByNameExtStream()", e);
        }
    }

    @Override
    public void getObjectExt(CmdGetByNameExt request, StreamObserver<CmdGetByNameExtResponse> responseObserver) {
        LOGGER.info("got request getObjectExt()");

        try {
            CmdGetByNameExtResponse response;
            Optional<CmdGetByNameExtResponse.MsgOnSuccess> msgOnSuccess = objectRepository.getSDBRecordsByKey(request.getSecurityName());

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