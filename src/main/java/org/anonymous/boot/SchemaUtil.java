package org.anonymous.boot;

import java.util.Properties;
import org.anonymous.connection.HikariCPConnectionProvider;
import java.sql.Connection;
import java.sql.SQLException;
import org.anonymous.module.ObjectRepository;

public class SchemaUtil {

    public static void main(String[] args) throws Exception {

        Properties rwprops = new Properties();
        rwprops.setProperty("dataSourceClassName", System.getProperty("dataSourceClassName"));
        rwprops.setProperty("dataSource.user", System.getProperty("dataSource.user"));
        rwprops.setProperty("dataSource.password", System.getProperty("dataSource.password"));
        rwprops.setProperty("dataSource.databaseName", System.getProperty("dataSource.databaseName"));
        rwprops.setProperty("dataSource.portNumber", System.getProperty("dataSource.portNumber"));
        rwprops.setProperty("dataSource.serverName", System.getProperty("dataSource.rwserverName"));
        rwprops.setProperty("dataSource.currentSchema", System.getProperty("dataSource.currentSchema"));
        rwprops.setProperty("autoCommit", "false");
        try (HikariCPConnectionProvider rwConnectionProvider = new HikariCPConnectionProvider(rwprops)) {
            try (Connection connection = rwConnectionProvider.getConnection()) {
                connection
                        .prepareStatement(
                                String.format("drop schema %s cascade", System.getProperty("dataSource.currentSchema")))
                        .executeUpdate();

                connection
                        .prepareStatement(
                                String.format("create schema %s", System.getProperty("dataSource.currentSchema")))
                        .executeUpdate();

                connection.commit();
            } finally {

            }

            new ObjectRepository(null, rwConnectionProvider).runDDL(false);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }
}