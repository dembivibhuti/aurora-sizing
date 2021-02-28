package org.anonymous.async.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;
import me.dinowernli.grpc.prometheus.Configuration;
import me.dinowernli.grpc.prometheus.MonitoringServerInterceptor;
import org.anonymous.async.db.Repository;
import org.anonymous.stats.MetricsServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;


//  mvn exec:java
//  -DroMaximumPoolSize=150
//  -DdataSource.user=postgres
//  -DdataSource.password=postgres
//  -DdataSource.databaseName=postgres
//  -DdataSource.portNumber=5432
//  -DdataSource.roserverName=database-1.cluster-cpw6mwbci5yo.us-east-1.rds.amazonaws.com
//  -Dport=8080
//  -Dexec.mainClass="org.anonymous.async.server.GrpcServer

public class GrpcServer {

    private static final Repository repository = new Repository();
    private static final Thread shutdownHook = new Thread(() -> System.out.println("shutting down ......"));
    private static Logger LOGGER = LoggerFactory.getLogger(GrpcServer.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Runtime.getRuntime().addShutdownHook(shutdownHook);

        try {

            int port = Integer.parseInt(System.getProperty("port"));

            MonitoringServerInterceptor monitoringInterceptor =
                    MonitoringServerInterceptor.create(Configuration.allMetrics());

            Server server = ServerBuilder.forPort(port)
                    .addService(ServerInterceptors.intercept(new ObjServiceImpl2(repository), monitoringInterceptor))
                    .build();

            LOGGER.info("Listening on {}", port);
            startMetricsServer();
            server.start();
            server.awaitTermination();

        } catch (Exception e) {
            LOGGER.error("unexpected error", e);
        } finally {
            repository.close();
        }
    }

    public static void startMetricsServer() {
        new Thread(() -> {

            try {
                int port = 9090;
                org.eclipse.jetty.server.Server server1 = new org.eclipse.jetty.server.Server(port);
                ServletContextHandler context = new ServletContextHandler();
                context.setContextPath("/");
                server1.setHandler(context);
                context.addServlet(new ServletHolder(new MetricsServlet()), "/metrics");
                server1.start();
                LOGGER.info("Metrics Server started on {}", port);
                server1.join();
            } catch (IOException e) {
                LOGGER.error("failed to start metrics server", e);
            } catch (Exception e) {
                LOGGER.error("failed to start metrics server", e);
            }
        }).start();
    }
}
