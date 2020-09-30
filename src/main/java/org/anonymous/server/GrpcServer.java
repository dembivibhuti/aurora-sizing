package org.anonymous.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.anonymous.client.Client;
import org.anonymous.connection.ConnectionProvider;
import org.anonymous.module.ObjectRepository;
import org.anonymous.util.TimeKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrpcServer {
    static Logger logger = LoggerFactory.getLogger(Client.class);
    public static void main(String[] args) {
        try (ConnectionProvider connectionProvider = new ConnectionProvider()) {
            ObjectRepository objectRepository = new ObjectRepository(connectionProvider, connectionProvider);
            objectRepository.runDDL(false);
            objectRepository.load(3, 1 , new TimeKeeper());

            Server server = ServerBuilder.forPort(Integer.parseInt(System.getProperty("port")))
                    .addService(new ObjectServiceImpl()).addService(new ObjServiceImpl(objectRepository)).build();
            logger.info("Starting server...");
            server.start();
            logger.info("Server started!");
            server.awaitTermination();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
