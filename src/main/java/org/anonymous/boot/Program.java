package org.anonymous.boot;

import java.util.Properties;
import org.anonymous.connection.ConnectionProvider;
import java.sql.Connection;
import java.sql.SQLException;
import org.anonymous.module.ObjectRepository;
import org.anonymous.util.StopWatch;
import org.anonymous.util.TimeKeeper;
import org.anonymous.pattern.LoadObjectsDataAndLookup;
import org.anonymous.connection.GetDBCredsByIAM;

public class Program {

    private final static ConnectionProvider.Holder holder = ConnectionProvider.create();

    public static void main(String[] args) throws Exception {

        LoadObjectsDataAndLookup loadObjectsAndLookup = new LoadObjectsDataAndLookup(holder.roConnectionProvider,
                holder.rwConnectionProvider);
        loadObjectsAndLookup.loadObjectsAndLookup(10, 1, 10); // numberOfLookupOps, lookupLimit

        loadObjectsAndLookup.close();
        holder.roConnectionProvider.close();
        holder.rwConnectionProvider.close();
    }

}