package org.anonymous.connection;

import org.anonymous.connection.pool.SimpleDataSource2;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Future;

public class NoPoolConnectionProvider implements ConnectionProvider {

    private final DataSource dataSource;


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

        if (SimpleJDBCConnectionProvider.isInMemDB()) {
            SimpleJDBCConnectionProvider connectionProvider = new SimpleJDBCConnectionProvider(); // h2
            holder.roConnectionProvider = connectionProvider;
            holder.rwConnectionProvider = connectionProvider;
            return holder;
        }

        holder.roConnectionProvider = new NoPoolConnectionProvider(System.getProperty("dataSource.roserverName"));
        holder.rwConnectionProvider = new NoPoolConnectionProvider(System.getProperty("dataSource.rwserverName"));
        return holder;
    }


    public NoPoolConnectionProvider(final String serverName) {
        this.dataSource = createDataSource(serverName);
    }


    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = this.dataSource.getConnection();
        connection.setAutoCommit(false);
        //connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        return connection;
    }

    private DataSource createDataSource(final String serverName) {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUser(System.getProperty("dataSource.user"));
        dataSource.setPassword(System.getProperty("dataSource.password"));
        dataSource.setDatabaseName(System.getProperty("dataSource.databaseName"));
        dataSource.setServerNames(new String[]{serverName});
        dataSource.setPortNumbers(new int[]{Integer.parseInt(System.getProperty("dataSource.portNumber"))});
        dataSource.setCurrentSchema(System.getProperty("dataSource.currentSchema"));
        dataSource.setConnectTimeout(0); //infinite timeout
        if (System.getProperty("javax.net.ssl.trustStore") != null) {
            dataSource.setSsl(true);
            dataSource.setSslmode("verify-full");
            dataSource.setSslfactory("org.postgresql.ssl.DefaultJavaSSLFactory");
        }
        dataSource.setPrepareThreshold(1);
        return dataSource;
    }

    @Override
    public Future<Connection> getConnectionAsync() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
        // no - op
    }
}
