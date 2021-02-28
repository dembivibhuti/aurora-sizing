package org.anonymous.server;

import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.ConnectionPoolConfigurationBuilder;
import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.SSLConfiguration;
import com.github.jasync.sql.db.postgresql.PostgreSQLConnectionBuilder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.jasync.sql.db.SSLConfiguration.Mode.Prefer;

public class JAsyncPgSQL {

    private static final int NUMBER_OF_QUERIES = 1000;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        async(NUMBER_OF_QUERIES);
        sync(NUMBER_OF_QUERIES);
    }

    private static void sync(int count) {
        Properties roprops = new Properties();
        roprops.setProperty("poolName", "roPool");
        roprops.setProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource");
        roprops.setProperty("maximumPoolSize", "150");
        roprops.setProperty("minimumIdle", "72");
        roprops.setProperty("registerMbeans", "true");
        roprops.setProperty("transactionIsolation", "TRANSACTION_READ_COMMITTED");
        roprops.setProperty("dataSource.prepareThreshold", "1");
        roprops.setProperty("idleTimeout", "1800000");
        roprops.setProperty("maxLifetime", "1800000");
        roprops.setProperty("dataSource.user", "postgres");
        roprops.setProperty("dataSource.password", "postgres");
        roprops.setProperty("dataSource.databaseName", "postgres");
        roprops.setProperty("dataSource.portNumber", "5432");
        roprops.setProperty("dataSource.serverName", "database-1.cluster-cpw6mwbci5yo.us-east-1.rds.amazonaws.com");
        roprops.setProperty("dataSource.currentSchema", "public");
        roprops.setProperty("autoCommit", "true");
        roprops.setProperty("connectionTestQuery", "SELECT 1");

        HikariConfig config = new HikariConfig(roprops);
        HikariDataSource hikariDataSource = new HikariDataSource(config);
        int i = count;
        CompletableFuture<String>[] allResults = new CompletableFuture[count];
        ExecutorService service = Executors.newFixedThreadPool(count);
        long start = System.currentTimeMillis();
        while (i > 0) {
            i--;
            CompletableFuture<String> completableFuture = new CompletableFuture<>();
            allResults[i] = completableFuture;
            service.submit(() -> {
                try (java.sql.Connection connection = hikariDataSource.getConnection();
                     PreparedStatement ps = connection.prepareStatement("Select * from objects where nameLower = ?")) {
                    ps.setString(1, RCachingTester.SEC_KEY);
                    ResultSet rs = ps.executeQuery();
                    rs.next();
                    completableFuture.complete(rs.getString(1));
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                }
                completableFuture.completeExceptionally(new Exception());
            });
        }

        List<String> keys = Stream.of(allResults).map(CompletableFuture::join).collect(Collectors.toList());
        if (keys.size() == count) {
            System.out.println("Total Time Taken  = " + (System.currentTimeMillis() - start) / 1000 + "(s)");
        } else {
            System.out.println("error !!!");
        }
        hikariDataSource.close();
        service.shutdown();
    }

    private static void async(int count) throws InterruptedException, ExecutionException {
        ConnectionPoolConfigurationBuilder connectionPoolConfigurationBuilder = new ConnectionPoolConfigurationBuilder();
        connectionPoolConfigurationBuilder.setHost("database-1-primary.cpw6mwbci5yo.us-east-1.rds.amazonaws.com");
        connectionPoolConfigurationBuilder.setPort(5432);
        connectionPoolConfigurationBuilder.setUsername("postgres");
        connectionPoolConfigurationBuilder.setPassword("postgres");
        connectionPoolConfigurationBuilder.setDatabase("postgres");
        connectionPoolConfigurationBuilder.setMaxActiveConnections(150);
        connectionPoolConfigurationBuilder.setSsl(new SSLConfiguration(Prefer, null, null, null));
        Connection connection = PostgreSQLConnectionBuilder.createConnectionPool(connectionPoolConfigurationBuilder.build());

        int i = count;
        CompletableFuture<QueryResult>[] allResults = new CompletableFuture[count];
        List<String> params = Arrays.asList(RCachingTester.SEC_KEY);

        long start = System.currentTimeMillis();
        while (i > 0) {
            i--;
            allResults[i] = connection.sendPreparedStatement("Select * from objects where nameLower = ?", params, true);
        }
        long end1 = System.currentTimeMillis();
        List<String> keys = Stream.of(allResults).map(CompletableFuture::join).map(queryResult -> (String) queryResult.getRows().get(0).get(0)).collect(Collectors.toList());
        long end2 = System.currentTimeMillis();
        if (keys.size() == count) {
            System.out.println("Time Taken for Query Submission = " + (end1 - start) / 1000 + "(s)");
            System.out.println("Total Time Taken  = " + (end2 - start) / 1000 + "(s)");
        } else {
            System.out.println("error !!!");
        }
        connection.disconnect().get();
    }
}
