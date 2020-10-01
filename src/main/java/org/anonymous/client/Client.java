package org.anonymous.client;

import org.anonymous.grpc.ObjectRequest;
import org.anonymous.grpc.ObjectResponse;
import org.anonymous.grpc.ObjectServiceGrpc;
import org.anonymous.grpc.*;

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
        lookupByName(stub);
        stub.withWaitForReady();
        lookupByType(stub);
        stub.withWaitForReady();
        lookupByNameStream(stub);
        stub.withWaitForReady();
        lookupByTypeStream(stub);
        stub.withWaitForReady();
        getObjectManyByNameStream(stub);
        channel.shutdown();
    }

    private static void connect(ObjServiceGrpc.ObjServiceBlockingStub stub) {
        CmdConnectResponse response = stub.connect(CmdConnect.newBuilder().setAppName("test").build());
        System.out.println("Response received from server:\n" + response);
    }

    private static void lookupByName(ObjServiceGrpc.ObjServiceBlockingStub stub) {
        CmdLookupByNameResponse response = stub.lookupByName(CmdLookupByName.newBuilder().setCount(10).setMessageType(CmdType.CMD_NAME_LOOKUP).setSecurityNamePrefix("test").build());
        System.out.println("Response received from server:\n" + response);
    }

    private static void lookupByNameStream(ObjServiceGrpc.ObjServiceBlockingStub stub) {
        CmdLookupByName request = CmdLookupByName.newBuilder().setCount(10).setMessageType(CmdType.CMD_NAME_LOOKUP).setSecurityNamePrefix("test").build();
        Iterator<CmdLookupByNameResponseStream> it;
        try {
            it = stub.lookupByNameStream(request);
            System.out.println("Response received from server:");
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
        CmdNameLookupByTypeResponse response2 = stub.lookupByType(CmdNameLookupByType.newBuilder().setCount(10).setGetType(GetType.METADATA_GET_FIRST).setMessageType(CmdType.CMD_NAME_LOOKUP_BY_TYPE).build());
        System.out.println("Response received from server:\n" + response2);
    }

    private static void lookupByTypeStream(ObjServiceGrpc.ObjServiceBlockingStub stub) {
        CmdNameLookupByType request = CmdNameLookupByType.newBuilder().setCount(10).setGetType(GetType.METADATA_GET_FIRST).setMessageType(CmdType.CMD_NAME_LOOKUP_BY_TYPE).build();
        Iterator<CmdNameLookupByTypeResponseStream> it;
        try {
            it = stub.lookupByTypeStream(request);
            System.out.println("Response received from server:");
            while (it.hasNext()) {
                CmdNameLookupByTypeResponseStream response = it.next();
                String objname = response.getSecurityName();
                System.out.println(objname);
            }
        } catch (Exception e) {
            LOGGER.info("Caught exception in Streaming Server-side Lookup by type stream", e);
        }
    }

    private static void getObjectManyByNameStream(ObjServiceGrpc.ObjServiceBlockingStub stub) {
        Iterator<CmdGetManyByNameResponseStream> response = stub.getObjectManyByNameStream(CmdGetManyByName.newBuilder().addSecurityName("test0").addSecurityName("test1").addSecurityName("test2").build());
        while (response.hasNext()) {
            logger.info("Response received from server:\n" + response.next());
        }
//        Iterator<CmdGetManyByNameResponseStream> response = stub.getObjectManyByNameStream(CmdGetManyByName.newBuilder().addSecurityName("test0").addSecurityName("test1").addSecurityName("test2").build());
//
//        while (response.hasNext()) {
//            logger.info("Response received from server:\n" + response.next());
//        }


        CmdGetManyByNameExtResponse response = stub.getObjectManyByNameExt(CmdGetManyByNameExt.newBuilder().addSecurityNames("test0").addSecurityNames("test1").addSecurityNames("test2").build());
        logger.info("Response received from server:\n" + response);

//        Iterator<CmdGetManyByNameExtResponseStream> response = stub.getObjectManyByNameExtStream(CmdGetManyByNameExt.newBuilder().addSecurityNames("test0").addSecurityNames("test1").addSecurityNames("test2").build());
//        while (response.hasNext()) {
//            logger.info("Response received from server:\n" + response.next());
//        }

        channel.shutdown();
    }
}