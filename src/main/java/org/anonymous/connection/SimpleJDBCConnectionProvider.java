package org.anonymous.connection;

import org.anonymous.connection.pool.SimpleDataSource2;
import org.h2.jdbcx.JdbcDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Future;

public class SimpleJDBCConnectionProvider implements ConnectionProvider {

    private final SimpleDataSource2 ds;

    public SimpleJDBCConnectionProvider(SimpleDataSource2.PoolProperties ps) {
        ds = new SimpleDataSource2(ps);
    }

    public SimpleJDBCConnectionProvider() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:saral;INIT=CREATE SCHEMA IF NOT EXISTS OBJECTS_SCHEMA\\;SET SCHEMA OBJECTS_SCHEMA\\;SET MODE PostgreSQL;");// Mem  // Mode
        dataSource.setUser("sa");
        dataSource.setPassword("sa");
        ds = new SimpleDataSource2("in-mem", 20000, 10, Connection.TRANSACTION_READ_COMMITTED, false, 5000, dataSource);
    }

    public static boolean isInMemDB() {
        return null == System.getProperty("dataSourceClassName");
    }

    public static ConnectionProviderHolder create() {
        /*
         * RO endpoint has a TTL of 1s, we should honor that here. Setting this aggressively makes sure that when the PG
         * JDBC driver creates a new connection, it will resolve a new different RO endpoint on subsequent attempts
         * (assuming there is > 1 read node in your cluster)
         */
        java.security.Security.setProperty("networkaddress.cache.ttl", "1");
        // If the lookup fails, default to something like small to retry
        java.security.Security.setProperty("networkaddress.cache.negative.ttl", "3");

        ConnectionProviderHolder holder = new ConnectionProviderHolder();

        if (isInMemDB()) {
            SimpleJDBCConnectionProvider connectionProvider = new SimpleJDBCConnectionProvider(); // h2
            holder.roConnectionProvider = connectionProvider;
            holder.rwConnectionProvider = connectionProvider;
            return holder;
        }


        SimpleDataSource2.PoolProperties roprops = getROProperties();
        holder.roConnectionProvider = new SimpleJDBCConnectionProvider(roprops);

        SimpleDataSource2.PoolProperties rwprops = getRWProperties();
        holder.rwConnectionProvider = new SimpleJDBCConnectionProvider(rwprops);
        return holder;
    }

    private static SimpleDataSource2.PoolProperties getRWProperties() {
        rwConnectionPoolMaxSize.labels("connection_pool_props").set(Double.parseDouble(System.getProperty("rwMaximumPoolSize")));
        SimpleDataSource2.PoolProperties rwprops = new SimpleDataSource2.PoolProperties();
        rwprops.poolName = "read-write pool";
        rwprops.maxTimeout = 10000;
        rwprops.maximumPoolSize = Integer.parseInt(System.getProperty("rwMaximumPoolSize"));
        rwprops.user = System.getProperty("dataSource.user");
        rwprops.pswd = System.getProperty("dataSource.password");
        rwprops.databaseName = System.getProperty("dataSource.databaseName");
        rwprops.serverName = System.getProperty("dataSource.rwserverName");
        rwprops.portNumber = Integer.parseInt(System.getProperty("dataSource.portNumber"));
        rwprops.currentSchema = System.getProperty("dataSource.currentSchema");
        rwprops.transactionIsolation = Connection.TRANSACTION_READ_COMMITTED;
        rwprops.autoCommit = false;
        rwprops.validityCheckInterval = 60000;
        if (System.getProperty("javax.net.ssl.trustStore") != null) {
            rwprops.ssl = true;
        }
        return rwprops;
    }

    private static SimpleDataSource2.PoolProperties getROProperties() {
        roConnectionPoolMaxSize.labels("connection_pool_props").set(Double.parseDouble(System.getProperty("roMaximumPoolSize")));
        SimpleDataSource2.PoolProperties roprops = new SimpleDataSource2.PoolProperties();
        roprops.poolName = "read-only pool";
        roprops.maxTimeout = 10000;
        roprops.maximumPoolSize = Integer.parseInt(System.getProperty("roMaximumPoolSize"));
        roprops.user = System.getProperty("dataSource.user");
        roprops.pswd = System.getProperty("dataSource.password");
        roprops.databaseName = System.getProperty("dataSource.databaseName");
        roprops.serverName = System.getProperty("dataSource.roserverName");
        roprops.portNumber = Integer.parseInt(System.getProperty("dataSource.portNumber"));
        roprops.currentSchema = System.getProperty("dataSource.currentSchema");
        roprops.transactionIsolation = Connection.TRANSACTION_READ_COMMITTED;
        roprops.autoCommit = false;
        roprops.validityCheckInterval = 10000;
        if (System.getProperty("javax.net.ssl.trustStore") != null) {
            roprops.ssl = true;
        }
        return roprops;
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    @Override
    public Future<Connection> getConnectionAsync() throws SQLException {
        throw new RuntimeException("Not Implemented for SimpleDataSource2 ");
    }

    public void close() {
        (ds).close();
    }

}