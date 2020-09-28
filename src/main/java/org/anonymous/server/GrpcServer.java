package org.anonymous.server;

import java.io.IOException;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.anonymous.connection.ConnectionProvider;
import org.anonymous.module.ObjectRepository;
import org.anonymous.util.TimeKeeper;

public class GrpcServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        try (ConnectionProvider connectionProvider = new ConnectionProvider()) {
            ObjectRepository objectRepositiory = new ObjectRepository(connectionProvider, connectionProvider);
            objectRepositiory.runDDL(false);
            objectRepositiory.load(1, 1 , new TimeKeeper()).join();


            Server server = ServerBuilder.forPort(Integer.parseInt(System.getProperty("port")))
                    .addService(new ObjectServiceImpl()).addService(new ObjServiceImpl(connectionProvider)).build();


            System.out.println("Starting server...");
            server.start();
            System.out.println("Server started!");
            server.awaitTermination();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
