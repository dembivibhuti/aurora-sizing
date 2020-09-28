package org.anonymous.client;

import org.anonymous.connection.ConnectionProvider;
import org.anonymous.grpc.*;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.anonymous.module.ObjectRepository;
import org.anonymous.util.TimeKeeper;

public class Client {
    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(System.getProperty("host"), Integer.parseInt(System.getProperty("port"))).usePlaintext()
                .build();

//        ObjectServiceGrpc.ObjectServiceBlockingStub stub = ObjectServiceGrpc.newBlockingStub(channel);

        ConnectionProvider connectionProvider = new ConnectionProvider();
        new ObjectRepository(null, connectionProvider).runDDL(false);
        new ObjectRepository(null, connectionProvider).load(1, 1 , new TimeKeeper());

//        SecSrvSvcGrpc.SecSrvSvcBlockingStub stub = SecSrvSvcGrpc.newBlockingStub(channel);

        ObjServiceGrpc.ObjServiceBlockingStub stub = ObjServiceGrpc.newBlockingStub(channel);

        CmdGetManyByNameResponse response = stub.getObjectManyByName(CmdGetManyByName.newBuilder().addSecurityName("testSec-1418335106-0").build());

        System.out.println("Response received from server:\n" + response);

        channel.shutdown();
    }
}