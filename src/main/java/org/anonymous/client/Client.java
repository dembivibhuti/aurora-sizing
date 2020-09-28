package org.anonymous.client;

import org.anonymous.grpc.*;
import org.anonymous.server.GrpcServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.grpc.ManagedChannelBuilder;

public class Client {
    private static Logger LOGGER= LoggerFactory.getLogger(Client.class);
    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(System.getProperty("host"), Integer.parseInt(System.getProperty("port"))).usePlaintext()
                .build();

        ObjServiceGrpc.ObjServiceBlockingStub stub= ObjServiceGrpc.newBlockingStub(channel);
        CmdLookupByNameResponse response= stub.lookupByName(CmdLookupByName.newBuilder().setCount(10).setMessageType(CmdType.CMD_NAME_LOOKUP).setSecurityNamePrefix("test").build());
        System.out.println("Response received from server:\n" + response);

        stub.withWaitForReady();

        // lookup by type id
        CmdNameLookupByTypeResponse response2= stub.lookupByType(CmdNameLookupByType.newBuilder().setCount(10).setGetType(GetType.METADATA_GET_FIRST).setMessageType(CmdType.CMD_NAME_LOOKUP_BY_TYPE).build());
        System.out.println("Response received from server:\n" + response2);


        channel.shutdown();
    }
}