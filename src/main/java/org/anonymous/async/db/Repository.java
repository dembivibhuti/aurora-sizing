package org.anonymous.async.db;

import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.ConnectionPoolConfigurationBuilder;
import com.github.jasync.sql.db.RowData;
import com.github.jasync.sql.db.SSLConfiguration;
import com.github.jasync.sql.db.postgresql.PostgreSQLConnectionBuilder;
import com.google.protobuf.ByteString;
import org.anonymous.grpc.CmdGetByNameExtResponse;
import org.anonymous.grpc.Metadata;
import org.anonymous.module.ObjectRepository;
import org.anonymous.module.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.github.jasync.sql.db.SSLConfiguration.Mode.Prefer;
import static org.anonymous.sql.Store.GET_FULL_OBJECT;
import static org.anonymous.sql.Store.LOOKUP_OBJECTS;

public class Repository implements AutoCloseable {

    private final Connection connection;

    public Repository() {
        ConnectionPoolConfigurationBuilder connectionPoolConfigurationBuilder = new ConnectionPoolConfigurationBuilder();
        connectionPoolConfigurationBuilder.setHost(System.getProperty("dataSource.roserverName"));
        connectionPoolConfigurationBuilder.setPort(Integer.parseInt(System.getProperty("dataSource.portNumber")));
        connectionPoolConfigurationBuilder.setDatabase(System.getProperty("dataSource.databaseName"));
        connectionPoolConfigurationBuilder.setUsername(System.getProperty("dataSource.user"));
        connectionPoolConfigurationBuilder.setPassword(System.getProperty("dataSource.password"));
        connectionPoolConfigurationBuilder.setMaxActiveConnections(Integer.parseInt(System.getProperty("roMaximumPoolSize")));
        connectionPoolConfigurationBuilder.setSsl(new SSLConfiguration(Prefer, null, null, null));
        connection = PostgreSQLConnectionBuilder.createConnectionPool(connectionPoolConfigurationBuilder.build());
    }


    @Override
    public void close() throws ExecutionException, InterruptedException {
        connection.disconnect().get();
    }

    public CompletableFuture<List<String>> lookup(String prefix, int typeId, int limit) {
        Pair<String, String> exp = ObjectRepository.expression(typeId);
        return connection.sendPreparedStatement(String.format(LOOKUP_OBJECTS, exp.first, exp.second), Arrays.asList(prefix.toLowerCase(), limit), true)
                .thenApply(queryResult -> queryResult.getRows().stream().map(row -> row.getString(0)).collect(Collectors.toList()));
    }

    public CompletableFuture<Optional<CmdGetByNameExtResponse.MsgOnSuccess>> getFullObject(String secKey) {
        return connection.sendPreparedStatement(GET_FULL_OBJECT, Arrays.asList(secKey.toLowerCase()), true)
                .thenApply(queryResult -> {
                    if (queryResult.getRows().size() == 1) {
                        RowData rowData = queryResult.getRows().get(0);
                        return Optional.of(CmdGetByNameExtResponse.MsgOnSuccess.newBuilder().
                                setMem(ByteString.copyFrom(rowData.<byte[]>getAs("mem"))).
                                setMetadata(Metadata.newBuilder().
                                        setSecurityName(rowData.getString("name")).
                                        setSecurityType(rowData.getInt("typeid")).
                                        setLastTxnId(rowData.getLong("lasttransaction")).
                                        setUpdateCount(rowData.getLong("updatecount")).
                                        setDateCreated(rowData.getInt("datecreated")).
                                        setDbIdUpdated(rowData.getInt("dbidupdated")).
                                        setVersionInfo(rowData.getInt("versioninfo")).
                                        setTimeUpdate(rowData.getDate("timeupdated").toString())).
                                build());
                    } else {
                        return Optional.empty();
                    }
                });
    }
}
