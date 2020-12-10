package org.anonymous.server;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.prometheus.client.exporter.MetricsServlet;
import org.anonymous.connection.ConnectionProvider;
import org.anonymous.module.ObjectRepository;
import org.anonymous.util.TimeKeeper;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import static org.anonymous.connection.ConnectionProvider.isInMemDB;

public class GrpcServer {

    private static Logger LOGGER = LoggerFactory.getLogger(GrpcServer.class);

    public static void main(String[] args) {
        try (ConnectionProvider.Holder holder = ConnectionProvider.create()) {

            ObjectRepository objectRepositiory = new ObjectRepository(holder.roConnectionProvider, holder.rwConnectionProvider);

            if ( isInMemDB()) {
                LOGGER.info("Starting in-Mem DB Mode");
                objectRepositiory.runDDL(false);
                TimeKeeper timekeeper = new TimeKeeper("load", false);
                objectRepositiory.load(6, 6, 32000, timekeeper).join();
                FileReader filereader = new FileReader("C:/Users/Ria Bhatia/IdeaProjects/aurora-sizing/data/index/ClassicIndex.csv");
                CSVReader csvReader = new CSVReaderBuilder(filereader) .withSkipLines(1).build();
                List<String[]> allData = csvReader.readAll();
                objectRepositiory.insertIndexRecordsFromCSV(allData);
                objectRepositiory.insertFromOneTableToOther("Table_TETID", "Table_TT");
                //objectRepositiory.insertFromOneTableToOther("Table_TETID", "Table_TT");
            } else {
                LOGGER.info("Starting in Aurora Mode");
            }


            int port = Integer.parseInt(System.getProperty("port"));

            /* MonitoringServerInterceptor monitoringInterceptor =
                    MonitoringServerInterceptor.create(Configuration.allMetrics());

            Server server = ServerBuilder.forPort(port)
                    .addService(ServerInterceptors.intercept(new ObjServiceImpl(objectRepositiory), monitoringInterceptor))
                    .addService(ServerInterceptors.intercept(new TransactionServiceImpl(objectRepositiory), monitoringInterceptor))
                    .build();*/

            Server server = ServerBuilder.forPort(port)
                    .addService(new ObjServiceImpl(objectRepositiory))
                    .addService(new TransactionServiceImpl(objectRepositiory))
                    .build();

            LOGGER.info("Listening on {}", port);

            //startMetricsServer();

            server.start();
            server.awaitTermination();
        } catch (Exception e) {
            LOGGER.error("unexpected error", e);
        }
    }

    private static void startMetricsServer() {
        new Thread(() -> {

            try {
                org.eclipse.jetty.server.Server server1 = new org.eclipse.jetty.server.Server(9090);
                ServletContextHandler context = new ServletContextHandler();
                context.setContextPath("/");
                server1.setHandler(context);
                context.addServlet(new ServletHolder(new MetricsServlet()), "/metrics");
                server1.start();
                server1.join();

            } catch (IOException e) {
                LOGGER.error("failed to start metrics server", e);
            } catch (Exception e) {
                LOGGER.error("failed to start metrics server", e);
            }
        }).start();
    }


}
