package org.anonymous.connection.pool;

import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;


public class SimpleDataSource2 implements AutoCloseable {

    private static Logger LOGGER = LoggerFactory.getLogger(SimpleDataSource2.class);
    private final ConcurrentLinkedQueue<Connection> recycledConnections = new ConcurrentLinkedQueue<>();          // list of inactive PooledConnections
    private final ExecutorService connRecyclerEventLoop = Executors.newSingleThreadExecutor(r -> new Thread(r, "db-conn-recycler-thread"));
    private final ExecutorService credsRefresherEventLoop = Executors.newSingleThreadExecutor(r -> new Thread(r, "iam-creds-refresher-thread"));
    private final ExecutorService connCheckerEventLoop = Executors.newSingleThreadExecutor(r -> new Thread(r, "db-conn-validity-check-thread"));

    private String poolName;
    private AtomicReference<String> user = new AtomicReference<>();
    private AtomicReference<String> pswd = new AtomicReference<>();
    private String databaseName;
    private String serverName;
    private int portNumber;
    private String currentSchema;
    private boolean ssl;
    private long validityCheckInterval;
    private AtomicReference<DataSource> dataSourceRef = new AtomicReference<>();
    private int maximumPoolSize;
    private int transactionIsolation;
    private long maxTimeout = 10000;
    private boolean autoCommit;
    private PrintWriter logWriter;
    private AtomicInteger activeConnections = new AtomicInteger();            // number of active (open) connections of this pool
    private final Consumer<Connection> closeHandler = new Consumer<Connection>() {
        @Override
        public void accept(Connection conn) {
            recycledConnections.add(conn);
            activeConnections.decrementAndGet();
        }
    };
    private AtomicBoolean isClosed = new AtomicBoolean(false);


