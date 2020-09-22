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

    public ConnectionProvider() {
        Properties props = new Properties();
        props.setProperty("driverClassName", "org.h2.Driver");
        props.setProperty("jdbcUrl",
                "jdbc:h2:mem:saral;INIT=CREATE SCHEMA IF NOT EXISTS SARAL\\;SET SCHEMA SARAL\\;SET MODE PostgreSQL;"); // Mem
                                                                                                                       // Mode
        props.setProperty("username", "sa");
        props.setProperty("password", "sa");
        props.setProperty("autoCommit", "false");

        HikariConfig config = new HikariConfig(props);
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