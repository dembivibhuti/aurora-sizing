package org.anonymous.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.anonymous.connection.ConnectionProvider;
import org.anonymous.module.ObjectRepository;
import org.anonymous.util.TimeKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.anonymous.connection.ConnectionProvider.isInMemDB;

public class GrpcServer {

    private static Logger LOGGER= LoggerFactory.getLogger(GrpcServer.class);

    public static void main(String[] args) {
        try (ConnectionProvider.Holder holder = ConnectionProvider.create()) {

            ObjectRepository objectRepositiory = new ObjectRepository(holder.roConnectionProvider, holder.rwConnectionProvider);


            if ( isInMemDB()) {
                objectRepositiory.runDDL(false);
                TimeKeeper timekeeper = new TimeKeeper();
                //changed for testing purpose
                objectRepositiory.load(4, 1, timekeeper).join();
            }

            int port = Integer.parseInt(System.getProperty("port"));
            Server server = ServerBuilder.forPort(port)
                    .addService(new ObjServiceImpl(objectRepositiory)).build();

            LOGGER.info("Listening on {}", port);
            server.start();

            server.awaitTermination();
        } catch (Exception e) {
            LOGGER.error("unexpected error", e);
        }
    }


}
