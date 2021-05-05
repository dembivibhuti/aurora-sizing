package org.anonymous.server;

import org.anonymous.connection.ConnectionProviderHolder;
import org.anonymous.connection.HikariCPConnectionProvider;

import java.sql.Connection;
import java.sql.SQLException;

import static org.anonymous.sql.Store.*;

public class SQLCTETester {

    private static final ConnectionProviderHolder connectionProviderHolder = HikariCPConnectionProvider.create();

    public static void main(String[] args) {
        runnDDL();
    }

    private static void runnDDL() {
        try (Connection connection = connectionProviderHolder.rwConnectionProvider.getConnection()) {
            connection
                    .prepareStatement(TXN_ID_MAP)
                    .executeUpdate();
            connection
                    .prepareStatement(CREATE_TABLE_2)
                    .executeUpdate();
            connection
                    .prepareStatement(String.format(POPULATE_EXT_TXN_ID_FUNC, System.getProperty("dataSource.currentSchema")))
                    .executeUpdate();
            connection
                    .prepareStatement(ATTACH_TRIGG_TRANS_HEADER_TABLE)
                    .executeUpdate();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
}
