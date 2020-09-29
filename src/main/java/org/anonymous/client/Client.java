package org.anonymous.client;

import org.anonymous.grpc.*;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class Client {
    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(System.getProperty("host"), Integer.parseInt(System.getProperty("port"))).usePlaintext()
                .build();

        ObjServiceGrpc.ObjServiceBlockingStub stub= ObjServiceGrpc.newBlockingStub(channel);

        lookupByName(stub);
        stub.withWaitForReady();
        lookupByType(stub);

        channel.shutdown();
    }

    private static void lookupByType(ObjServiceGrpc.ObjServiceBlockingStub stub) {
        CmdNameLookupByTypeResponse response2= stub.lookupByType(CmdNameLookupByType.newBuilder().setCount(10).setGetType(GetType.METADATA_GET_FIRST).setMessageType(CmdType.CMD_NAME_LOOKUP_BY_TYPE).build());
        System.out.println("Response received from server:\n" + response2);
    }


    private static void lookupByName(ObjServiceGrpc.ObjServiceBlockingStub stub) {
        CmdLookupByNameResponse response= stub.lookupByName(CmdLookupByName.newBuilder().setCount(10).setMessageType(CmdType.CMD_NAME_LOOKUP).setSecurityNamePrefix("test").build());
        System.out.println("Response received from server:\n" + response);
    }
}