package org.anonymous.server;

import io.prometheus.client.Gauge;
import org.anonymous.connection.ConnectionProviderHolder;
import org.anonymous.connection.HikariCPConnectionProvider;
import org.anonymous.domain.ObjectDTO;
import org.anonymous.module.ObjectRepository;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.Executors;

public class CachingTester {

    private static final ConnectionProviderHolder connectionProviderHolder = HikariCPConnectionProvider.create();
    private static final Thread shutdownHook = new Thread(() -> connectionProviderHolder.close());
    private static final ObjectRepository objectRepository = new ObjectRepository(connectionProviderHolder.roConnectionProvider, connectionProviderHolder.rwConnectionProvider);
    private static final Jedis jedis = new Jedis("aurora-sizing.uga7qd.ng.0001.use1.cache.amazonaws.com");
    private static final Gauge getObjFromCacheGaugeTimer = Gauge.build().name("get_object_cache").help("Get Object on Middleware from cache").labelNames("redis").register();
    private static final Gauge getObjFromDBGaugeTimer = Gauge.build().name("get_object_db").help("Get Object on Middleware from db").labelNames("db").register();
    public static final String SEC_KEY = "232574-46439-1-1326302";

    public static void main(String[] args) throws InterruptedException {
        Runtime.getRuntime().addShutdownHook(shutdownHook);
        GrpcServer.startMetricsServer();
        CachingTester cachingTester = new CachingTester();
        Executors.newFixedThreadPool(500).execute(() -> {
            while (true) {
                cachingTester.fromDB(SEC_KEY);
            }
        });

        Executors.newFixedThreadPool(500).execute(() -> {
            while (true) {
                cachingTester.fromCache(SEC_KEY);
            }
        });

        while (true) {
            Thread.sleep(Integer.MAX_VALUE);
        }
    }

    private Optional<ObjectDTO> fromCache(String key) {
        Gauge.Timer timer = getObjFromCacheGaugeTimer.labels("get_object_cache").startTimer();
        byte[] fromCache = jedis.get(key.getBytes());
        if (null != fromCache) {
            try {
                return Optional.of(ObjectDTO.fromBytes(fromCache));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Optional<ObjectDTO> fromDB = fromDB(key);
            if ( fromDB.isPresent()) {
                jedis.set(key.getBytes(), fromDB.get().toBytes());
                return fromDB;
            }
        }
        timer.setDuration();
        return Optional.empty();
    }

    private Optional<ObjectDTO> fromDB(String key) {
        Gauge.Timer timer = getObjFromDBGaugeTimer.labels("get_object_db").startTimer();
        Optional<ObjectDTO> fromDB = objectRepository.getFullObject(key);
        timer.setDuration();
        return fromDB;
    }
}
