package org.anonymous.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.anonymous.grpc.CmdGetByName;
import org.anonymous.grpc.CmdGetByNameResponse;
import org.anonymous.grpc.CmdType;
import org.anonymous.grpc.ObjServiceGrpc;

public class Client {
    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(System.getProperty("host"), Integer.parseInt(System.getProperty("port"))).usePlaintext()
                .build();

        ObjServiceGrpc.ObjServiceBlockingStub stub= ObjServiceGrpc.newBlockingStub(channel);

        CmdGetByNameResponse response = stub.getObject(CmdGetByName.newBuilder().setMsgType(CmdType.CMD_GET_BY_NAME).setSecurityName("testSec-10-0").build());

        System.out.println("Response received from server:\n" + response);

        channel.shutdown();
    }
}