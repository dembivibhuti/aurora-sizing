package org.anonymous.unused;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Connector {
    protected static Connection connection;

    static {
        try {
            System.out.println("Connecting .... ");
            AuroraDriverManager driver = new AuroraDriverManager();
            connection = driver.getConnection("primary");
            System.out.println("Connection Established");
            System.out.println("Hello from Aurora !!!");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }
}