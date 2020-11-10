package org.anonymous.boot;

import org.anonymous.connection.ConnectionProviderHolder;
import org.anonymous.connection.HikariCPConnectionProvider;
import org.anonymous.pattern.LoadObjectsDataAndLookup;

public class Program {

    private final static ConnectionProviderHolder holder = HikariCPConnectionProvider.create();

    public static void main(String[] args) throws Exception {

        LoadObjectsDataAndLookup loadObjectsAndLookup = new LoadObjectsDataAndLookup(holder.roConnectionProvider,
                holder.rwConnectionProvider);
        loadObjectsAndLookup.loadObjectsAndLookup(10, 1, 10); // numberOfLookupOps, lookupLimit

        loadObjectsAndLookup.close();
        holder.roConnectionProvider.close();
        holder.rwConnectionProvider.close();
    }

}