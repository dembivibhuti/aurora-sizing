package org.anonymous.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;
import io.prometheus.client.exporter.MetricsServlet;
import me.dinowernli.grpc.prometheus.Configuration;
import me.dinowernli.grpc.prometheus.MonitoringServerInterceptor;
import org.anonymous.connection.ConnectionProviderHolder;
import org.anonymous.connection.HikariCPConnectionProvider;
import org.anonymous.connection.SimpleJDBCConnectionProvider;
import org.anonymous.connection.TomcatJDBCConnectionProvider;
import org.anonymous.module.ObjectRepository;
import org.anonymous.util.TimeKeeper;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.anonymous.connection.HikariCPConnectionProvider.isInMemDB;

public class GrpcServer {

    private static Logger LOGGER = LoggerFactory.getLogger(GrpcServer.class);

    private static ConnectionProviderHolder connectionProviderHolder;
    private static final Thread shutdownHook = new Thread(() -> connectionProviderHolder.close());

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(shutdownHook);

        switch (System.getProperty("dataSource.poolType")) {
            case "hikaricp":
                connectionProviderHolder = HikariCPConnectionProvider.create();
                break;
            case "tomcatjdbc":
                connectionProviderHolder = TomcatJDBCConnectionProvider.create();
                break;
            case "simple":
                connectionProviderHolder = SimpleJDBCConnectionProvider.create();
            default:
                throw new RuntimeException(" -DdataSource.poolType is a mandatory arguement ");
        }

        try {
            ObjectRepository objectRepositiory = new ObjectRepository(connectionProviderHolder.roConnectionProvider, connectionProviderHolder.rwConnectionProvider);

            if (isInMemDB()) {
                LOGGER.info("Starting in-Mem DB Mode");
                objectRepositiory.runDDL(false);
                TimeKeeper timekeeper = new TimeKeeper("load", false);
                objectRepositiory.load(6, 6, 32000, timekeeper).join();
            } else {
                LOGGER.info("Starting in Aurora Mode");
            }


            int port = Integer.parseInt(System.getProperty("port"));

            MonitoringServerInterceptor monitoringInterceptor =
                    MonitoringServerInterceptor.create(Configuration.allMetrics());

            Server server = ServerBuilder.forPort(port)
                    .addService(ServerInterceptors.intercept(new ObjServiceImpl(objectRepositiory), monitoringInterceptor))
                    .addService(ServerInterceptors.intercept(new TransactionServiceImpl(objectRepositiory), monitoringInterceptor))
                    .build();

            /*Server server = ServerBuilder.forPort(port)
                    .addService(new ObjServiceImpl(objectRepositiory))
                    .addService(new TransactionServiceImpl(objectRepositiory))
                    .build();*/

            LOGGER.info("Listening on {}", port);

            startMetricsServer();

            server.start();
            server.awaitTermination();
        } catch (Exception e) {
            LOGGER.error("unexpected error", e);
        } finally {
            connectionProviderHolder.close();
        }
    }

    private static void startMetricsServer() {
        new Thread(() -> {

            try {
                int port = 9090;
                org.eclipse.jetty.server.Server server1 = new org.eclipse.jetty.server.Server(port);
                ServletContextHandler context = new ServletContextHandler();
                context.setContextPath("/");
                server1.setHandler(context);
                context.addServlet(new ServletHolder(new MetricsServlet()), "/metrics");
                server1.start();
                LOGGER.info("Metrics Server parted on {}", port);
                server1.join();
            } catch (IOException e) {
                LOGGER.error("failed to start metrics server", e);
            } catch (Exception e) {
                LOGGER.error("failed to start metrics server", e);
            }
        }).start();
    }

}
