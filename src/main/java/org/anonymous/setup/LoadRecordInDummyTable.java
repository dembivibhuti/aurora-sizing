package org.anonymous.setup;

import org.anonymous.connection.ConnectionProviderHolder;
import org.anonymous.connection.HikariCPConnectionProvider;
import org.anonymous.module.ObjectRepository;
import org.anonymous.util.TimeKeeper;


public class LoadRecordInDummyTable {
    private final static ConnectionProviderHolder holder = HikariCPConnectionProvider.create();

    public static void main(String[] args) {
        try {
            ObjectRepository objectRepository = new ObjectRepository(holder.roConnectionProvider, holder.rwConnectionProvider);
            TimeKeeper timekeeper = new TimeKeeper("load", false);
            objectRepository.insertFromOneTableToOther("objects", "Table_TETID");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            holder.close();
        }
    }
}
