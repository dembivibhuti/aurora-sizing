package org.anonymous.connection;

import io.prometheus.client.Gauge;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Future;

public interface ConnectionProvider extends AutoCloseable {

    Gauge roConnectionPoolMaxSize = Gauge.build().name("ro_conn_pool_max_size").help("Max RO Pool Size").labelNames("connection_pool_props").register();
    Gauge rwConnectionPoolMaxSize = Gauge.build().name("rw_conn_pool_max_size").help("Max RW Pool Size").labelNames("connection_pool_props").register();

    Connection getConnection() throws SQLException;

    Future<Connection> getConnectionAsync() throws SQLException;

    void close();

}
