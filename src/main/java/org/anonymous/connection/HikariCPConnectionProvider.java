package org.anonymous.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.anonymous.stats.MetricsServlet;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.Future;

public class HikariCPConnectionProvider implements ConnectionProvider {

    private final DataSource ds;

    public HikariCPConnectionProvider(Properties ps) {
        HikariConfig config = new HikariConfig(ps);
        HikariDataSource hikariDataSource = new HikariDataSource(config);
        hikariDataSource.setMetricRegistry(MetricsServlet.registry);
        ds = hikariDataSource;
    }

    public HikariCPConnectionProvider() {
        Properties props = new Properties();
        props.setProperty("driverClassName", "org.h2.Driver");
        props.setProperty("jdbcUrl",
                "jdbc:h2:mem:saral;INIT=CREATE SCHEMA IF NOT EXISTS OBJECTS_SCHEMA\\;SET SCHEMA OBJECTS_SCHEMA\\;SET MODE PostgreSQL;"); // Mem
        // Mode
        props.setProperty("username", "sa");
        props.setProperty("password", "sa");
        props.setProperty("autoCommit", "false");

        HikariConfig config = new HikariConfig(props);
        HikariDataSource hikariDataSource = new HikariDataSource(config);
        hikariDataSource.setMetricRegistry(MetricsServlet.registry);
        ds = hikariDataSource;
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
            HikariCPConnectionProvider connectionProvider = new HikariCPConnectionProvider(); // h2
            holder.roConnectionProvider = connectionProvider;
            holder.rwConnectionProvider = connectionProvider;
            return holder;
        }

        holder.roConnectionProvider = new HikariCPConnectionProvider(getROProperties());
        holder.rwConnectionProvider = new HikariCPConnectionProvider(getRWProperties());
        return holder;
    }

    private static Properties getRWProperties() {
        Properties rwprops = new Properties();
        rwprops.setProperty("poolName", "rwPool");
        rwprops.setProperty("dataSourceClassName", System.getProperty("dataSourceClassName"));
        rwprops.setProperty("maximumPoolSize", System.getProperty("rwMaximumPoolSize"));
        rwConnectionPoolMaxSize.labels("connection_pool_props").set(Double.parseDouble(System.getProperty("rwMaximumPoolSize")));
        rwprops.setProperty("minimumIdle", System.getProperty("rwMinimumIdle"));
        rwprops.setProperty("registerMbeans", "true");
        rwprops.setProperty("transactionIsolation", "TRANSACTION_READ_COMMITTED");
        rwprops.setProperty("dataSource.prepareThreshold", "1");
        rwprops.setProperty("idleTimeout", "1800000");
        rwprops.setProperty("maxLifetime", "1800000");
        rwprops.setProperty("dataSource.user", System.getProperty("dataSource.user"));
        if (System.getProperty("dataSource.password") == null) {
            rwprops.setProperty("dataSource.password",
                    GetDBCredsByIAM.generateAuthToken(System.getProperty("rds.region"),
                            System.getProperty("dataSource.rwserverName"),
                            Integer.parseInt(System.getProperty("dataSource.portNumber")),
                            System.getProperty("dataSource.user")));
        } else {
            rwprops.setProperty("dataSource.password", System.getProperty("dataSource.password"));
        }

        rwprops.setProperty("dataSource.databaseName", System.getProperty("dataSource.databaseName"));
        rwprops.setProperty("dataSource.portNumber", System.getProperty("dataSource.portNumber"));
        rwprops.setProperty("dataSource.serverName", System.getProperty("dataSource.rwserverName"));
        rwprops.setProperty("dataSource.currentSchema", System.getProperty("dataSource.currentSchema"));
        if (System.getProperty("javax.net.ssl.trustStore") != null) {
            rwprops.setProperty("dataSource.sslmode", "verify-full");
            rwprops.setProperty("dataSource.sslfactory", "org.postgresql.ssl.DefaultJavaSSLFactory");
        }
        rwprops.setProperty("autoCommit", "false");
        rwprops.setProperty("connectionTestQuery", "SELECT 1");
        return rwprops;
    }

    private static Properties getROProperties() {
        Properties roprops = new Properties();
        roprops.setProperty("poolName", "roPool");
        roprops.setProperty("dataSourceClassName", System.getProperty("dataSourceClassName"));
        roprops.setProperty("maximumPoolSize", System.getProperty("roMaximumPoolSize"));
        roConnectionPoolMaxSize.labels("connection_pool_props").set(Double.parseDouble(System.getProperty("roMaximumPoolSize")));
        roprops.setProperty("minimumIdle", System.getProperty("roMinimumIdle"));
        roprops.setProperty("registerMbeans", "true");
        roprops.setProperty("transactionIsolation", "TRANSACTION_READ_COMMITTED");
        roprops.setProperty("dataSource.prepareThreshold", "1");
        roprops.setProperty("idleTimeout", "1800000");
        roprops.setProperty("maxLifetime", "1800000");
        roprops.setProperty("dataSource.user", System.getProperty("dataSource.user"));

        if (System.getProperty("dataSource.password") == null) {

            String token = GetDBCredsByIAM.generateAuthToken(System.getProperty("rds.region"),
                    System.getProperty("dataSource.roserverName"),
                    Integer.parseInt(System.getProperty("dataSource.portNumber")),
                    System.getProperty("dataSource.user"));
            System.out.println("token = " + token);

            roprops.setProperty("dataSource.password", token);
        } else {
            roprops.setProperty("dataSource.password", System.getProperty("dataSource.password"));
        }

        roprops.setProperty("dataSource.databaseName", System.getProperty("dataSource.databaseName"));
        roprops.setProperty("dataSource.portNumber", System.getProperty("dataSource.portNumber"));
        roprops.setProperty("dataSource.serverName", System.getProperty("dataSource.roserverName"));
        roprops.setProperty("dataSource.currentSchema", System.getProperty("dataSource.currentSchema"));
        if (System.getProperty("javax.net.ssl.trustStore") != null) {
            roprops.setProperty("dataSource.sslmode", "verify-full");
            roprops.setProperty("dataSource.sslfactory", "org.postgresql.ssl.DefaultJavaSSLFactory");
        }
        roprops.setProperty("autoCommit", "true");
        roprops.setProperty("connectionTestQuery", "SELECT 1");
        return roprops;
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    @Override
    public Future<Connection> getConnectionAsync() throws SQLException {
        throw new RuntimeException("Not Implemented for HikariCP Conn. Pool");
    }

    public void close() {
        ((HikariDataSource) ds).close();
    }
}