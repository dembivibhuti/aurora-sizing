package org.anonymous.unused;

import org.joda.time.Duration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AuroraDriverManager {
    private static Duration LOGIN_TIMEOUT = Duration.standardSeconds(2);
    private static Duration CONNECT_TIMEOUT = Duration.standardSeconds(2);
    private static Duration CANCEL_SIGNAL_TIMEOUT = Duration.standardSeconds(1);
    private static Duration DEFAULT_SOCKET_TIMEOUT = Duration.standardSeconds(5);
    private static String urlFormat = "jdbc:postgresql://%s" + "/postgres" + "?user=%s" + "&password=%s"
            + "&loginTimeout=%d" + "&connectTimeout=%d" + "&cancelSignalTimeout=%d" + "&socketTimeout=%d"
            + "&targetServerType=%s" + "&tcpKeepAlive=true"
            // + "&ssl=true"
            + "&loadBalanceHosts=true";

    public AuroraDriverManager() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        /*
         * RO endpoint has a TTL of 1s, we should honor that here. Setting this aggressively makes sure that when the PG
         * JDBC driver creates a new connection, it will resolve a new different RO endpoint on subsequent attempts
         * (assuming there is > 1 read node in your cluster)
         */
        java.security.Security.setProperty("networkaddress.cache.ttl", "1");
        // If the lookup fails, default to something like small to retry
        java.security.Security.setProperty("networkaddress.cache.negative.ttl", "3");
    }

    private static String getFormattedEndpointList(List<String> endpoints) {
        return IntStream.range(0, endpoints.size()).mapToObj(i -> endpoints.get(i).toString())
                .collect(Collectors.joining(","));
    }

    public Connection getConnection(String targetServerType) throws SQLException {
        return getConnection(targetServerType, DEFAULT_SOCKET_TIMEOUT);
    }

    public Connection getConnection(String targetServerType, Duration queryTimeout) throws SQLException {
        Connection conn = DriverManager.getConnection(getJdbcConnectionString(targetServerType, queryTimeout));

        /*
         * A good practice is to set socket and statement timeout to be the same thing since both the client AND server
         * will kill the query at the same time, leaving no running queries on the backend
         */
        // Statement st = conn.createStatement();
        // st.execute("set statement_timeout to " + queryTimeout.getMillis());
        // st.close();
        conn.setAutoCommit(false);

        return conn;
    }

    public String getJdbcConnectionString(String targetServerType, Duration queryTimeout) {
        return String.format(urlFormat, getFormattedEndpointList(getLocalEndpointList()), "postgres", "postgres",
                LOGIN_TIMEOUT.getStandardSeconds(), CONNECT_TIMEOUT.getStandardSeconds(),
                CANCEL_SIGNAL_TIMEOUT.getStandardSeconds(), queryTimeout.getStandardSeconds(), targetServerType);
    }

    private List<String> getLocalEndpointList() {
        /*
         * As mentioned in the best practices doc, a good idea is to read a local resource file and parse the cluster
         * endpoints. For illustration purposes, the endpoint list is hardcoded here
         */
        List<String> newEndpointList = new ArrayList<>();
        newEndpointList.add("database-1.cluster-cpw6mwbci5yo.us-east-1.rds.amazonaws.com:5432");
        newEndpointList.add("database-1.cluster-ro-cpw6mwbci5yo.us-east-1.rds.amazonaws.com:5432");

        return newEndpointList;
    }
}