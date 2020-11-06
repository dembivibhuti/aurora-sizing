package org.anonymous.connection;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionProvider extends AutoCloseable  {
    Connection getConnection() throws SQLException;
}
