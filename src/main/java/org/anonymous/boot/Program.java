package org.anonymous.boot;

import java.util.Properties;
import org.anonymous.connection.ConnectionProvider;
import java.sql.Connection;
import java.sql.SQLException;
import org.anonymous.module.ObjectRepository;
import org.anonymous.util.StopWatch;
import org.anonymous.util.TimeKeeper;
import org.anonymous.pattern.LoadObjectsDataAndLookup;

public class Program {

    private static ConnectionProvider roConnectionProvider;
    private static ConnectionProvider rwConnectionProvider;

    public static void main(String[] args) throws Exception {

        /*
         * RO endpoint has a TTL of 1s, we should honor that here. Setting this aggressively makes sure that when the PG
         * JDBC driver creates a new connection, it will resolve a new different RO endpoint on subsequent attempts
         * (assuming there is > 1 read node in your cluster)
         */
        java.security.Security.setProperty("networkaddress.cache.ttl", "1");
        // If the lookup fails, default to something like small to retry
        java.security.Security.setProperty("networkaddress.cache.negative.ttl", "3");

        if (null == System.getProperty("dataSourceClassName")) {
            ConnectionProvider connectionProvider = new ConnectionProvider(); // h2
            roConnectionProvider = connectionProvider;
            rwConnectionProvider = connectionProvider;
        } else {
            Properties roprops = new Properties();
            roprops.setProperty("dataSourceClassName", System.getProperty("dataSourceClassName"));
            roprops.setProperty("dataSource.user", System.getProperty("dataSource.user"));
            roprops.setProperty("dataSource.password", System.getProperty("dataSource.password"));
            roprops.setProperty("dataSource.databaseName", System.getProperty("dataSource.databaseName"));
            roprops.setProperty("dataSource.portNumber", System.getProperty("dataSource.portNumber"));
            roprops.setProperty("dataSource.serverName", System.getProperty("dataSource.roserverName"));
            roprops.setProperty("dataSource.currentSchema", System.getProperty("dataSource.currentSchema"));
            if (System.getProperty("javax.net.ssl.trustStore") != null) {
                roprops.setProperty("dataSource.sslmode", "verify-full");
                roprops.setProperty("dataSource.sslfactory", "org.postgresql.ssl.DefaultJavaSSLFactory");
            }
            roprops.setProperty("autoCommit", "false");
            roConnectionProvider = new ConnectionProvider(roprops);

            Properties rwprops = new Properties();
            rwprops.setProperty("dataSourceClassName", System.getProperty("dataSourceClassName"));
            rwprops.setProperty("dataSource.user", System.getProperty("dataSource.user"));
            rwprops.setProperty("dataSource.password", System.getProperty("dataSource.password"));
            rwprops.setProperty("dataSource.databaseName", System.getProperty("dataSource.databaseName"));
            rwprops.setProperty("dataSource.portNumber", System.getProperty("dataSource.portNumber"));
            rwprops.setProperty("dataSource.serverName", System.getProperty("dataSource.rwserverName"));
            rwprops.setProperty("dataSource.currentSchema", System.getProperty("dataSource.currentSchema"));
            if (System.getProperty("javax.net.ssl.trustStore") != null) {
                rwprops.setProperty("dataSource.sslmode", "verify-full");
                rwprops.setProperty("dataSource.sslfactory", "org.postgresql.ssl.DefaultJavaSSLFactory");
            }
            rwprops.setProperty("autoCommit", "false");
            rwConnectionProvider = new ConnectionProvider(rwprops);
        }

        LoadObjectsDataAndLookup loadObjectsAndLookup = new LoadObjectsDataAndLookup(roConnectionProvider,
                rwConnectionProvider);
        loadObjectsAndLookup.loadObjectsAndLookup(10, 1, 10); // numberOfLookupOps, lookupLimit

        loadObjectsAndLookup.close();
        roConnectionProvider.close();
        rwConnectionProvider.close();

    }

}