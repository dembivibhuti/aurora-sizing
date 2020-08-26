package org.anonymous.unused;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class AdminCLI extends Connector {

    public static void main(String[] args) throws SQLException {

        Scanner in = new Scanner(System.in);
        while (in.hasNextLine()) {
            String sql = in.nextLine();
            if (sql.equals("quit")) {
                System.out.println("bye");
                break;
            }

            Statement statement = connection.createStatement();
            try {
                System.out.println("Number of Records Modified : " + statement.executeUpdate(sql));
                connection.commit();
            } catch (SQLException ex) {
                System.out.println(ex);
            }

        }
    }

}
