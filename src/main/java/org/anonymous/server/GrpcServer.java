package org.anonymous.server;

import java.io.IOException;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class GrpcServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(Integer.parseInt(System.getProperty("port")))
                .addService(new ObjectServiceImpl()).addService(new ObjServiceImpl()).build();
        ;

        System.out.println("Starting server...");
        server.start();
        System.out.println("Server started!");
        server.awaitTermination();
    }
}
