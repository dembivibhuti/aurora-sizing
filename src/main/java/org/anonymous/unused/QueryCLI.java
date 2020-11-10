package org.anonymous.unused;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * Hello world!
 */
public class QueryCLI extends Connector {

    public static void main(String[] args) throws SQLException {

        Scanner in = new Scanner(System.in);
        while (in.hasNextLine()) {
            String sql = in.nextLine();
            if (sql.equals("quit")) {
                System.out.println("bye");
                break;
            }

            try {
                Statement statement = connection.createStatement();
                // statement.setFetchSize(10);
                ResultSet rs = statement.executeQuery("select count(*) from securities");
                rs.next();
                System.out.println(rs.getLong(1));

                while (rs.next()) {
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int columnsNumber = rsmd.getColumnCount();
                    int count = 0;

                    while (rs.next()) {
                        for (int i = 1; i <= columnsNumber; i++) {
                            if (i > 1)
                                System.out.print(",  ");
                            String columnValue = rs.getString(i);
                            System.out.print(rsmd.getColumnName(i) + " = " + "'" + columnValue + "'");
                            count++;
                        }
                        System.out.println("");
                    }
                    System.out.println(" Number of records fetched = " + count);
                }
            } catch (SQLException ex) {
                System.out.println(ex);
            }

        }
    }

}