    public SimpleDataSource2(String poolName, int maxTimeout, int maximumPoolSize, int transactionIsolation, boolean autoCommit, long validityCheckInterval, DataSource dataSource) {
        dataSourceRef.set(dataSource);
        this.poolName = poolName;
        this.maxTimeout = maxTimeout;
        this.maximumPoolSize = maximumPoolSize;
        this.transactionIsolation = transactionIsolation;
        this.autoCommit = autoCommit;
        this.validityCheckInterval = validityCheckInterval;

        LOGGER.info("Creating DB Connection Pool with autoCommit = {} transactionIsolation = {}", autoCommit, transactionIsolation);

        try {
            logWriter = dataSource.getLogWriter();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        // Pre-populate Connections
        for (int i = 0; i < maximumPoolSize; i++) {
            populateConnection();
        }

        connCheckerEventLoop.execute(() -> {
            checkConnection();
        });


        logStats();
    }

    public SimpleDataSource2(PoolProperties poolProperties) {
        LOGGER.info("Creating DB Connection Pool with {}", poolProperties.toString());
        this.poolName = poolProperties.poolName;
        this.user = new AtomicReference<>(poolProperties.user);
        this.pswd = new AtomicReference<>(poolProperties.pswd);
        this.maxTimeout = poolProperties.maxTimeout;
        this.maximumPoolSize = poolProperties.maximumPoolSize;
        this.databaseName = poolProperties.databaseName;
        this.serverName = poolProperties.serverName;
        this.portNumber = poolProperties.portNumber;
        this.currentSchema = poolProperties.currentSchema;
        this.transactionIsolation = poolProperties.transactionIsolation;
        this.autoCommit = poolProperties.autoCommit;
        this.ssl = poolProperties.ssl;
        this.validityCheckInterval = poolProperties.validityCheckInterval;

        DataSource dataSource = createDataSource();
        dataSourceRef.set(dataSource);

        try {
            logWriter = dataSource.getLogWriter();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        // Pre-populate Connections
        for (int i = 0; i < maximumPoolSize; i++) {
            populateConnection();
        }

        //Start the Creds Refresher Loop.
        credsRefresherEventLoop.execute(() -> {
            while (!isClosed.get()) {
                // configured wait time
                // get IAM Creds
                // set in user, and pswd fields
                // createDataSource()
                // dataSourceRef.set(pgSimpleDataSource);
            }
        });

        // Start the recycler Loop
        connRecyclerEventLoop.execute(() -> {
            while (!isClosed.get()) {
                // configured wait time
                try {
                    Thread.sleep(Long.MAX_VALUE); // Switch to configuration, when IAM Authen. works
                    ((ConnectionProxy<Connection>) getConnection()).getInner().close(); // pop and close the Connection
                    populateConnection();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });

        connCheckerEventLoop.execute(() -> {
            checkConnection();
        });

        logStats();
    }

    private void checkConnection() {
        while (!isClosed.get()) {
            try {
                Connection conn = null;
                while ((conn = recycledConnections.poll()) != null) {
                    if (!conn.isValid(0)) {
                        ((ConnectionProxy<Connection>) conn).getInner().close();
                        populateConnection();
                    } else {
                        recycledConnections.add(conn);
                    }
                    logStats();
                    Thread.sleep(validityCheckInterval);
                }
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private DataSource createDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUser(user.get());
        dataSource.setPassword(pswd.get());
        dataSource.setDatabaseName(databaseName);
        dataSource.setServerNames(new String[]{serverName});
        dataSource.setPortNumbers(new int[]{portNumber});
        dataSource.setCurrentSchema(currentSchema);
        if (ssl) {
            dataSource.setSsl(true);
            dataSource.setSslmode("verify-full");
            dataSource.setSslfactory("org.postgresql.ssl.DefaultJavaSSLFactory");
        }
        dataSource.setPrepareThreshold(1);
        return dataSource;
    }

    public synchronized void close() {
        if (isClosed.compareAndSet(false, true)) {
            return;
        }

        connCheckerEventLoop.shutdown();
        connRecyclerEventLoop.shutdown();
        credsRefresherEventLoop.shutdown();
        LOGGER.info("Starting SimpleDataSource2 Connection Pool Shutdown ..... ");
        logStats();

        SQLException e = null;
        while (!recycledConnections.isEmpty()) {
            Connection conn = recycledConnections.poll();
            try {
                ((ConnectionProxy<Connection>) conn).getInner().close();
            } catch (SQLException e2) {
                if (e == null) {
                    e = e2;
                }
            }
        }
        if (e != null) {
            e.printStackTrace();
        }
        logStats();
        LOGGER.info("SimpleDataSource2 Connection Pool is shutdown");
    }

    public Connection getConnection() {
        if (isClosed.get()) {
            throw new IllegalStateException("Connection pool has been disposed.");
        }

        long timeoutTime = System.currentTimeMillis() + maxTimeout;
        Connection conn;
        while ((conn = recycledConnections.poll()) == null) {
            if (System.currentTimeMillis() > timeoutTime) {
                throw new TimeoutException("Timeout while waiting for a valid database connection from Pool. maximumPoolSize = " + maximumPoolSize
                        + " Active Connections = " + activeConnections.get() + " Idle Connections = " + recycledConnections.size());
            }
        }
        activeConnections.incrementAndGet();
        return conn;
    }

    private void populateConnection() {
        try {
            Connection conn = dataSourceRef.get().getConnection();
            conn.setAutoCommit(autoCommit);
            conn.setTransactionIsolation(transactionIsolation);

            Connection proxy = ConnectionProxy.getProxy(conn, closeHandler, Connection.class);
            recycledConnections.add(proxy);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    private void log(String msg) {
        String s = "SimpleConnectionPool2: " + msg;
        try {
            if (logWriter == null) {
                System.err.println(s);
            } else {
                logWriter.println(s);
            }
        } catch (Exception e) {
        }
    }

    private synchronized void assertInnerState() {
        if (activeConnections.get() < 0) {
            throw new AssertionError();
        }
        if (activeConnections.get() + recycledConnections.size() > maximumPoolSize) {
            throw new AssertionError();
        }
    }

    private void logStats() {
        LOGGER.info("Pool = {} Max Pool Size = {} Active Connections = {} Inactive Connections = {}", poolName, maximumPoolSize, getActiveConnections(), getInactiveConnections());
    }

    public int getActiveConnections() {
        return activeConnections.get();
    }

    public int getInactiveConnections() {
        return recycledConnections.size();
    }

    public static class TimeoutException extends RuntimeException {
        private static final long serialVersionUID = 1;

        public TimeoutException() {
            super("Timeout while waiting for a free database connection.");
        }

        public TimeoutException(String msg) {
            super(msg);
        }
    }

    public static class ConnectionProxy<T> implements InvocationHandler {

        private final T t;
        private final Consumer<T> closeHandler;

        public ConnectionProxy(T t, Consumer<T> closeHandler) {
            this.t = t;
            this.closeHandler = closeHandler;
        }

        @SuppressWarnings("unchecked")
        public static <T> T getProxy(T t, Consumer<T> closeHandler, Class<? super T> interfaceType) {
            ConnectionProxy handler = new ConnectionProxy(t, closeHandler);
            return (T) Proxy.newProxyInstance(interfaceType.getClassLoader(),
                    new Class<?>[]{interfaceType}, handler
            );
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("close")) {
                closeHandler.accept((T) proxy);
                return null;
            } else {
                return method.invoke(t, args);
            }
        }

        public T getInner() {
            return t;
        }
    }

    public static class PoolProperties {
        public String poolName;
        public long maxTimeout;
        public int maximumPoolSize;
        public String user;
        public String pswd;
        public String databaseName;
        public String serverName;
        public int portNumber;
        public String currentSchema;
        public int transactionIsolation;
        public boolean autoCommit;
        public boolean ssl;
        public long validityCheckInterval;

        @Override
        public String toString() {
            return "PoolProperties{" +
                    "poolName=" + poolName +
                    ", maxTimeout=" + maxTimeout +
                    ", maximumPoolSize=" + maximumPoolSize +
                    ", user='" + user + '\'' +
                    ", pswd='" + "xxxx" + '\'' +
                    ", databaseName='" + databaseName + '\'' +
                    ", serverName='" + serverName + '\'' +
                    ", portNumber=" + portNumber +
                    ", currentSchema='" + currentSchema + '\'' +
                    ", transactionIsolation=" + transactionIsolation +
                    ", autoCommit=" + autoCommit +
                    ", ssl=" + ssl +
                    ", validityCheckInterval=" + validityCheckInterval +
                    '}';
        }
    }

}