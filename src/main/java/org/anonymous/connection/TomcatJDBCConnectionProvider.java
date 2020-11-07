package org.anonymous.connection;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Future;

public class TomcatJDBCConnectionProvider implements ConnectionProvider {

    private final DataSource ds;

    public TomcatJDBCConnectionProvider(PoolProperties ps) {
        DataSource datasource = new DataSource();
        datasource.setPoolProperties(ps);
        ds = datasource;
    }

    public TomcatJDBCConnectionProvider() {
        PoolProperties props = new PoolProperties();
        props.setDriverClassName("org.h2.Driver");
        props.setUrl("jdbc:h2:mem:saral;INIT=CREATE SCHEMA IF NOT EXISTS OBJECTS_SCHEMA\\;SET SCHEMA OBJECTS_SCHEMA\\;SET MODE PostgreSQL;"); // Mem  // Mode
        props.setUsername("sa");
        props.setPassword("sa");
        props.setDefaultAutoCommit(false);

        DataSource datasource = new DataSource();
        datasource.setPoolProperties(props);
        ds = datasource;
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    @Override
    public Future<Connection> getConnectionAsync() throws SQLException {
        return ds.getConnectionAsync();
    }

    public static boolean isInMemDB() {
        return null == System.getProperty("dataSourceClassName");
    }

    public static Holder create() {
        /*
         * RO endpoint has a TTL of 1s, we should honor that here. Setting this aggressively makes sure that when the PG
         * JDBC driver creates a new connection, it will resolve a new different RO endpoint on subsequent attempts
         * (assuming there is > 1 read node in your cluster)
         */
        java.security.Security.setProperty("networkaddress.cache.ttl", "1");
        // If the lookup fails, default to something like small to retry
        java.security.Security.setProperty("networkaddress.cache.negative.ttl", "3");

        Holder holder = new Holder();

        if (isInMemDB()) {
            TomcatJDBCConnectionProvider connectionProvider = new TomcatJDBCConnectionProvider(); // h2
            holder.roConnectionProvider = connectionProvider;
            holder.rwConnectionProvider = connectionProvider;
            return holder;
        }


        PoolProperties roprops = getROProperties();
        holder.roConnectionProvider = new TomcatJDBCConnectionProvider(roprops);

        PoolProperties rwprops = getRWProperties();
        holder.rwConnectionProvider = new TomcatJDBCConnectionProvider(rwprops);
        return holder;
    }

    private static PoolProperties getRWProperties() {
        rwConnectionPoolMaxSize.labels("connection_pool_props").set(Double.parseDouble(System.getProperty("rwMaximumPoolSize")));
        PoolProperties rwprops = new PoolProperties();
        rwprops.setUrl(String.format("jdbc:postgresql://%s:%s/%s?currentSchema=%s", System.getProperty("dataSource.rwserverName"), System.getProperty("dataSource.portNumber"), System.getProperty("dataSource.databaseName"),
                System.getProperty("dataSource.currentSchema")));
        rwprops.setDriverClassName("org.postgresql.Driver");
        rwprops.setUsername(System.getProperty("dataSource.user"));
        rwprops.setPassword(System.getProperty("dataSource.password"));
        rwprops.setJmxEnabled(false);
        rwprops.setTestWhileIdle(false);
        rwprops.setTestOnBorrow(false);
        rwprops.setValidationQuery("SELECT 1");
        rwprops.setTestOnReturn(false);
        rwprops.setValidationInterval(30000);
        rwprops.setTimeBetweenEvictionRunsMillis(30000);
        rwprops.setMaxActive(Integer.parseInt(System.getProperty("roMaximumPoolSize")));
        rwprops.setInitialSize(10);
        rwprops.setMaxWait(20000);
        rwprops.setRemoveAbandonedTimeout(60);
        rwprops.setMinEvictableIdleTimeMillis(30000);
        rwprops.setMinIdle(10);
        rwprops.setLogAbandoned(true);
        rwprops.setRemoveAbandoned(true);
        rwprops.setDefaultAutoCommit(false);
        rwprops.setDefaultTransactionIsolation(2); //== TRANSACTION_READ_COMMITTED
        rwprops.setJdbcInterceptors(
                /*"org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"+*/
                        "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
        return rwprops;
    }

    private static PoolProperties getROProperties() {
        roConnectionPoolMaxSize.labels("connection_pool_props").set(Double.parseDouble(System.getProperty("roMaximumPoolSize")));
        PoolProperties roprops = new PoolProperties();
        roprops.setUrl(String.format("jdbc:postgresql://%s:%s/%s?currentSchema=%s", System.getProperty("dataSource.roserverName"), System.getProperty("dataSource.portNumber"), System.getProperty("dataSource.databaseName"),
                System.getProperty("dataSource.currentSchema")));
        roprops.setDriverClassName("org.postgresql.Driver");
        roprops.setUsername(System.getProperty("dataSource.user"));
        roprops.setPassword(System.getProperty("dataSource.password"));
        roprops.setJmxEnabled(false);
        roprops.setTestWhileIdle(false);
        roprops.setTestOnBorrow(false);
        roprops.setValidationQuery("SELECT 1");
        roprops.setTestOnReturn(false);
        roprops.setValidationInterval(30000);
        roprops.setTimeBetweenEvictionRunsMillis(30000);
        roprops.setMaxActive(Integer.parseInt(System.getProperty("roMaximumPoolSize")));
        roprops.setInitialSize(10);
        roprops.setMaxWait(10000);
        roprops.setRemoveAbandonedTimeout(60);
        roprops.setMinEvictableIdleTimeMillis(30000);
        roprops.setMinIdle(10);
        roprops.setLogAbandoned(true);
        roprops.setRemoveAbandoned(true);
        roprops.setDefaultAutoCommit(false);
        roprops.setDefaultTransactionIsolation(2); //== TRANSACTION_READ_COMMITTED
        roprops.setJdbcInterceptors(
                /*"org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"+*/
                        "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
        return roprops;
    }

    public static class Holder implements AutoCloseable {
        public TomcatJDBCConnectionProvider roConnectionProvider;
        public TomcatJDBCConnectionProvider rwConnectionProvider;

        @Override
        public void close() {
            roConnectionProvider.close();
            rwConnectionProvider.close();
        }
    }

    public void close() {
        (ds).close();
    }

}