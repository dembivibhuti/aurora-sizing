package org.anonymous.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.anonymous.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;

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
        lookupByNameStream(objSvcStub, 10000);
        objSvcStub.withWaitForReady();
        lookupByNameStreamAll(objSvcStub);
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

        TransactionServiceGrpc.TransactionServiceBlockingStub transSvcStub = TransactionServiceGrpc.newBlockingStub(channel);

        insertRecordTest(transSvcStub);
        transSvcStub.withWaitForReady();
        updateRecordTest(transSvcStub);
        transSvcStub.withWaitForReady();
        renameRecordTest(transSvcStub);
        transSvcStub.withWaitForReady();
        deleteRecordTest(transSvcStub);

        channel.shutdown();

    }

    private static void connect(ObjServiceGrpc.ObjServiceBlockingStub stub) {
        CmdConnectResponse response = stub.connect(CmdConnect.newBuilder().setAppName("test").build());
        LOGGER.info("Response received from connect: \n" + response);
    }

    private static void insertRecordTest(TransactionServiceGrpc.TransactionServiceBlockingStub stub) {
        Metadata sdb = Metadata.newBuilder().setSecurityName("newRecord").setTimeUpdate("0001-01-01 00:00:00").
                setLastTxnId(11111).setDbIdUpdated(7).setUpdateCount(5).setVersionInfo(11).build();
        CmdInsertResponse response = stub.insertRecord(CmdInsert.newBuilder().setSdbDisk(sdb).build());
        LOGGER.info("Response received from insertRecordTest: Ack is...... \n" + response);
    }
    private static void updateRecordTest(TransactionServiceGrpc.TransactionServiceBlockingStub stub) {
        Metadata sdb1 = Metadata.newBuilder().setSecurityName("newRecord").setTimeUpdate("0001-01-01 00:00:00").
                setLastTxnId(11111).setDbIdUpdated(7).setUpdateCount(5).setVersionInfo(11).build();
        Metadata sdb2 = Metadata.newBuilder().setSecurityName("newRecord").setTimeUpdate("0002-02-02 00:00:00").setLastTxnId(222222).setVersionInfo(22)
                .setDbIdUpdated(17).setUpdateCount(15).setVersionInfo(10).build();
        CmdUpdateResponse response = stub.updateRecord(CmdUpdate.newBuilder().setOldSdbDisk(sdb1).setNewSdbDisk(sdb2).build());
        LOGGER.info("Response received from updateRecordTest: Ack is...... \n" + response);
    }

    private static void deleteRecordTest(TransactionServiceGrpc.TransactionServiceBlockingStub stub) {
        Metadata sdb = Metadata.newBuilder().setSecurityName("renamedRecord").setTimeUpdate("0").setVersionInfo(7).setUpdateCount(2)
                .setDbIdUpdated(4).setLastTxnId(33333).build();
        CmdDeleteDataResponse response = stub.deleteRecord(CmdDeleteData.newBuilder().setMetadata(sdb).build());
       LOGGER.info("Response received from deleteRecordTest: \n" + response);
    }

    private static void renameRecordTest(TransactionServiceGrpc.TransactionServiceBlockingStub stub) {
        Metadata oldSdb = Metadata.newBuilder().setSecurityName("newRecord").setTimeUpdate("0002-02-02 00:00:00").setLastTxnId(222222).setVersionInfo(22)
                .setDbIdUpdated(17).setUpdateCount(15).setVersionInfo(10).build();
        Metadata newSdb = Metadata.newBuilder().setSecurityName("renamedRecord").setDbIdUpdated(4).setUpdateCount(2).setVersionInfo(7).setLastTxnId(33333).setDateCreated(8).build();
        CmdRenameDataResponse response = stub.renameRecord(CmdRenameData.newBuilder().setOldMetadata(oldSdb).setNewMetadata(newSdb).build());
        LOGGER.info("Response received from renameRecordTest: \n" + response);
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
            LOGGER.info("Caught exception in Streaming Server-side Lookup by name stream", e);
        }
    }


    private static void lookupByNameStreamAll(ObjServiceGrpc.ObjServiceBlockingStub stub) {
        CmdLookupByName request = CmdLookupByName.newBuilder().setCount(0).setMessageType(CmdType.CMD_NAME_LOOKUP).setGetType(GetType.METADATA_GET_GREATER).setSecurityNamePrefix("test").build();
        Iterator<CmdLookupByNameResponseStream> it;
        try {
            it = stub.lookupByNameStream(request);
            System.out.println("Response received from lookupByNameStreamALL:");
            while (it.hasNext()) {
                CmdLookupByNameResponseStream response = it.next();
                String objname = response.getSecurityName();
                System.out.println(objname);
            }
        } catch (Exception e) {
            LOGGER.info("Caught exception in Streaming Server-side Lookup by name stream ALL", e);
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
            LOGGER.info("Caught exception in Streaming Server-side Lookup by type stream", e);
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
            LOGGER.info("Caught exception in Streaming Server-side Get Many by Name stream", e);
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
            LOGGER.info("Caught exception in Streaming Server-side Get Many by Name Ext stream", e);
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
}