package org.anonymous.module;

import com.google.protobuf.ByteString;
import com.google.protobuf.ProtocolStringList;
import org.anonymous.connection.ConnectionProvider;
import org.anonymous.grpc.*;
import org.anonymous.stats.Statistics;
import org.anonymous.util.StopWatch;
import org.anonymous.util.TimeKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.anonymous.sql.Store.*;

public class ObjectRepository implements AutoCloseable {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(100);
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectRepository.class);
    private final ConnectionProvider roConnectionProvider;
    private final ConnectionProvider rwConnectionProvider;

    public ObjectRepository(ConnectionProvider roConnectionProvider, ConnectionProvider rwConnectionProvider) {
        this.roConnectionProvider = roConnectionProvider;
        this.rwConnectionProvider = rwConnectionProvider;
    }

    public void runDDL(boolean dropAndCreate) {

        try (Connection connection = rwConnectionProvider.getConnection()) {

            try {
                if (dropAndCreate) {
                    connection.prepareStatement(DROP_TABLE).executeUpdate();
                    connection.commit();
                }
            } catch (Exception ex) {
                LOGGER.error("failed to drop table, will try creating it ", ex);
            }

            connection.prepareStatement(CREATE_TABLE).executeUpdate();
            LOGGER.info(" created objects table ");
            connection.commit();

            connection.prepareStatement(CREATE_RECORD_INDEX_BY_TYPEID_NAME)
                    .executeUpdate();
            LOGGER.info(" created index by typeId and name ");

            connection.prepareStatement(CREATE_RECORD_INDEX_BY_LOWER_NAME)
                    .executeUpdate();
            LOGGER.info(" created index by lower name ");

            connection.prepareStatement(CREATE_RECORD_INDEX_BY_NAME)
                    .executeUpdate();
            LOGGER.info(" created index by name ");

            connection.prepareStatement(CREATE_TXN_ID_SEQ)
                    .executeUpdate();
            LOGGER.info(" created txn sequence ");

            connection.prepareStatement(CREATE_TRANS_HEADER_TABLE)
                    .executeUpdate();
            LOGGER.info(" created trans header table ");

            connection.prepareStatement(CREATE_TRANS_PARTS_TABLE)
                    .executeUpdate();
            LOGGER.info(" created trans parts table ");
            connection.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public CompletableFuture load(int recordCount, int threadCount, int objSize, TimeKeeper secInsertTimeKeeper) {
        int numberOfRecsPerThread = recordCount / threadCount;
        CompletableFuture[] all = new CompletableFuture[threadCount + 1]; // extra 1 for the remaining records

        Iterator<Integer> it = IntStream.range(0, threadCount).iterator();
        while (it.hasNext()) {
            int batchId = it.next();
            CompletableFuture completableFuture = new CompletableFuture();
            all[batchId] = completableFuture;
            executorService.submit(() -> {
                StopWatch.start(String.format("sec.insert.batch.%d", batchId));
                insertRecs(numberOfRecsPerThread, completableFuture, objSize, secInsertTimeKeeper);
                StopWatch.stop(String.format("sec.insert.batch.%d", batchId));
            });
        }

        int remainingRecs = recordCount % threadCount;
        if (remainingRecs > 0) {
            CompletableFuture completableFuture = new CompletableFuture();
            all[threadCount] = completableFuture;
            executorService.submit(() -> {
                StopWatch.stop(String.format("sec.insert.batch.%d", threadCount));
                insertRecs(remainingRecs, completableFuture, objSize, secInsertTimeKeeper);
                StopWatch.stop(String.format("sec.insert.batch.%d", threadCount));
            });
        } else {
            all[threadCount] = CompletableFuture.completedFuture("");
        }

        return CompletableFuture.allOf(all);
    }

    private void insertRecs(int numberOfRecsPerThread, CompletableFuture completableFuture, int objSize, TimeKeeper secInsertTimeKeeper) {

        try (Connection connection = rwConnectionProvider.getConnection(); PreparedStatement insertRec = connection
                .prepareStatement(INSERT_RECORDS)) {

            byte[] sdbMem = getSizedByteArray(100);
            byte[] mem = getSizedByteArray(objSize);

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
                insertRec.setString(11, name.toLowerCase());
                insertRec.executeUpdate();
                connection.commit();

                secInsertTimeKeeper.stop(spanId);
            }
            completableFuture.complete("");
            LOGGER.info("Inserted another {} records", numberOfRecsPerThread);

        } catch (Exception ex) {
            ex.printStackTrace();
            completableFuture.completeExceptionally(ex);
        }
    }

    public void insertObjectsFromCSV(int totalSecurities, List<String[]> allData, TimeKeeper secInsertTimeKeeper) {

        try (Connection connection = rwConnectionProvider.getConnection(); PreparedStatement insertRec = connection
                .prepareStatement(INSERT_RECORDS)) {

            for (String[] row : allData) {
                int numObjects = Integer.parseInt(row[2]);
                int objMemSize = Integer.parseInt(row[1]);

                byte[] objPropertyMem = getSizedByteArray(100);
                byte[] mem = getSizedByteArray(objMemSize);
                int objClassId = Integer.parseInt(row[0]);

                Iterator<Integer> randIntStream = new SplittableRandom().ints().iterator();
                Iterator<Long> randLongStream = new SplittableRandom().longs().iterator();

                for (int i = 0; i < numObjects; i++) {

                    String name = String.format("testSec-%d-%d", randIntStream.next(), objClassId);
                    Timestamp timeStampCreated = new Timestamp(randIntStream.next() * 1000L);

                    long spanId = secInsertTimeKeeper.start();
                    insertRec.setString(1, name);
                    insertRec.setInt(2, objClassId);
                    insertRec.setLong(3, randLongStream.next());
                    insertRec.setTimestamp(4, timeStampCreated);
                    insertRec.setLong(5, randLongStream.next());
                    insertRec.setInt(6, (short) (timeStampCreated.getTime() / 1000));
                    insertRec.setInt(7, randIntStream.next());
                    insertRec.setInt(8, randIntStream.next());
                    insertRec.setBytes(9, objPropertyMem);
                    insertRec.setBytes(10, mem);
                    insertRec.setString(11, name.toLowerCase());
                    insertRec.executeUpdate();
                    connection.commit();

                    secInsertTimeKeeper.stop(spanId);
                }
                LOGGER.info("Inserted another {} records", numObjects);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private static byte[] getSizedByteArray(int size) {
        byte[] result = new byte[size];
        Arrays.fill(result, (byte) 'a');
        return result;
    }

    public CompletableFuture<List<String>> randomLookUpByPrefix(int numberOfLookupOps, int limit,
                                                                TimeKeeper lookupTimeKeeper) {
        CompletableFuture<List<String>> completableFuture = new CompletableFuture();
        List<String> secKeys = new ArrayList<>();
        Iterator<Integer> randIntStream = new SplittableRandom().ints().iterator();
        try (Connection connection = rwConnectionProvider.getConnection(); PreparedStatement lookupStmt = connection
                .prepareStatement(LOOKUP_OBJECTS)) {

            while (numberOfLookupOps > 0) {
                numberOfLookupOps--;

                long spanId = lookupTimeKeeper.start();
                lookupStmt.setString(1, String.format("testSec-%d", randIntStream.next() % 1000));
                lookupStmt.setInt(2, limit);
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
                             LOOKUP_OBJECTS_BY_TYPEID)) {
                    lookupStmt.setString(1, String.format("testSec-%d", randIntStream.next() % 1000));
                    lookupStmt.setInt(2, limit);
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

    private Pair<String, String> expression(int number) {
        Pair<String, String> pair = new Pair(">=", "asc"); //default value
        if (number == 2) {
            pair.first = "=";
            pair.second = "asc";
        } else if (number == 3) {
            pair.first = "<=";
            pair.second = "asc";
        } else if (number == 4) {
            pair.first = "<=";
            pair.second = "desc";
        } else if (number == 5) {
            pair.first = ">=";
            pair.second = "asc";
        } else if (number == 6) {
            pair.first = ">=";
            pair.second = "desc";
        }
        return pair;
    }

    public List<String> lookup(String name, int typeId, int limit) {
        Pair<String, String> exp = expression(typeId);
        List<String> secKeys = new ArrayList<>();
        try (Connection connection = roConnectionProvider.getConnection(); PreparedStatement lookupStmt = connection
                .prepareStatement(String.format(LOOKUP_OBJECTS, exp.first, exp.second))) {

            lookupStmt.setString(1, name.toLowerCase());
            lookupStmt.setInt(2, limit);

            ResultSet rs = lookupStmt.executeQuery();

            while (rs.next()) {
                secKeys.add(rs.getString(1));
            }
            rs.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return secKeys;
    }

    public List<String> lookupById(String name, int typeId, int limit, int objectType) {
        Pair<String, String> exp = expression(typeId);
        List<String> secKeys = new ArrayList<>();
        try (Connection connection = roConnectionProvider.getConnection(); PreparedStatement lookupStmt = connection
                .prepareStatement(String.format(LOOKUP_OBJECTS_BY_TYPEID, exp.first, exp.second))) {

            lookupStmt.setString(1, name.toLowerCase());
            lookupStmt.setInt(2, objectType);
            lookupStmt.setInt(3, limit);
            ResultSet rs = lookupStmt.executeQuery();

            while (rs.next()) {
                secKeys.add(rs.getString(1));
            }
            rs.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return secKeys;
    }

    public CompletableFuture<Void> getSDBByKey(String secKey, TimeKeeper lookupTimeKeeper) {
        CompletableFuture<Void> completableFuture = new CompletableFuture();
        executorService.submit(() -> {
            long spanId = lookupTimeKeeper.start();

            try (Connection connection = rwConnectionProvider.getConnection();
                 PreparedStatement lookupStmt = connection.prepareStatement(
                         GET_RECORDS)) {
                lookupStmt.setString(1, secKey.toLowerCase());
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
                    GET_MANY_RECORDS,
                    String.join(",", Collections.nCopies(secKeys.size(), "?")));

            try (Connection connection = rwConnectionProvider.getConnection();
                 PreparedStatement lookupStmt = connection.prepareStatement(sql)) {
                for (int i = 0; i < secKeys.size(); i++) {
                    lookupStmt.setString(i + 1, secKeys.get(i).toLowerCase());
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
                    .prepareStatement(GET_SDB_MEM)) {
                lookupStmt.setString(1, secKey.toLowerCase());
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
            String sql = String.format(GET_MANY_SDB_RECORDS,
                    String.join(",", Collections.nCopies(size, "?")));

            try (Connection connection = rwConnectionProvider.getConnection();
                 PreparedStatement lookupStmt = connection.prepareStatement(sql)) {
                for (int i = 0; i < size; i++) {
                    lookupStmt.setString(i + 1, secKeys.get(i).toLowerCase());
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
                    .prepareStatement(GET_MEM)) {
                lookupStmt.setString(1, secKey.toLowerCase());
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

    /*public Optional<byte[]> getMemByKeyInBytes(final String secKey) {
        byte[] arrayContainsMem = null;
        try (Connection connection = roConnectionProvider.getConnection(); PreparedStatement lookupStmt = connection
                .prepareStatement(GET_MEM)) {
            lookupStmt.setString(1, secKey.toLowerCase());
            ResultSet rs = lookupStmt.executeQuery();
            while (rs.next()) {
                arrayContainsMem = rs.getBytes("mem");
            }
            rs.close();
        } catch (SQLException throwables) {
            LOGGER.error("error in getMemByKeyInBytes()", throwables);
        }
        return Optional.ofNullable(arrayContainsMem);
    }

    public Optional<CmdGetByNameExtResponse.MsgOnSuccess> getFullObject(final String secKey) {
        CmdGetByNameExtResponse.MsgOnSuccess msgOnSuccess = null;
        try (Connection connection = roConnectionProvider.getConnection();
             PreparedStatement lookupStmt = connection.prepareStatement(
                     GET_FULL_OBJECT)) {
            lookupStmt.setString(1, secKey.toLowerCase());
            ResultSet rs = lookupStmt.executeQuery();
            if(rs.next()) {
                msgOnSuccess = CmdGetByNameExtResponse.MsgOnSuccess.newBuilder().
                        setMem(ByteString.copyFrom(rs.getBytes("mem"))).
                        setMetadata(Metadata.newBuilder().
                                setSecurityName(rs.getString("name")).
                                setSecurityType(rs.getInt("typeId")).
                                setLastTxnId(rs.getLong("lastTransaction")).
                                setUpdateCount(rs.getLong("updateCount")).
                                setDateCreated(rs.getInt("dateCreated")).
                                setDbIdUpdated(rs.getInt("dbIdUpdated")).
                                setVersionInfo(rs.getInt("versionInfo")).
                                setTimeUpdate(rs.getString("timeUpdated"))).
                        build();
            }
            rs.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Optional.ofNullable(msgOnSuccess);
    }

    */

    // Modified for more stats collection
    public Optional<byte[]> getMemByKeyInBytes(final String secKey) {
        byte[] arrayContainsMem = null;
        try {
            long span = Statistics.getObjectDBGetConnection.start();
            Connection connection = roConnectionProvider.getConnection();
            Statistics.getObjectDBGetConnection.stop(span);

            span = Statistics.getObjectDBPreparedStatementMake.start();
            PreparedStatement lookupStmt = connection.prepareStatement(GET_MEM);
            lookupStmt.setString(1, secKey.toLowerCase());
            Statistics.getObjectDBPreparedStatementMake.stop(span);

            span = Statistics.getObjectDBResultSetFetch.start();
            ResultSet rs = lookupStmt.executeQuery();
            while (rs.next()) {
                arrayContainsMem = rs.getBytes("mem");
            }
            Statistics.getObjectDBResultSetFetch.stop(span);


            // The Connection delegate will close all Statement, the closure of Statement will close the Resultset
            //rs.close();
            //lookupStmt.close();

            span = Statistics.getObjectDBCloseResource.start();
            connection.close();
            Statistics.getObjectDBCloseResource.stop(span);

        } catch (SQLException sqlException) {
            LOGGER.error("error in getMemByKeyInBytes()", sqlException);
        }
        return Optional.ofNullable(arrayContainsMem);
    }

    public Optional<CmdGetByNameExtResponse.MsgOnSuccess> getFullObject(final String secKey) {
        CmdGetByNameExtResponse.MsgOnSuccess msgOnSuccess = null;


        try {
            long span = Statistics.getObjectExtDBGetConnection.start();
            Connection connection = roConnectionProvider.getConnection();
            Statistics.getObjectExtDBGetConnection.stop(span);

            span = Statistics.getObjectExtDBPreparedStatementMake.start();
            PreparedStatement lookupStmt = connection.prepareStatement(GET_FULL_OBJECT);
            lookupStmt.setString(1, secKey.toLowerCase());
            Statistics.getObjectExtDBPreparedStatementMake.stop(span);

            span = Statistics.getObjectExtDBResultSetFetch.start();
            ResultSet rs = lookupStmt.executeQuery();
            if (rs.next()) {
                msgOnSuccess = CmdGetByNameExtResponse.MsgOnSuccess.newBuilder().
                        setMem(ByteString.copyFrom(rs.getBytes("mem"))).
                        setMetadata(Metadata.newBuilder().
                                setSecurityName(rs.getString("name")).
                                setSecurityType(rs.getInt("typeId")).
                                setLastTxnId(rs.getLong("lastTransaction")).
                                setUpdateCount(rs.getLong("updateCount")).
                                setDateCreated(rs.getInt("dateCreated")).
                                setDbIdUpdated(rs.getInt("dbIdUpdated")).
                                setVersionInfo(rs.getInt("versionInfo")).
                                setTimeUpdate(rs.getString("timeUpdated"))).
                        build();
            }
            Statistics.getObjectExtDBResultSetFetch.stop(span);

            span = Statistics.getObjectExtDBCloseResource.start();
            rs.close();
            lookupStmt.close();
            connection.close();
            Statistics.getObjectExtDBCloseResource.stop(span);

        } catch (SQLException sqlException) {
            LOGGER.error("error in getFullObject()", sqlException);
        }
        return Optional.ofNullable(msgOnSuccess);
    }

    public CompletableFuture<Void> getMemsByKeys(List<String> secKeys, TimeKeeper lookupTimeKeeper) {
        CompletableFuture<Void> completableFuture = new CompletableFuture();
        int size = secKeys.size();
        executorService.submit(() -> {
            long spanId = lookupTimeKeeper.start();
            String sql = String.format(GET_MANY_MEM_RECORDS,
                    String.join(",", Collections.nCopies(size, "?")));

            try (Connection connection = rwConnectionProvider.getConnection();
                 PreparedStatement lookupStmt = connection.prepareStatement(sql)) {
                for (int i = 0; i < size; i++) {
                    lookupStmt.setString(i + 1, secKeys.get(i).toLowerCase());
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
                 PreparedStatement lookupStmt = connection.prepareStatement(COUNT_RECORDS)) {
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

    public List<ByteString> getManyMemByName(ProtocolStringList securityNameList) {
        List<ByteString> secMem = new ArrayList<>();
        String sql = String.format(GET_MANY_MEM_RECORDS, String.join(",", Collections.nCopies(securityNameList.size(), "?")));
        try (Connection connection = roConnectionProvider.getConnection();
             PreparedStatement getManyStmt = connection.prepareStatement(sql)) {

            for (int i = 0; i < securityNameList.toArray().length; i++) {
                getManyStmt.setString(i + 1, securityNameList.get(i).toLowerCase());
            }
            ResultSet rs = getManyStmt.executeQuery();
            while (rs.next()) {
                secMem.add(ByteString.copyFrom(rs.getBytes("mem")));
            }
            rs.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return secMem;
    }

    public List<CmdGetManyByNameExtResponse.ResponseMessage> getManyFullSecurities(ProtocolStringList securityNameList) {
        List<CmdGetManyByNameExtResponse.ResponseMessage> responseMessages = new ArrayList<>();
        String sql = String.format(GET_MANY_RECORDS, String.join(",", Collections.nCopies(securityNameList.size(), "?")));
        try (Connection connection = roConnectionProvider.getConnection();
             PreparedStatement getManyStmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < securityNameList.toArray().length; i++) {
                getManyStmt.setString(i + 1, securityNameList.get(i).toLowerCase());
            }
            ResultSet rs = getManyStmt.executeQuery();
            while (rs.next()) {
                try {
                    Metadata metadata = Metadata.newBuilder().setSecurityName(rs.getString("name"))
                            .setSecurityType(rs.getInt("typeId"))
                            .setUpdateCount(rs.getLong("updateCount"))
                            .setDateCreated(rs.getInt("dateCreated"))
                            .setTimeUpdate(rs.getString("timeUpdated"))
                            .setDbIdUpdated(rs.getInt("dbIdUpdated"))
                            .setLastTxnId(rs.getLong("lastTransaction"))
                            .setVersionInfo(rs.getInt("versionInfo")).build();
                    CmdGetManyByNameExtResponse.ResponseMessage.MsgOnSuccess msgOnSuccess = CmdGetManyByNameExtResponse.ResponseMessage.MsgOnSuccess.newBuilder()
                            .setMetadata(metadata)
                            .setMem(ByteString.copyFrom(rs.getBytes("mem")))
                            .setHasSucceeded(true).build();
                    responseMessages.add(CmdGetManyByNameExtResponse.ResponseMessage.newBuilder().setMsgOnSuccess(msgOnSuccess).build());
                } catch (SQLException throwables) {
                    CmdGetManyByNameExtResponse.ResponseMessage.MsgOnFailure msgOnFailure = CmdGetManyByNameExtResponse.ResponseMessage.MsgOnFailure.newBuilder().setHasSucceeded(false).build();
                    responseMessages.add(CmdGetManyByNameExtResponse.ResponseMessage.newBuilder().setMsgOnFailure(msgOnFailure).build());
                    throwables.printStackTrace();
                }
            }
            rs.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return responseMessages;
    }

    public List<CmdGetManyByNameExtResponseStream> getManySDBByNameStream(ProtocolStringList securityNameList) {
        List<CmdGetManyByNameExtResponseStream> responseMessages = new ArrayList<>();
        String sql = String.format(GET_MANY_RECORDS, String.join(",", Collections.nCopies(securityNameList.size(), "?")));
        try (Connection connection = roConnectionProvider.getConnection();
             PreparedStatement getManyStmt = connection.prepareStatement(sql)) {

            for (int i = 0; i < securityNameList.toArray().length; i++) {
                getManyStmt.setString(i + 1, securityNameList.get(i).toLowerCase());
            }
            ResultSet rs = getManyStmt.executeQuery();
            while (rs.next()) {
                try {
                    Metadata metadata = Metadata.newBuilder().setSecurityName(rs.getString("name"))
                            .setSecurityType(rs.getInt("typeId"))
                            .setUpdateCount(rs.getLong("updateCount"))
                            .setDateCreated(rs.getInt("dateCreated"))
                            .setTimeUpdate(rs.getString("timeUpdated"))
                            .setDbIdUpdated(rs.getInt("dbIdUpdated"))
                            .setLastTxnId(rs.getLong("lastTransaction"))
                            .setVersionInfo(rs.getInt("versionInfo")).build();
                    CmdGetManyByNameExtResponseStream.MsgOnSuccess msgOnSuccess = CmdGetManyByNameExtResponseStream.MsgOnSuccess.newBuilder()
                            .setMetadata(metadata)
                            .setMem(ByteString.copyFrom(rs.getBytes("mem")))
                            .setHasSucceeded(true).build();
                    responseMessages.add(CmdGetManyByNameExtResponseStream.newBuilder().setMsgOnSuccess(msgOnSuccess).build());
                } catch (SQLException throwables) {
                    CmdGetManyByNameExtResponseStream.MsgOnFailure msgOnFailure = CmdGetManyByNameExtResponseStream.MsgOnFailure.newBuilder().setHasSucceeded(false).build();
                    responseMessages.add(CmdGetManyByNameExtResponseStream.newBuilder().setMsgOnFailure(msgOnFailure).build());
                    throwables.printStackTrace();
                }
            }
            rs.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return responseMessages;
    }

    public boolean insertRec(Connection connection, CmdInsert cmdInsert, long nextTxnId) throws SQLException {
        int rowsAffected = 0;
        Metadata metadata = cmdInsert.getMetadata();
        try (PreparedStatement insertRecStmt = connection
                .prepareStatement(INSERT_RECORDS)) {
            insertRecStmt.setString(1, metadata.getSecurityName());
            insertRecStmt.setInt(2, metadata.getSecurityType());
            insertRecStmt.setLong(3, nextTxnId);
            insertRecStmt.setTimestamp(4, Timestamp.valueOf(metadata.getTimeUpdate()));
            insertRecStmt.setLong(5, metadata.getUpdateCount());
            insertRecStmt.setInt(6, metadata.getDateCreated());
            insertRecStmt.setInt(7, metadata.getDbIdUpdated());
            insertRecStmt.setInt(8, metadata.getVersionInfo());
            insertRecStmt.setBytes(9, getSizedByteArray(100));
            insertRecStmt.setBytes(10, cmdInsert.getMem().toByteArray());
            insertRecStmt.setString(11, metadata.getSecurityName().toLowerCase());
            rowsAffected = insertRecStmt.executeUpdate();
        } catch (SQLException sqlException) {
            throw sqlException;
        }
        return rowsAffected == 1;
    }

    public boolean updateRec(Connection connection, CmdUpdate cmdUpdate, long nextTxnId) throws SQLException {
        int rowsAffected = 0;
        Metadata oldMetaData = cmdUpdate.getOldMetadata();
        Metadata newMetaData = cmdUpdate.getNewMetadata();
        try (PreparedStatement updateRecStmt = connection
                .prepareStatement(UPDATE_RECORDS)) {
            updateRecStmt.setInt(1, newMetaData.getSecurityType());
            updateRecStmt.setLong(2, nextTxnId);
            updateRecStmt.setTimestamp(3, Timestamp.valueOf(newMetaData.getTimeUpdate()));
            updateRecStmt.setLong(4, newMetaData.getUpdateCount());
            updateRecStmt.setInt(5, newMetaData.getDateCreated());
            updateRecStmt.setInt(6, newMetaData.getDbIdUpdated());
            updateRecStmt.setInt(7, newMetaData.getVersionInfo());
            updateRecStmt.setBytes(8, getSizedByteArray(100));
            updateRecStmt.setBytes(9, cmdUpdate.getMem().toByteArray());
            updateRecStmt.setString(10, oldMetaData.getSecurityName());
            updateRecStmt.setLong(11, oldMetaData.getUpdateCount());
            updateRecStmt.setInt(12, oldMetaData.getDbIdUpdated());

            rowsAffected = updateRecStmt.executeUpdate();

        } catch (SQLException sqlException) {
            throw sqlException;
        }
        return rowsAffected == 1;
    }

    public boolean deleteDataRecords(Connection connection, CmdDelete cmdDeleteData) throws SQLException {
        int rowsAffected = 0;
        Metadata metadata = cmdDeleteData.getMetadata();
        try (PreparedStatement deleteRecStmt = connection.prepareStatement(
                DELETE_RECORDS)) {
            deleteRecStmt.setString(1, metadata.getSecurityName());
            deleteRecStmt.setLong(2, metadata.getUpdateCount());
            deleteRecStmt.setInt(3, metadata.getDbIdUpdated());

            rowsAffected = deleteRecStmt.executeUpdate();

        } catch (SQLException sqlException) {
            throw sqlException;
        }
        return rowsAffected == 1;
    }

    public boolean renameDataRecords(Connection connection, CmdRename cmdRenameData, long nextTxnId) throws SQLException {
        TransMsgResponse.MsgOnSuccess msgOnSuccess = null;
        int rowsAffected = 0;
        Metadata oldMetadata = cmdRenameData.getOldMetadata();
        Metadata newMetadata = cmdRenameData.getNewMetadata();
        try (PreparedStatement renameRecStmt = connection.prepareStatement(
                RENAME_RECORDS)) {
            renameRecStmt.setString(1, newMetadata.getSecurityName());
            renameRecStmt.setString(2, newMetadata.getSecurityName().toLowerCase());
            renameRecStmt.setLong(3, nextTxnId);
            renameRecStmt.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
            renameRecStmt.setLong(5, newMetadata.getUpdateCount());
            renameRecStmt.setInt(6, newMetadata.getDateCreated());
            renameRecStmt.setInt(7, newMetadata.getDbIdUpdated());
            renameRecStmt.setInt(8, newMetadata.getVersionInfo());
            renameRecStmt.setBytes(9, getSizedByteArray(100));   // will change
            renameRecStmt.setString(10, oldMetadata.getSecurityName());
            renameRecStmt.setLong(11, oldMetadata.getUpdateCount());
            renameRecStmt.setInt(12, oldMetadata.getDbIdUpdated());

            rowsAffected = renameRecStmt.executeUpdate();

        } catch (SQLException sqlException) {
            throw sqlException;
        }
        return rowsAffected == 1;
    }

    public boolean commitTransaction(Connection connection) throws SQLException {
        boolean success = true;
        try {
            connection.commit();
        } catch (SQLException sqlException) {
            success = false;
            throw sqlException;
        }
        return success;
    }

    public Connection getConnectionObjRepo() throws SQLException {
        LOGGER.info("Connection Created");
        return rwConnectionProvider.getConnection();
    }

    public boolean insertHeaderLog(Connection connection, CmdTransHeader header, long nextTxnId) throws SQLException {
        int rowsAffected = 0;
        try (PreparedStatement insertLogStmt = connection
                .prepareStatement(INSERT_TRANS_HEADER)) {
            insertLogStmt.setLong(1, nextTxnId);
            insertLogStmt.setInt(2, header.getDbId());
            insertLogStmt.setLong(3, header.getSourceTransId());
            insertLogStmt.setInt(4, header.getTransType());
            insertLogStmt.setTimestamp(5, new Timestamp(0));
            insertLogStmt.setString(6, header.getTransName());
            insertLogStmt.setInt(7, header.getSecType());
            insertLogStmt.setString(8, "");
            insertLogStmt.setString(9, "");//no application name in proto
            insertLogStmt.setString(10, "");//no username in proto
            insertLogStmt.setString(11, "");
            insertLogStmt.setString(12, "");
            insertLogStmt.setLong(13, 0);//no network address in proto
            insertLogStmt.setString(14, "");
            insertLogStmt.setInt(15, header.getTransFlags());
            insertLogStmt.setInt(16, header.getDetailParts());
            insertLogStmt.setInt(17, 0);
            insertLogStmt.setTimestamp(18, new Timestamp(System.currentTimeMillis()));
            insertLogStmt.setBytes(19, new byte[0]);
            rowsAffected = insertLogStmt.executeUpdate();
        } catch (SQLException sqlException) {
            throw sqlException;
        }
        return rowsAffected == 1;
    }

    public boolean insertTranspartLog(Connection connection, CmdTransactionRequest request, long nextTxnId, int transpartIndex) throws SQLException {
        int rowsAffected = 0;
        try (PreparedStatement insertLogStmt = connection
                .prepareStatement(INSERT_TRANS_PARTS)) {
            insertLogStmt.setLong(1, nextTxnId);
            insertLogStmt.setInt(2, transpartIndex);
            insertLogStmt.setInt(3, 0);//find event code
            insertLogStmt.setInt(4, request.getTransSeqValue());//find op code
            insertLogStmt.setString(5, "0");
            insertLogStmt.setBoolean(6, false);
            insertLogStmt.setBoolean(7, false);
            insertLogStmt.setBoolean(8, false);
            insertLogStmt.setBoolean(9, false);
            insertLogStmt.setBytes(10, new byte[0]);
            rowsAffected = insertLogStmt.executeUpdate();
        } catch (SQLException sqlException) {
            throw sqlException;
        }
        return rowsAffected == 1;
    }

    public void close() {
        executorService.shutdown();
    }

}