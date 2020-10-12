package org.anonymous.server;

import io.grpc.stub.StreamObserver;
import org.anonymous.grpc.*;
import org.anonymous.module.ObjectRepository;
import org.anonymous.stats.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class TransactionServiceImpl extends TransactionServiceGrpc.TransactionServiceImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjServiceImpl.class);
    private static ObjectRepository objectRepository;

    TransactionServiceImpl(ObjectRepository objectRepositiory) {
        TransactionServiceImpl.objectRepository = objectRepositiory;
    }

    @Override
    public void connect(CmdConnect request,
                        StreamObserver<CmdConnectResponse> responseObserver) {
        LOGGER.info("got request connect()");
        long span = Statistics.connect.start();
        responseObserver.onNext(CmdConnectResponse.newBuilder().setMsgSize(1).setVerAndRev(-1).setFeatureFlag(1).build());
        responseObserver.onCompleted();
        Statistics.connect.stop(span);
    }

    @Override
    public void insertRecord(CmdInsert request, StreamObserver<CmdInsertResponse> responseObserver) {
        LOGGER.info("got request insertRecord()");
        try {
            CmdInsertResponse.Builder responseBuilder = CmdInsertResponse.newBuilder();
            TransMsgResponse.Builder transMsgResponseBuilder = TransMsgResponse.newBuilder();
            if(objectRepository.insertRec(request.getSdbDisk(), request.getMem())){
                TransMsgResponse.MsgOnSuccess.NotSecSyncMessage notSecSyncMessage = TransMsgResponse.MsgOnSuccess.NotSecSyncMessage.newBuilder()
                        .setAck(1)
                        .setTxnId((int) request.getSdbDisk().getLastTxnId()).build();
                TransMsgResponse.MsgOnSuccess msgOnSuccess = TransMsgResponse.MsgOnSuccess.newBuilder().setNotSecSyncMessage(notSecSyncMessage).build();
                transMsgResponseBuilder.setMsgOnSuccess(msgOnSuccess);
            }
            else{
                TransMsgResponse.MsgOnFailure.NotSecSyncMessage notSecSyncMessage = TransMsgResponse.MsgOnFailure.NotSecSyncMessage.newBuilder()
                        .setAck(0)
                        .setTxnId(0).build();
                TransMsgResponse.MsgOnFailure msgOnFailure = TransMsgResponse.MsgOnFailure.newBuilder().setNotSecSyncMessage(notSecSyncMessage).build();
                transMsgResponseBuilder.setMsgOnFailure(msgOnFailure);
            }
            responseBuilder.setTransMsgResponse(transMsgResponseBuilder.build());
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            LOGGER.info("Caught Exception in insertRecord()", e);
        }
    }

    @Override
    public void updateRecord(CmdUpdate request, StreamObserver<CmdUpdateResponse> responseObserver) {
        LOGGER.info("got request updateRecord()");
        try {
            CmdUpdateResponse.Builder responseBuilder = CmdUpdateResponse.newBuilder();
            TransMsgResponse.Builder transMsgResponseBuilder = TransMsgResponse.newBuilder();
            if(objectRepository.updateRec(request.getOldSdbDisk(), request.getNewSdbDisk(), request.getMem())){
                TransMsgResponse.MsgOnSuccess.NotSecSyncMessage notSecSyncMessage = TransMsgResponse.MsgOnSuccess.NotSecSyncMessage.newBuilder()
                        .setAck(1)
                        .setTxnId((int) request.getNewSdbDisk().getLastTxnId()).build();
                TransMsgResponse.MsgOnSuccess msgOnSuccess = TransMsgResponse.MsgOnSuccess.newBuilder().setNotSecSyncMessage(notSecSyncMessage).build();
                transMsgResponseBuilder.setMsgOnSuccess(msgOnSuccess);
            }
            else{
                TransMsgResponse.MsgOnFailure.NotSecSyncMessage notSecSyncMessage = TransMsgResponse.MsgOnFailure.NotSecSyncMessage.newBuilder()
                        .setAck(0)
                        .setTxnId(0).build();
                TransMsgResponse.MsgOnFailure msgOnFailure = TransMsgResponse.MsgOnFailure.newBuilder().setNotSecSyncMessage(notSecSyncMessage).build();
                transMsgResponseBuilder.setMsgOnFailure(msgOnFailure);
            }
            responseBuilder.setTransMsgResponse(transMsgResponseBuilder.build());
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            LOGGER.info("Caught Exception in updateRecord()", e);
        }
    }
    @Override
    public void deleteRecord(CmdDeleteData request, StreamObserver<CmdDeleteDataResponse> responseObserver) {
        LOGGER.info("got request deleteRecord()");

        try {
            CmdDeleteDataResponse.Builder responseBuilder = CmdDeleteDataResponse.newBuilder();
            Optional<TransMsgResponse.MsgOnSuccess> msgOnSuccess = objectRepository.deleteDataRecords(request.getMetadata());

            if(msgOnSuccess.isPresent()){
                System.out.println("msgOnSuccess");
                TransMsgResponse transMsgResponse = TransMsgResponse.newBuilder().setMsgOnSuccess(msgOnSuccess.get()).build();
                responseBuilder = responseBuilder.setTransMsgResponse(transMsgResponse);
            }
            else{
                System.out.println("msgOnFailure");
                TransMsgResponse.MsgOnFailure msgOnFailure = TransMsgResponse.MsgOnFailure.newBuilder().
                        setNotSecSyncMessage(TransMsgResponse.MsgOnFailure.NotSecSyncMessage
                                .newBuilder().setAck(0).setTxnId(0).setErrorMessageType(ErrorType.ERR_INVALID_ARGUMENTS)).build();
                TransMsgResponse transMsgResponse = TransMsgResponse.newBuilder().setMsgOnFailure(msgOnFailure).build();
                responseBuilder = responseBuilder.setTransMsgResponse(transMsgResponse);

            }

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            LOGGER.info("Caught Exception in deleteData()", e);
        }
    }

    @Override
    public void renameRecord(CmdRenameData request, StreamObserver<CmdRenameDataResponse> responseObserver) {
        LOGGER.info("got request renameRecord()");

        try {
            CmdRenameDataResponse.Builder responseBuilder = CmdRenameDataResponse.newBuilder();
            Optional<TransMsgResponse.MsgOnSuccess> msgOnSuccess = objectRepository.renameDataRecords(request.getOldMetadata(), request.getNewMetadata());

            if(msgOnSuccess.isPresent()){
                System.out.println("msgOnSuccess");
                TransMsgResponse transMsgResponse = TransMsgResponse.newBuilder().setMsgOnSuccess(msgOnSuccess.get()).build();
                responseBuilder = responseBuilder.setTransMsgResponse(transMsgResponse);
            }
            else{
                System.out.println("msgOnFailure");
                TransMsgResponse.MsgOnFailure msgOnFailure = TransMsgResponse.MsgOnFailure.newBuilder().
                        setNotSecSyncMessage(TransMsgResponse.MsgOnFailure.NotSecSyncMessage
                                .newBuilder().setAck(0).setTxnId(0).setErrorMessageType(ErrorType.ERR_INVALID_ARGUMENTS)).build();
                TransMsgResponse transMsgResponse = TransMsgResponse.newBuilder().setMsgOnFailure(msgOnFailure).build();
                responseBuilder = responseBuilder.setTransMsgResponse(transMsgResponse);

            }

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            LOGGER.info("Caught Exception in renameData()", e);
        }

    }
}
