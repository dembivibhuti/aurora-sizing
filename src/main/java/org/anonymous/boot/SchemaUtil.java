package org.anonymous.boot;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.anonymous.connection.ConnectionProvider;
import java.sql.Connection;
import java.sql.SQLException;
import org.anonymous.module.ObjectRepository;
import org.anonymous.util.TimeKeeper;

public class SchemaUtil {

    public static void main(String[] args) throws Exception {

//        Properties rwprops = new Properties();
//        rwprops.setProperty("dataSourceClassName", System.getProperty("dataSourceClassName"));
//        rwprops.setProperty("dataSource.user", System.getProperty("dataSource.user"));
//        rwprops.setProperty("dataSource.password", System.getProperty("dataSource.password"));
//        rwprops.setProperty("dataSource.databaseName", System.getProperty("dataSource.databaseName"));
//        rwprops.setProperty("dataSource.portNumber", System.getProperty("dataSource.portNumber"));
//        rwprops.setProperty("dataSource.serverName", System.getProperty("dataSource.rwserverName"));
//        rwprops.setProperty("dataSource.currentSchema", System.getProperty("dataSource.currentSchema"));
//        rwprops.setProperty("autoCommit", "false");
//        try (ConnectionProvider rwConnectionProvider = new ConnectionProvider(rwprops)) {
//            try (Connection connection = rwConnectionProvider.getConnection()) {
//                connection
//                        .prepareStatement(
//                                String.format("drop schema %s cascade", System.getProperty("dataSource.currentSchema")))
//                        .executeUpdate();
//
//                connection
//                        .prepareStatement(
//                                String.format("create schema %s", System.getProperty("dataSource.currentSchema")))
//                        .executeUpdate();
//
//                connection.commit();
//            } finally {
//
//            }
            ConnectionProvider connectionProvider = new ConnectionProvider();
            new ObjectRepository(null, connectionProvider).runDDL(false);
            new ObjectRepository(null, connectionProvider).load(1, 1 , new TimeKeeper());
            List<String> list=new ArrayList<String>();
            list.add("testSec-1418335106-0");
            new ObjectRepository(null, connectionProvider).getManySDBByKeys(list, new TimeKeeper());


//        } catch (SQLException ex) {
//            ex.printStackTrace();
//        }

    }
}