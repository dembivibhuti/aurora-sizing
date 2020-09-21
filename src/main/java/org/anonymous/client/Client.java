package org.anonymous.client;

import org.anonymous.grpc.ObjectRequest;
import org.anonymous.grpc.ObjectResponse;
import org.anonymous.grpc.ObjectServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class Client {
    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(System.getProperty("host"), Integer.parseInt(System.getProperty("port"))).usePlaintext()
                .build();

        ObjectServiceGrpc.ObjectServiceBlockingStub stub = ObjectServiceGrpc.newBlockingStub(channel);

        ObjectResponse response = stub.exists(ObjectRequest.newBuilder().setName("phantom").setTypeId(212).build());

        System.out.println("Response received from server:\n" + response);

        channel.shutdown();
    }
}