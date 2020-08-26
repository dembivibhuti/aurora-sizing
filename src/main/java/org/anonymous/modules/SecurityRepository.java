package org.anonymous.module;

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
import java.util.concurrent.CompletableFuture;
import org.anonymous.connection.ConnectionProvider;
import java.util.stream.IntStream;
import org.anonymous.util.StopWatch;
import org.anonymous.util.TimeKeeper;
import java.util.stream.Stream;
import java.util.Collections;

public class SecurityRepository implements AutoCloseable {
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    private ConnectionProvider roConnectionProvider;
    private ConnectionProvider rwConnectionProvider;

    public SecurityRepository(ConnectionProvider roConnectionProvider, ConnectionProvider rwConnectionProvider) {
        this.roConnectionProvider = roConnectionProvider;
        this.rwConnectionProvider = rwConnectionProvider;
    }

    public void runDDL(boolean dropAndCreate) {

        try (Connection connection = rwConnectionProvider.getConnection()) {

            if (dropAndCreate) {
                connection.prepareStatement("drop table securities").executeUpdate();
                connection.commit();
                System.out.println(" dropped securities ");
            }

            String createTableSQL = "create table securities ( \n" + "name varchar NOT NULL, \n"
                    + "typeId integer NOT NULL, \n" + "lastTransaction bigint NOT NULL, \n"
                    + "timeUpdated timestamp NOT NULL, \n" + "updateCount bigint NOT NULL, \n"
                    + "dateCreated integer NOT NULL, \n" + "dbIdUpdated integer NOT NULL, \n"
                    + "versionInfo integer NOT NULL, \n" + "sdbDiskMem bytea NOT NULL, \n" + "mem bytea NOT NULL, \n"
                    + "Primary Key ( name ) )";

            connection.prepareStatement(createTableSQL).executeUpdate();
            System.out.println(" created securities table ");
            connection.commit();

            connection.prepareStatement("create unique index securities_typeid_name on securities(typeId, name)")
                    .executeUpdate();
            System.out.println(" created index by typeId and name ");
            connection.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public CompletableFuture load(int recordCount, int threadCount, TimeKeeper secInsertTimeKeeper) {
        int numberOfRecsPerThread = recordCount / threadCount;
        CompletableFuture[] all = new CompletableFuture[threadCount + 1]; // extra 1 for the remaining records

        Iterator<Integer> it = IntStream.range(0, threadCount).iterator();
        while (it.hasNext()) {
            int batchId = it.next();
            CompletableFuture completableFuture = new CompletableFuture();
            all[batchId] = completableFuture;
            executorService.submit(() -> {
                StopWatch.start(String.format("sec.insert.batch.%d", batchId));
                insertRecs(numberOfRecsPerThread, completableFuture, secInsertTimeKeeper);
                StopWatch.stop(String.format("sec.insert.batch.%d", batchId));
            });
        }

        int remainingRecs = recordCount % threadCount;
        if (remainingRecs > 0) {
            CompletableFuture completableFuture = new CompletableFuture();
            all[threadCount] = completableFuture;
            executorService.submit(() -> {
                StopWatch.stop(String.format("sec.insert.batch.%d", threadCount));
                insertRecs(remainingRecs, completableFuture, secInsertTimeKeeper);
                StopWatch.stop(String.format("sec.insert.batch.%d", threadCount));
            });
        } else {
            all[threadCount] = CompletableFuture.completedFuture("");
        }

        return CompletableFuture.allOf(all);
    }

    private void insertRecs(int numberOfRecsPerThread, CompletableFuture completableFuture,
            TimeKeeper secInsertTimeKeeper) {

        try (Connection connection = rwConnectionProvider.getConnection(); PreparedStatement insertRec = connection
                .prepareStatement("insert into securities values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

            byte[] sdbMem = getSizedByteArray(100);
            byte[] mem = getSizedByteArray(32000);

            Iterator<Integer> randIntStream = new SplittableRandom().ints().iterator();
            for (int i = 0; i < numberOfRecsPerThread; i++) {

                int randTypeId = randIntStream.next();
                String name = String.format("testSec-%d-%d", randIntStream.next(), i);

                long spanId = secInsertTimeKeeper.start();
                insertRec.setString(1, name);
                insertRec.setInt(2, randTypeId);
                insertRec.setLong(3, i);
                insertRec.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
                insertRec.setLong(5, 0);
                insertRec.setInt(6, 0);
                insertRec.setInt(7, 0);
                insertRec.setInt(8, 0);
                insertRec.setBytes(9, sdbMem);
                insertRec.setBytes(10, mem);
                insertRec.executeUpdate();
                connection.commit();

                secInsertTimeKeeper.stop(spanId);
            }
            completableFuture.complete("");

        } catch (Exception ex) {
            ex.printStackTrace();
            completableFuture.completeExceptionally(ex);
        }
    }

    private byte[] getSizedByteArray(int size) {
        String string = "asdf";
        byte[] result = new byte[size];
        System.arraycopy(string.getBytes(), 0, result, size - string.length(), string.length());
        return result;
    }

    public CompletableFuture<List<String>> randomLookUpByPrefix(int numberOfLookupOps, int limit,
            TimeKeeper lookupTimeKeeper) {
        CompletableFuture<List<String>> completableFuture = new CompletableFuture();
        List<String> secKeys = new ArrayList<>();
        Iterator<Integer> randIntStream = new SplittableRandom().ints().iterator();
        try (Connection connection = rwConnectionProvider.getConnection();
                PreparedStatement lookupStmt = connection.prepareStatement(
                        "select name from securities where lower(name) >= ? order by name asc LIMIT " + limit)) {

            while (numberOfLookupOps > 0) {
                numberOfLookupOps--;

                long spanId = lookupTimeKeeper.start();
                lookupStmt.setString(1, String.format("testSec-%d", randIntStream.next() % 1000));
                ResultSet rs = lookupStmt.executeQuery();

                while (rs.next()) {
                    secKeys.add(rs.getString(1));
                }
                rs.close();
                lookupTimeKeeper.stop(spanId);
            }
            completableFuture.complete(secKeys);
        } catch (Exception ex) {
            ex.printStackTrace();
            completableFuture.completeExceptionally(ex);
        }
        return completableFuture;
    }

    public Stream<CompletableFuture<List<String>>> randomLookUpByPrefixMultiThread(int numberOfLookupOps, int limit,
            TimeKeeper lookupTimeKeeper) {
        CompletableFuture[] all = new CompletableFuture[numberOfLookupOps];
        Iterator<Integer> randIntStream = new SplittableRandom().ints().iterator();

        while (numberOfLookupOps > 0) {
            numberOfLookupOps--;
            CompletableFuture<List<String>> completableFuture = new CompletableFuture();
            all[numberOfLookupOps] = completableFuture;

            executorService.submit(() -> {
                long spanId = lookupTimeKeeper.start();
                List<String> secKeys = new ArrayList<>();
                try (Connection connection = rwConnectionProvider.getConnection();
                        PreparedStatement lookupStmt = connection.prepareStatement(
                                "select name from securities where lower(name) >= ? order by name asc LIMIT "
                                        + limit)) {
                    lookupStmt.setString(1, String.format("testSec-%d", randIntStream.next() % 1000));
                    ResultSet rs = lookupStmt.executeQuery();

                    while (rs.next()) {
                        secKeys.add(rs.getString(1));
                    }
                    rs.close();
                    lookupTimeKeeper.stop(spanId);
                    completableFuture.complete(secKeys);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    completableFuture.completeExceptionally(ex);
                }
            });
        }
        return Stream.of(all);
    }

    public CompletableFuture<Void> getSDBByKey(String secKey, TimeKeeper lookupTimeKeeper) {
        CompletableFuture<Void> completableFuture = new CompletableFuture();
        executorService.submit(() -> {
            long spanId = lookupTimeKeeper.start();

            try (Connection connection = rwConnectionProvider.getConnection();
                    PreparedStatement lookupStmt = connection.prepareStatement(
                            "select name, typeId, lastTransaction, timeUpdated, updateCount, dateCreated, dbIdUpdated, versionInfo from securities where name = ?")) {
                lookupStmt.setString(1, secKey);
                ResultSet rs = lookupStmt.executeQuery();

                Object x = null;
                while (rs.next()) {
                    x = rs.getString("name");
                    x = rs.getInt("typeId");
                    x = rs.getLong("lastTransaction");
                    x = rs.getTimestamp("timeUpdated");
                    x = rs.getLong("updateCount");
                    x = rs.getInt("dateCreated");
                    x = rs.getInt("dbIdUpdated");
                    x = rs.getInt("versionInfo");
                }
                rs.close();
                lookupTimeKeeper.stop(spanId);
                completableFuture.complete(null);

            } catch (Exception ex) {
                ex.printStackTrace();
                completableFuture.completeExceptionally(ex);
            }
        });
        return completableFuture;
    }

    public CompletableFuture<Void> getManySDBByKeys(List<String> secKeys, TimeKeeper lookupTimeKeeper) {
        CompletableFuture<Void> completableFuture = new CompletableFuture();
        executorService.submit(() -> {
            long spanId = lookupTimeKeeper.start();
            String sql = String.format(
                    "select name, typeId, lastTransaction, timeUpdated, updateCount, dateCreated, dbIdUpdated, versionInfo from securities where name in (%s)",
                    String.join(",", Collections.nCopies(secKeys.size(), "?")));

            try (Connection connection = rwConnectionProvider.getConnection();
                    PreparedStatement lookupStmt = connection.prepareStatement(sql)) {
                for (int i = 0; i < secKeys.size(); i++) {
                    lookupStmt.setString(i + 1, secKeys.get(i));
                }

                ResultSet rs = lookupStmt.executeQuery();

                Object x = null;
                while (rs.next()) {
                    x = rs.getString("name");
                    x = rs.getInt("typeId");
                    x = rs.getLong("lastTransaction");
                    x = rs.getTimestamp("timeUpdated");
                    x = rs.getLong("updateCount");
                    x = rs.getInt("dateCreated");
                    x = rs.getInt("dbIdUpdated");
                    rs.getInt("versionInfo");
                }
                rs.close();
                lookupTimeKeeper.stop(spanId);
                completableFuture.complete(null);

            } catch (Exception ex) {
                ex.printStackTrace();
                completableFuture.completeExceptionally(ex);
            }
        });
        return completableFuture;
    }

    public CompletableFuture<Void> getSDBMemByKey(String secKey, TimeKeeper lookupTimeKeeper) {
        CompletableFuture<Void> completableFuture = new CompletableFuture();
        executorService.submit(() -> {
            long spanId = lookupTimeKeeper.start();

            try (Connection connection = rwConnectionProvider.getConnection(); PreparedStatement lookupStmt = connection
                    .prepareStatement("select sdbDiskMem from securities where name = ?")) {
                lookupStmt.setString(1, secKey);
                ResultSet rs = lookupStmt.executeQuery();

                Object x = null;
                while (rs.next()) {
                    x = rs.getBytes("sdbDiskMem");
                }
                rs.close();
                lookupTimeKeeper.stop(spanId);
                completableFuture.complete(null);

            } catch (Exception ex) {
                ex.printStackTrace();
                completableFuture.completeExceptionally(ex);
            }
        });
        return completableFuture;
    }

    public CompletableFuture<Void> getSDBMemsByKeys(List<String> secKeys, TimeKeeper lookupTimeKeeper) {
        CompletableFuture<Void> completableFuture = new CompletableFuture();
        int size = secKeys.size();
        executorService.submit(() -> {
            long spanId = lookupTimeKeeper.start();
            String sql = String.format("select sdbDiskMem from securities where name in (%s)",
                    String.join(",", Collections.nCopies(size, "?")));

            try (Connection connection = rwConnectionProvider.getConnection();
                    PreparedStatement lookupStmt = connection.prepareStatement(sql)) {
                for (int i = 0; i < size; i++) {
                    lookupStmt.setString(i + 1, secKeys.get(i));
                }

                ResultSet rs = lookupStmt.executeQuery();

                Object x = null;
                long cnt = 0;
                while (rs.next()) {
                    x = rs.getBytes("sdbDiskMem");
                    cnt++;
                }
                if (size != cnt) {
                    completableFuture
                            .completeExceptionally(new RuntimeException("the count of fetched recs dont match"));
                }
                rs.close();
                lookupTimeKeeper.stop(spanId);
                completableFuture.complete(null);

            } catch (Exception ex) {
                ex.printStackTrace();
                completableFuture.completeExceptionally(ex);
            }
        });
        return completableFuture;
    }

    public CompletableFuture<Void> getMemByKey(String secKey, TimeKeeper lookupTimeKeeper) {
        CompletableFuture<Void> completableFuture = new CompletableFuture();
        executorService.submit(() -> {
            long spanId = lookupTimeKeeper.start();

            try (Connection connection = rwConnectionProvider.getConnection(); PreparedStatement lookupStmt = connection
                    .prepareStatement("select mem from securities where name = ?")) {
                lookupStmt.setString(1, secKey);
                ResultSet rs = lookupStmt.executeQuery();

                Object x = null;
                while (rs.next()) {
                    x = rs.getBytes("mem");
                }
                rs.close();
                lookupTimeKeeper.stop(spanId);
                completableFuture.complete(null);

            } catch (Exception ex) {
                ex.printStackTrace();
                completableFuture.completeExceptionally(ex);
            }
        });
        return completableFuture;
    }

    public CompletableFuture<Void> getMemsByKeys(List<String> secKeys, TimeKeeper lookupTimeKeeper) {
        CompletableFuture<Void> completableFuture = new CompletableFuture();
        int size = secKeys.size();
        executorService.submit(() -> {
            long spanId = lookupTimeKeeper.start();
            String sql = String.format("select mem from securities where name in (%s)",
                    String.join(",", Collections.nCopies(size, "?")));

            try (Connection connection = rwConnectionProvider.getConnection();
                    PreparedStatement lookupStmt = connection.prepareStatement(sql)) {
                for (int i = 0; i < size; i++) {
                    lookupStmt.setString(i + 1, secKeys.get(i));
                }

                ResultSet rs = lookupStmt.executeQuery();
                long cnt = 0;
                Object x = null;
                while (rs.next()) {
                    x = rs.getBytes("mem");
                    cnt++;
                }
                if (size != cnt) {
                    completableFuture
                            .completeExceptionally(new RuntimeException("the count of fetched recs dont match"));
                }
                rs.close();
                lookupTimeKeeper.stop(spanId);
                completableFuture.complete(null);

            } catch (Exception ex) {
                ex.printStackTrace();
                completableFuture.completeExceptionally(ex);
            }
        });
        return completableFuture;
    }

    public CompletableFuture<Long> countRecs(TimeKeeper lookupTimeKeeper) {
        CompletableFuture<Long> completableFuture = new CompletableFuture();
        executorService.submit(() -> {
            long spanId = lookupTimeKeeper.start();

            try (Connection connection = rwConnectionProvider.getConnection();
                    PreparedStatement lookupStmt = connection.prepareStatement("select count(name) from securities")) {
                ResultSet rs = lookupStmt.executeQuery();
                Long x = null;
                while (rs.next()) {
                    x = rs.getLong(1);
                }
                rs.close();
                lookupTimeKeeper.stop(spanId);
                completableFuture.complete(x);

            } catch (Exception ex) {
                ex.printStackTrace();
                completableFuture.completeExceptionally(ex);
            }
        });
        return completableFuture;
    }

    public void close() {
        executorService.shutdown();
    }

    /*
     * 
     * 
     * 
     * private static void randomLookUpByPrefix(int limit) { try { AuroraDriverManager driver = new
     * AuroraDriverManager(); Connection connection = driver.getConnection("primary");
     * 
     * PreparedStatement lookupStmt =
     * connection.prepareStatement("select name from securities where lower(name) >= ? order by name asc LIMIT " +
     * limit); Iterator<Integer> randIntStream = new SplittableRandom().ints().iterator();
     * 
     * while(true) { lookupStmt.setString(1, String.format("testSec-%d", randIntStream.next() % 1000 ));
     * 
     * long start = System.currentTimeMillis(); ResultSet rs = lookupStmt.executeQuery(); long matchCount = 0;
     * while(rs.next()) { matchCount++; } long elapsedTime = (System.currentTimeMillis() - start); double avgTimePerRec
     * = matchCount == 0 ? 0 : ((double)elapsedTime) / ((double)matchCount);
     * System.out.println(" lookup :: matched records " + matchCount + " elapsed time " + elapsedTime + "(ms)" +
     * " time per record " + avgTimePerRec + "(ms)");
     * 
     * Thread.sleep(4000); }
     * 
     * } catch (Exception ex) { ex.printStackTrace(); }
     * 
     * 
     * }
     * 
     * private static void randomLookUpByTypeAndPrefix(int limit) { try { AuroraDriverManager driver = new
     * AuroraDriverManager(); Connection connection = driver.getConnection("primary");
     * 
     * PreparedStatement lookupStmt = connection.
     * prepareStatement("select name from securities where lower(name) >= ? and typeId = ? order by name asc LIMIT " +
     * limit ); Iterator<Integer> randIntStream = new SplittableRandom().ints().iterator(); while(true) {
     * 
     * lookupStmt.setString(1, String.format("testSec-%d", randIntStream.next() % 10 )); lookupStmt.setInt(2,
     * TYPE_IDS.get(Math.abs(randIntStream.next() % 10)));
     * 
     * long start = System.currentTimeMillis(); ResultSet rs = lookupStmt.executeQuery(); long matchCount = 0;
     * while(rs.next()) { matchCount++; } long elapsedTime = (System.currentTimeMillis() - start); double avgTimePerRec
     * = matchCount == 0 ? 0 : ((double)elapsedTime) / ((double)matchCount);
     * System.out.println(" lookup by type :: matched records " + matchCount + " elapsed time " + elapsedTime + "(ms)" +
     * " time per record " + avgTimePerRec + "(ms)");
     * 
     * Thread.sleep(4000); }
     * 
     * } catch (Exception ex) { ex.printStackTrace(); }
     * 
     * 
     * }
     * 
     * 
     * private static void getSecurity() {
     * 
     * try {
     * 
     * AuroraDriverManager driver = new AuroraDriverManager(); Connection connection = driver.getConnection("primary");
     * 
     * Iterator<Integer> randIntStream = new SplittableRandom().ints().iterator(); PreparedStatement lookupStmt =
     * connection.prepareStatement("select name from securities where name = ?" ); lookupStmt.setFetchSize(10);
     * while(true) {
     * 
     * lookupStmt.setString(1, SEC_NAMES.get(Math.abs(randIntStream.next() % 10))); System.out.println(lookupStmt); long
     * start = System.currentTimeMillis(); ResultSet rs = lookupStmt.executeQuery(); long matchCount = 0;
     * while(rs.next()) { ++matchCount; } long elapsedTime = (System.currentTimeMillis() - start); double avgTimePerRec
     * = matchCount == 0 ? 0 : ((double)elapsedTime) / ((double)matchCount);
     * System.out.println(" Get Security :: matched records " + matchCount + " elapsed time " + elapsedTime + "(ms)" +
     * " time per record " + avgTimePerRec + "(ms)");
     * 
     * Thread.sleep(4000); }
     * 
     * 
     * } catch (Exception ex) { ex.printStackTrace(); }
     * 
     * 
     * }
     */

}