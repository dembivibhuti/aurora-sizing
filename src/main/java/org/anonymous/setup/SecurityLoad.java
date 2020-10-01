package org.anonymous.setup;

import org.anonymous.connection.ConnectionProvider;
import org.anonymous.module.ObjectRepository;
import org.anonymous.util.TimeKeeper;

import java.sql.Connection;
import java.sql.SQLException;

public class SecurityLoad {
    private final static ConnectionProvider.Holder holder = ConnectionProvider.create();

    public static void main(String[] args) throws Exception {

        try {
            ObjectRepository objectRepository = new ObjectRepository(holder.roConnectionProvider, holder.rwConnectionProvider);
            TimeKeeper timekeeper = new TimeKeeper();
            objectRepository.load(7812500, 6, timekeeper).join();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
          holder.close();
        }
    }
}
