package org.anonymous.server;

import io.grpc.stub.StreamObserver;
import org.anonymous.grpc.CmdTransactionRequest;
import org.anonymous.grpc.TransMsgResponse;
import org.anonymous.grpc.TransactionServiceGrpc;
import org.anonymous.module.ObjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.anonymous.sql.Store.GET_NXT_TXN_ID;

public class TransactionServiceImpl extends TransactionServiceGrpc.TransactionServiceImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjServiceImpl.class);
    private static ObjectRepository objectRepository;

    TransactionServiceImpl(ObjectRepository objectRepositiory) {
        TransactionServiceImpl.objectRepository = objectRepositiory;
    }

    @Override
    public StreamObserver<CmdTransactionRequest> transaction(StreamObserver<TransMsgResponse> responseObserver) {
        return new StreamObserver<CmdTransactionRequest>() {
            long txnId=1L;
            Connection connection;
            {
                try {
                    connection = objectRepository.getConnectionObjRepo();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }

            @Override
            public void onNext(CmdTransactionRequest request) {
                try{
                    switch (request.getTransSeqValue()){
                        case 0:
                            LOGGER.info("got request insertHeader()");
                            /*
                            ResultSet resultSet = connection.prepareStatement(GET_NXT_TXN_ID)
                                    .executeQuery();
                            resultSet.next();
                            txnId = resultSet.getLong(1); */
                            break;
                        case 1:
                            LOGGER.info("got request insertRecord()");
                            if(!objectRepository.insertRec(connection, request.getInsert(), txnId)){
                                throw new SQLException();
                            }
                            break;
                        case 2:
                            LOGGER.info("got request updateRecord()");
                            if(!objectRepository.updateRec(connection, request.getUpdate(), txnId)){
                                throw new SQLException();
                            }
                            break;
                        case 3:
                            LOGGER.info("got request renameRecord()");
                            if(!objectRepository.renameDataRecords(connection, request.getRename(), txnId)){
                                throw new SQLException();
                            }
                            break;
                        case 4:
                            LOGGER.info("got request deleteRecord()");
                            if(!objectRepository.deleteDataRecords(connection, request.getDelete())) {
                                throw new SQLException();
                            }
                            break;
                        case 5:
                            LOGGER.info("got request commitTransaction() - end of transaction");
                            if(!objectRepository.commitTransaction(connection)){
                                throw new SQLException();
                            }
                    }

                    // insert trans log record - insert Header + tarns part
                } catch (SQLException sqlException) {
                    onError(sqlException);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                LOGGER.info("Error in transaction method " + throwable);
                try {
                    connection.rollback();
                } catch (SQLException throwables) {
                    LOGGER.info("connection rollback error");
                } finally {
                    try {
                        connection.close();
                    } catch (SQLException throwables) {
                        LOGGER.info("connection close error");
                    }
                }
                TransMsgResponse.Builder transMsgResponseBuilder = TransMsgResponse.newBuilder();
                TransMsgResponse.MsgOnFailure.NotSecSyncMessage notSecSyncMessage = TransMsgResponse.MsgOnFailure.NotSecSyncMessage.newBuilder()
                        .setAck(1).setTxnId((int) txnId).build();
                TransMsgResponse.MsgOnFailure msgOnFailure = TransMsgResponse.MsgOnFailure.newBuilder().setNotSecSyncMessage(notSecSyncMessage).build();
                transMsgResponseBuilder.setMsgOnFailure(msgOnFailure);
                responseObserver.onNext(transMsgResponseBuilder.build());
                responseObserver.onCompleted();
            }

            @Override
            public void onCompleted() {
                LOGGER.info("request.onCompleted() executed");
                try {
                    objectRepository.commitTransaction(connection);
                } catch (SQLException sqlException) {
                    LOGGER.info("commit failed");
                }
                try {
                    connection.close();
                } catch (SQLException throwables) {
                    LOGGER.info("connection close error");
                }
                TransMsgResponse.Builder transMsgResponseBuilder = TransMsgResponse.newBuilder();
                TransMsgResponse.MsgOnSuccess.NotSecSyncMessage notSecSyncMessage = TransMsgResponse.MsgOnSuccess.NotSecSyncMessage.newBuilder()
                        .setAck(0).setTxnId((int) txnId).build();
                TransMsgResponse.MsgOnSuccess msgOnSuccess = TransMsgResponse.MsgOnSuccess.newBuilder().setNotSecSyncMessage(notSecSyncMessage).build();
                transMsgResponseBuilder.setMsgOnSuccess(msgOnSuccess);
                responseObserver.onNext(transMsgResponseBuilder.build());
                responseObserver.onCompleted();
            }
        };
    }
}
