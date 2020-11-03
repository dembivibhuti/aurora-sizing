package org.anonymous.setup;

import org.anonymous.connection.ConnectionProvider;
import org.anonymous.module.ObjectRepository;
import org.anonymous.util.TimeKeeper;

import java.sql.Connection;
import java.sql.SQLException;

public class ObjectLoad {
    private final static ConnectionProvider.Holder holder = ConnectionProvider.create();

    public static void main(String[] args) {

        try {
            ObjectRepository objectRepository = new ObjectRepository(holder.roConnectionProvider, holder.rwConnectionProvider);
            TimeKeeper timekeeper = new TimeKeeper("load", false);
            int recordCount = Integer.parseInt(System.getProperty("obj.count"));
            int objSize = Integer.parseInt(System.getProperty("obj.size"));
            System.out.println("recordCount= " + recordCount);
            System.out.println("objSize= " + objSize);
            objectRepository.load(recordCount, 1000, objSize, timekeeper).join();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
          holder.close();
        }
    }
}
