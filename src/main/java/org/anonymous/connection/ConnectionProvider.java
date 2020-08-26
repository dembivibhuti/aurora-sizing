package org.anonymous.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.lang.AutoCloseable;

public class ConnectionProvider implements AutoCloseable {

    private final DataSource ds;

    public ConnectionProvider(Properties ps) {
        HikariConfig config = new HikariConfig(ps);
        ds = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        Connection c = ds.getConnection();
        c.setAutoCommit(false);
        c.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        return c;
    }

    public void close() {
        ((HikariDataSource) ds).close();
    }

}