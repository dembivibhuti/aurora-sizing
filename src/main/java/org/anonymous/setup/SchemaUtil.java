package org.anonymous.setup;

import org.anonymous.connection.ConnectionProvider;
import org.anonymous.module.ObjectRepository;
import org.anonymous.util.TimeKeeper;

import java.sql.Connection;
import java.sql.SQLException;

public class SchemaUtil {

    private final static ConnectionProvider.Holder holder = ConnectionProvider.create();

    public static void main(String[] args) throws Exception {

        try (Connection connection = holder.rwConnectionProvider.getConnection()) {
            connection
                    .prepareStatement(
                            String.format("drop schema %s cascade", System.getProperty("dataSource.currentSchema")))
                    .executeUpdate();

            connection
                    .prepareStatement(
                            String.format("create schema %s", System.getProperty("dataSource.currentSchema")))
                    .executeUpdate();

            connection.commit();

            ObjectRepository objectRepository = new ObjectRepository(holder.roConnectionProvider, holder.rwConnectionProvider);
            objectRepository.runDDL(false);
            TimeKeeper timekeeper = new TimeKeeper("load");
            objectRepository.load(6, 6, timekeeper).join();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {

        }
    }
}
