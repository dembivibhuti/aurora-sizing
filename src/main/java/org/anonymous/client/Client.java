package org.anonymous.client;

import org.anonymous.connection.ConnectionProvider;
import org.anonymous.grpc.*;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.anonymous.module.ObjectRepository;
import org.anonymous.util.TimeKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class Client {
    static Logger logger = LoggerFactory.getLogger(Client.class);
    public static void main(String[] args) {

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(System.getProperty("host"), Integer.parseInt(System.getProperty("port"))).usePlaintext()
                .build();

        ConnectionProvider connectionProvider = new ConnectionProvider();
        new ObjectRepository(null, connectionProvider).runDDL(false);
        new ObjectRepository(null, connectionProvider).load(1, 1 , new TimeKeeper());

        ObjServiceGrpc.ObjServiceBlockingStub stub = ObjServiceGrpc.newBlockingStub(channel);

        CmdGetManyByNameResponse response = stub.getObjectManyByName(CmdGetManyByName.newBuilder().addSecurityName("testSec-1418335106-0").build());

        logger.info("Response received from server:\n" + response);

        channel.shutdown();
    }
}