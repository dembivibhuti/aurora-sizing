package org.anonymous.server;

import java.io.IOException;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.anonymous.connection.ConnectionProvider;
import org.anonymous.module.ObjectRepository;
import org.anonymous.util.TimeKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GrpcServer {
    private static Logger LOGGER= LoggerFactory.getLogger(GrpcServer.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        try (ConnectionProvider connectionProvider = new ConnectionProvider()) {
            ObjectRepository objectRepositiory = new ObjectRepository(connectionProvider, connectionProvider);
            objectRepositiory.runDDL(false);
            TimeKeeper timekeeper = new TimeKeeper();
            objectRepositiory.load(6, 6, timekeeper).join();
            Server server = ServerBuilder.forPort(Integer.parseInt(System.getProperty("port")))
                    .addService(new ObjectServiceImpl())
                    .addService(new ObjServiceImpl(connectionProvider, objectRepositiory, timekeeper)).build();

            LOGGER.info("Starting server...");
            server.start();
            LOGGER.info("Server started!");
            server.awaitTermination();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
