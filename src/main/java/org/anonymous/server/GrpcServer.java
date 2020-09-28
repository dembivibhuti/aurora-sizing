package org.anonymous.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.anonymous.connection.ConnectionProvider;
import org.anonymous.module.ObjectRepository;
import org.anonymous.util.TimeKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GrpcServer {
    private static Logger LOGGER= LoggerFactory.getLogger(GrpcServer.class);
    public static void main(String[] args) throws IOException, InterruptedException {
        try (ConnectionProvider connectionProvider = new ConnectionProvider()) {
            ObjectRepository objectRepository = new ObjectRepository(connectionProvider, connectionProvider);
            objectRepository.runDDL(false);
            TimeKeeper timekeeper = new TimeKeeper();
            objectRepository.load(4, 1, timekeeper).join();
            Server server = ServerBuilder.forPort(Integer.parseInt(System.getProperty("port")))
                    .addService(new ObjectServiceImpl()).addService(new ObjServiceImpl(objectRepository)).build();

            LOGGER.info("Starting server...");
            server.start();
            LOGGER.info("Server started!");
            server.awaitTermination();

        } catch (Exception e) {
            LOGGER.error("Caught Exception");
            e.printStackTrace();
        }

    }
}
