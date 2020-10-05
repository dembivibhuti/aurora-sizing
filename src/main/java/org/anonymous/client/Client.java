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


        ObjServiceGrpc.ObjServiceBlockingStub stub = ObjServiceGrpc.newBlockingStub(channel);
        connect(stub);
        stub.withWaitForReady();
        // Lookup calls
        lookupByName(stub);
        stub.withWaitForReady();
        lookupByType(stub);
        stub.withWaitForReady();
        lookupByNameStream(stub);
        stub.withWaitForReady();
        lookupByTypeStream(stub);
        stub.withWaitForReady();

        // Get Object Many Calls
        getObjectManyByName(stub);
        stub.withWaitForReady();
        getObjectManyByNameStream(stub);
        stub.withWaitForReady();
        getObjectManyByNameExt(stub);
        stub.withWaitForReady();
        getObjectManyByNameExtStream(stub);
        stub.withWaitForReady();

        // Get Object By Name Calls
        getObjectByName(stub);
        stub.withWaitForReady();
        getObjectByNameExt(stub);

        channel.shutdown();
    }

    private static void connect(ObjServiceGrpc.ObjServiceBlockingStub stub) {
        CmdConnectResponse response = stub.connect(CmdConnect.newBuilder().setAppName("test").build());
        System.out.println("Response received from connect: \n" + response);
    }

    private static void lookupByName(ObjServiceGrpc.ObjServiceBlockingStub stub) {
        CmdLookupByNameResponse response = stub.lookupByName(CmdLookupByName.newBuilder().setCount(10).setMessageType(CmdType.CMD_NAME_LOOKUP).setGetType(GetType.METADATA_GET_GREATER).setSecurityNamePrefix("test").build());
        System.out.println("Response received from lookupByName:\n" + response);
    }

    private static void lookupByNameStream(ObjServiceGrpc.ObjServiceBlockingStub stub) {
        CmdLookupByName request = CmdLookupByName.newBuilder().setCount(10).setMessageType(CmdType.CMD_NAME_LOOKUP).setGetType(GetType.METADATA_GET_GREATER).setSecurityNamePrefix("test").build();
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

    private static void lookupByType(ObjServiceGrpc.ObjServiceBlockingStub stub) {
        CmdNameLookupByTypeResponse response2 = stub.lookupByType(CmdNameLookupByType.newBuilder().setCount(10).setMessageType(CmdType.CMD_NAME_LOOKUP_BY_TYPE).setGetType(GetType.METADATA_GET_GREATER).setSecurityType(0).build());
        System.out.println("Response received from lookupByType: \n" + response2);
    }

    private static void lookupByTypeStream(ObjServiceGrpc.ObjServiceBlockingStub stub) {
        CmdNameLookupByType request = CmdNameLookupByType.newBuilder().setCount(10).setGetType(GetType.METADATA_GET_GREATER).setSecurityType(0).setMessageType(CmdType.CMD_NAME_LOOKUP_BY_TYPE).build();
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
        CmdGetByNameExtResponse response = stub.getObjectExt(CmdGetByNameExt.newBuilder().setMsgType(CmdType.CMD_GET_BY_NAME).setSecurityName("testSec-10-0").build());
        System.out.println("Response received from getObjectByNameExt: \n" + response);
    }
    private static void getObjectByName(ObjServiceGrpc.ObjServiceBlockingStub stub) {
        CmdGetByNameResponse response = stub.getObject(CmdGetByName.newBuilder().setMsgType(CmdType.CMD_GET_BY_NAME).setSecurityName("testSec-10-0").build());
        System.out.println("Response received from getObjectByName: \n" + response);
    }
}