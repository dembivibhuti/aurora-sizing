package org.anonymous.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.anonymous.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;

public class Client {
    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);
    public ArrayList<String> list = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(System.getProperty("host"), Integer.parseInt(System.getProperty("port"))).usePlaintext()
                .build();


        ObjServiceGrpc.ObjServiceBlockingStub objSvcStub = ObjServiceGrpc.newBlockingStub(channel);
        connect(objSvcStub);
        objSvcStub.withWaitForReady();
        // Lookup calls
        lookupByName(objSvcStub, 100);
        objSvcStub.withWaitForReady();
        lookupByType(objSvcStub, 100);
        objSvcStub.withWaitForReady();
        lookupByNameStream(objSvcStub, 100);
        objSvcStub.withWaitForReady();
        lookupByTypeStream(objSvcStub, 100);
        objSvcStub.withWaitForReady();

        // Get Object Many Calls
        getObjectManyByName(objSvcStub);
        objSvcStub.withWaitForReady();
        getObjectManyByNameStream(objSvcStub);
        objSvcStub.withWaitForReady();
        getObjectManyByNameExt(objSvcStub);
        objSvcStub.withWaitForReady();
        getObjectManyByNameExtStream(objSvcStub);
        objSvcStub.withWaitForReady();

        // Get Object By Name Calls
        getObjectByName(objSvcStub);
        objSvcStub.withWaitForReady();
        getObjectByNameExt(objSvcStub);

        // Transaction Service
        CompletableFuture<Void> future = new CompletableFuture();
        TransactionServiceGrpc.TransactionServiceStub asyncStub = TransactionServiceGrpc.newStub(channel);
        transactionTest(asyncStub, future);
        future.join();
        channel.shutdown();

    }

    private static void connect(ObjServiceGrpc.ObjServiceBlockingStub stub) {
        CmdConnectResponse response = stub.connect(CmdConnect.newBuilder().setAppName("test").build());
        LOGGER.trace("Response received from connect: \n" + response);
    }

    private static void lookupByName(ObjServiceGrpc.ObjServiceBlockingStub stub, final int count) {
        CmdLookupByNameResponse response = stub.lookupByName(CmdLookupByName.newBuilder().setCount(count).setMessageType(CmdType.CMD_NAME_LOOKUP).setGetType(GetType.METADATA_GET_GREATER).setSecurityNamePrefix("test").build());
        System.out.println("Response received from lookupByName:\n" + response);
    }

    private static void lookupByNameStream(ObjServiceGrpc.ObjServiceBlockingStub stub, final int count) {
        CmdLookupByName request = CmdLookupByName.newBuilder().setCount(count).setMessageType(CmdType.CMD_NAME_LOOKUP).setGetType(GetType.METADATA_GET_GREATER).setSecurityNamePrefix("test").build();
        Iterator<CmdLookupByNameResponseStream> it;
        try {
            it = stub.lookupByNameStream(request);
            System.out.println("Response received from lookupByNameStream:");
            while (it.hasNext()) {
                CmdLookupByNameResponseStream response = it.next();
                String objname = response.getSecurityName();
                System.out.println(objname);
            }
        } catch (Exception e) {
            LOGGER.error("Caught exception in Streaming Server-side Lookup by name stream", e);
        }
    }

    private static void lookupByType(ObjServiceGrpc.ObjServiceBlockingStub stub, final int count) {
        CmdNameLookupByTypeResponse response2 = stub.lookupByType(CmdNameLookupByType.newBuilder().setCount(count).setMessageType(CmdType.CMD_NAME_LOOKUP_BY_TYPE).setGetType(GetType.METADATA_GET_GREATER).setSecurityType(0).setSecurityNamePrefix("test").build());
        System.out.println("Response received from lookupByType: \n" + response2);
    }

    private static void lookupByTypeStream(ObjServiceGrpc.ObjServiceBlockingStub stub, final int count) {
        CmdNameLookupByType request = CmdNameLookupByType.newBuilder().setCount(count).setGetType(GetType.METADATA_GET_GREATER).setSecurityType(0).setMessageType(CmdType.CMD_NAME_LOOKUP_BY_TYPE).setSecurityNamePrefix("test").build();
        Iterator<CmdNameLookupByTypeResponseStream> it;
        try {
            it = stub.lookupByTypeStream(request);
            System.out.println("Response received from lookupByTypeStream:");
            while (it.hasNext()) {
                CmdNameLookupByTypeResponseStream response = it.next();
                String objname = response.getSecurityName();
                System.out.println(objname);
            }
        } catch (Exception e) {
            LOGGER.error("Caught exception in Streaming Server-side Lookup by type stream", e);
        }
    }

    private static void getObjectManyByName(ObjServiceGrpc.ObjServiceBlockingStub stub) {
        CmdGetManyByNameResponse response = stub.getObjectManyByName(CmdGetManyByName.newBuilder().addSecurityNames("testSec-0").addSecurityNames("testSec-1").addSecurityNames("testSec-2").build());
        System.out.println("Response received from getObjectManyByName:\n" + response.getSecurityCount());
    }

    private static void getObjectManyByNameStream(ObjServiceGrpc.ObjServiceBlockingStub stub) {
        CmdGetManyByName request = CmdGetManyByName.newBuilder().addSecurityNames("testSec-0").addSecurityNames("testSec-1").addSecurityNames("testSec-2").build();
        try {
            Iterator<CmdGetManyByNameResponseStream> response = stub.getObjectManyByNameStream(request);
            System.out.println("Response received from getObjectManyByNameStream:");
            while (response.hasNext()) {
                System.out.println(response.next().getMessageResponseCase()); //gives 0 on success
            }
        } catch (Exception e) {
            LOGGER.error("Caught exception in Streaming Server-side Get Many by Name stream", e);
        }
    }

    private static void getObjectManyByNameExt(ObjServiceGrpc.ObjServiceBlockingStub stub) {
        CmdGetManyByNameExtResponse response = stub.getObjectManyByNameExt(CmdGetManyByNameExt.newBuilder().addSecurityNames("testSec-0").addSecurityNames("testSec-1").addSecurityNames("testSec-2").build());
        System.out.println("Response received from getObjectManyByNameExt: \n" + response);
    }

    private static void getObjectManyByNameExtStream(ObjServiceGrpc.ObjServiceBlockingStub stub) {
        CmdGetManyByNameExt request= CmdGetManyByNameExt.newBuilder().addSecurityNames("testSec-0").addSecurityNames("testSec-1").addSecurityNames("testSec-2").build();
        try {
            Iterator<CmdGetManyByNameExtResponseStream> response = stub.getObjectManyByNameExtStream(request);
            System.out.println("Response received from getObjectManyByNameExtStream:");
            while (response.hasNext()) {
                System.out.println(response.next().getMsgOnSuccess().getHasSucceeded());
            }
        }catch (Exception e) {
            LOGGER.error("Caught exception in Streaming Server-side Get Many by Name Ext stream", e);
        }
    }

    private static void getObjectByNameExt(ObjServiceGrpc.ObjServiceBlockingStub stub) {
        CmdGetByNameExtResponse response = stub.getObjectExt(CmdGetByNameExt.newBuilder().setMsgType(CmdType.CMD_GET_BY_NAME).setSecurityName("testSec-0").build());
        System.out.println("Response received from getObjectByNameExt: \n" + response);
    }
    private static void getObjectByName(ObjServiceGrpc.ObjServiceBlockingStub stub) {
        CmdGetByNameResponse response = stub.getObject(CmdGetByName.newBuilder().setMsgType(CmdType.CMD_GET_BY_NAME).setSecurityName("testSec-0").build());
        System.out.println("Response received from getObjectByName: \n" + response.getSecurity());
    }

    private static void transactionTest(TransactionServiceGrpc.TransactionServiceStub stub, CompletableFuture<Void> future) throws InterruptedException {
        StreamObserver<TransMsgResponse> streamObserver = new StreamObserver<TransMsgResponse>() {
            @Override
            public void onNext(TransMsgResponse transMsgResponse) {
                LOGGER.info("Response Received from server:");
                System.out.println(transMsgResponse);
            }

            @Override
            public void onError(Throwable throwable) {
                LOGGER.info("Error in response");
                future.completeExceptionally(throwable);
            }

            @Override
            public void onCompleted() {
                LOGGER.info("Transaction Completed");
                LOGGER.info("Thread Name " + Thread.currentThread().getName());
                future.complete(null);
            }
        };
        StreamObserver<CmdTransactionRequest> requestObserver = stub.transaction(streamObserver);
        // request generated for header
        CmdTransHeader header = CmdTransHeader.newBuilder().setTransName("test-transaction").build();
        requestObserver.onNext(CmdTransactionRequest.newBuilder().setHeader(header).setTransSeqValue(0).build());
        Thread.sleep(500);

        // request generated for insert
        Metadata sdb = Metadata.newBuilder().setSecurityName("newRecord").setTimeUpdate("0001-01-01 00:00:00").
                setLastTxnId(11111).setDbIdUpdated(7).setUpdateCount(5).setVersionInfo(11).build();
        CmdInsert insert = CmdInsert.newBuilder().setMetadata(sdb).build();
        requestObserver.onNext(CmdTransactionRequest.newBuilder().setInsert(insert).setTransSeqValue(1).build());
        Thread.sleep(500);

        //request generated for update
        Metadata sdb1 = Metadata.newBuilder().setSecurityName("newRecord").setTimeUpdate("0001-01-01 00:00:00").
                setLastTxnId(11111).setDbIdUpdated(7).setUpdateCount(5).setVersionInfo(11).build();
        Metadata sdb2 = Metadata.newBuilder().setSecurityName("newRecord").setTimeUpdate("0002-02-02 00:00:00").setLastTxnId(222222).setVersionInfo(22)
                .setDbIdUpdated(17).setUpdateCount(15).setVersionInfo(10).build();
        CmdUpdate update = CmdUpdate.newBuilder().setOldMetadata(sdb1).setNewMetadata(sdb2).build();
        requestObserver.onNext(CmdTransactionRequest.newBuilder().setUpdate(update).setTransSeqValue(2).build());
        Thread.sleep(500);

        //request generated for rename
        Metadata oldSdb = Metadata.newBuilder().setSecurityName("newRecord").setTimeUpdate("0002-02-02 00:00:00").setLastTxnId(222222).setVersionInfo(22)
                .setDbIdUpdated(17).setUpdateCount(15).setVersionInfo(10).build();
        Metadata newSdb = Metadata.newBuilder().setSecurityName("renamedRecord").setDbIdUpdated(4).setUpdateCount(2).setVersionInfo(7).setLastTxnId(33333).setDateCreated(8).build();
        CmdRename renameData = CmdRename.newBuilder().setOldMetadata(oldSdb).setNewMetadata(newSdb).build();
        requestObserver.onNext(CmdTransactionRequest.newBuilder().setRename(renameData).setTransSeqValue(3).build());
        Thread.sleep(500);

        //request generated for delete
        Metadata sdb4 = Metadata.newBuilder().setSecurityName("renamedRecord").setTimeUpdate("0").setVersionInfo(7).setUpdateCount(2)
                .setDbIdUpdated(4).setLastTxnId(33333).build();
        CmdDelete deleteData = CmdDelete.newBuilder().setMetadata(sdb4).build();
        requestObserver.onNext(CmdTransactionRequest.newBuilder().setDelete(deleteData).setTransSeqValue(4).build());
        Thread.sleep(500);

        // request generated for trailer
        CmdTrailer trailer = CmdTrailer.newBuilder().build();
        requestObserver.onNext(CmdTransactionRequest.newBuilder().setTrailer(trailer).setTransSeqValue(5).build());
        requestObserver.onCompleted();
    }
}