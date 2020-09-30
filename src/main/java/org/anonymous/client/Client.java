package org.anonymous.client;

import org.anonymous.connection.ConnectionProvider;
import org.anonymous.grpc.*;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.anonymous.module.ObjectRepository;
import org.anonymous.util.TimeKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;


public class Client {
    static Logger logger = LoggerFactory.getLogger(Client.class);
    public static void main(String[] args) {

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(System.getProperty("host"), Integer.parseInt(System.getProperty("port"))).usePlaintext()
                .build();


        ObjServiceGrpc.ObjServiceBlockingStub stub = ObjServiceGrpc.newBlockingStub(channel);
        Iterator<CmdGetManyByNameResponseStream> response = stub.getObjectManyByNameStream(CmdGetManyByName.newBuilder().addSecurityName("test0").addSecurityName("test1").addSecurityName("test2").build());

        while (response.hasNext()) {
            logger.info("Response received from server:\n" + response.next());
        }

        channel.shutdown();
    }
}