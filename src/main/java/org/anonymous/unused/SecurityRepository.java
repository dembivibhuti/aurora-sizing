package org.anonymous.unused;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.SplittableRandom;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class SecurityRepository {
    private static final int RECORD_COUNT = 45181998; // 50000000; // approx. 200 GiB
    private static final int LOOKUP_LIMIT = 200;
    private static final List<Integer> TYPE_IDS = new ArrayList<>();
    private static final List<String> SEC_NAMES = new ArrayList<>();

    public static void main(String[] args) throws SQLException {

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        populateDB(RECORD_COUNT);

        executorService.execute(new Runnable() {
            public void run() {
                randomLookUpByPrefix(LOOKUP_LIMIT);
            }
        });

        executorService.execute(new Runnable() {
            public void run() {
                randomLookUpByTypeAndPrefix(LOOKUP_LIMIT);
            }
        });

        executorService.execute(new Runnable() {
            public void run() {
                getSecurity();
            }
        });

        executorService.shutdown();
    }

    private static void populateDB(int recordCount) {
        try {
            AuroraDriverManager driver = new AuroraDriverManager();
            Connection connection = driver.getConnection("primary");

            // connection.prepareStatement("drop table securities").executeUpdate();
            // connection.commit();
            // System.out.println(" dropped securities ");

            /*
             * String createTableSQL = "create table securities ( \n" + "name varchar NOT NULL, \n" +
             * "typeId integer NOT NULL, \n" + "lastTransaction bigint NOT NULL, \n" +
             * "timeUpdated timestamp NOT NULL, \n" + "updateCount bigint NOT NULL, \n" +
             * "dateCreated integer NOT NULL, \n" + "dbIdUpdated integer NOT NULL, \n" +
             * "versionInfo integer NOT NULL, \n" + "sdbDiskMem bytea NOT NULL, \n" + "mem bytea NOT NULL, \n" +
             * "Primary Key ( name ) )";
             * 
             * connection.prepareStatement(createTableSQL).executeUpdate();
             * System.out.println(" created securities table "); connection.commit();
             * 
             * connection.prepareStatement("create unique index securities_typeid_name on securities(typeId, name)").
             * executeUpdate(); System.out.println(" created index by typeId and name "); connection.commit();
             */

            PreparedStatement insertRec = connection
                    .prepareStatement("insert into securities values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            long start = System.currentTimeMillis();
            Iterator<Integer> randIntStream = new SplittableRandom().ints().iterator();
            for (int i = 0; i < recordCount; i++) {

                int randTypeId = randIntStream.next();
                String name = String.format("testSec-%d-%d", randIntStream.next(), i);
                insertRec.setString(1, name);
                insertRec.setInt(2, randTypeId);
                insertRec.setLong(3, i);
                insertRec.setTimestamp(4, new java.sql.Timestamp(0));
                insertRec.setLong(5, 0);
                insertRec.setInt(6, 0);
                insertRec.setInt(7, 0);
                insertRec.setInt(8, 0);
                insertRec.setBytes(9, new byte[100]);
                insertRec.setBytes(10, new byte[32000]);

                insertRec.executeUpdate();

                if (i % 1000 == 0) {
                    TYPE_IDS.add(randTypeId);
                    SEC_NAMES.add(name);
                    connection.commit();
                    System.out.println(" commited 1000 records. recs inserted " + i);

                }
            }
            connection.commit();

            System.out.println(
                    " inserted  " + recordCount + " records in " + (System.currentTimeMillis() - start) / 1000 + "(s)");

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    private static void randomLookUpByPrefix(int limit) {
        try {
            AuroraDriverManager driver = new AuroraDriverManager();
            Connection connection = driver.getConnection("primary");

            PreparedStatement lookupStmt = connection.prepareStatement(
                    "select name from securities where lower(name) >= ? order by name asc LIMIT " + limit);
            Iterator<Integer> randIntStream = new SplittableRandom().ints().iterator();

            while (true) {
                lookupStmt.setString(1, String.format("testSec-%d", randIntStream.next() % 1000));

                long start = System.currentTimeMillis();
                ResultSet rs = lookupStmt.executeQuery();
                long matchCount = 0;
                while (rs.next()) {
                    matchCount++;
                }
                long elapsedTime = (System.currentTimeMillis() - start);
                double avgTimePerRec = matchCount == 0 ? 0 : ((double) elapsedTime) / ((double) matchCount);
                System.out.println(" lookup :: matched records " + matchCount + " elapsed time " + elapsedTime + "(ms)"
                        + " time per record " + avgTimePerRec + "(ms)");

                Thread.sleep(4000);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private static void randomLookUpByTypeAndPrefix(int limit) {
        try {
            AuroraDriverManager driver = new AuroraDriverManager();
            Connection connection = driver.getConnection("primary");

            PreparedStatement lookupStmt = connection.prepareStatement(
                    "select name from securities where lower(name) >= ? and typeId = ? order by name asc LIMIT "
                            + limit);
            Iterator<Integer> randIntStream = new SplittableRandom().ints().iterator();
            while (true) {

                lookupStmt.setString(1, String.format("testSec-%d", randIntStream.next() % 10));
                lookupStmt.setInt(2, TYPE_IDS.get(Math.abs(randIntStream.next() % 10)));

                long start = System.currentTimeMillis();
                ResultSet rs = lookupStmt.executeQuery();
                long matchCount = 0;
                while (rs.next()) {
                    matchCount++;
                }
                long elapsedTime = (System.currentTimeMillis() - start);
                double avgTimePerRec = matchCount == 0 ? 0 : ((double) elapsedTime) / ((double) matchCount);
                System.out.println(" lookup by type :: matched records " + matchCount + " elapsed time " + elapsedTime
                        + "(ms)" + " time per record " + avgTimePerRec + "(ms)");

                Thread.sleep(4000);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private static void getSecurity() {

        try {

            AuroraDriverManager driver = new AuroraDriverManager();
            Connection connection = driver.getConnection("primary");

            Iterator<Integer> randIntStream = new SplittableRandom().ints().iterator();
            PreparedStatement lookupStmt = connection.prepareStatement("select name from securities where name = ?");
            lookupStmt.setFetchSize(10);
            while (true) {

                lookupStmt.setString(1, SEC_NAMES.get(Math.abs(randIntStream.next() % 10)));
                System.out.println(lookupStmt);
                long start = System.currentTimeMillis();
                ResultSet rs = lookupStmt.executeQuery();
                long matchCount = 0;
                while (rs.next()) {
                    ++matchCount;
                }
                long elapsedTime = (System.currentTimeMillis() - start);
                double avgTimePerRec = matchCount == 0 ? 0 : ((double) elapsedTime) / ((double) matchCount);
                System.out.println(" Get Security :: matched records " + matchCount + " elapsed time " + elapsedTime
                        + "(ms)" + " time per record " + avgTimePerRec + "(ms)");

                Thread.sleep(4000);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}