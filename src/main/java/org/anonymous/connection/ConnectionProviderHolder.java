package org.anonymous.connection;

public class ConnectionProviderHolder implements AutoCloseable {
    public ConnectionProvider roConnectionProvider;
    public ConnectionProvider rwConnectionProvider;

    @Override
    public void close() {
        roConnectionProvider.close();
        rwConnectionProvider.close();
    }
}


