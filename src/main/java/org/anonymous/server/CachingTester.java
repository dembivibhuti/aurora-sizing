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
                if(!SEC_KEY.equals(cachingTester.fromDB(SEC_KEY).get().name)){
                    System.out.println("error from db");
                }
            }
        });

        Executors.newFixedThreadPool(500).execute(() -> {
            while (true) {
                if(!SEC_KEY.equals(cachingTester.fromCache(SEC_KEY).get().name)){
                    System.out.println("error from cache");
                }
            }
        });

        while (true) {
            Thread.sleep(Integer.MAX_VALUE);
        }
    }

    private Optional<ObjectDTO> fromCache(String key) {
        Gauge.Timer timer = getObjFromCacheGaugeTimer.labels("get_object_cache").startTimer();
        byte[] fromCache = jedis.get(key.getBytes());
        Optional<ObjectDTO> result = Optional.empty();
        if (null != fromCache) {
            try {
                result = Optional.of(ObjectDTO.fromBytes(fromCache));
                timer.setDuration();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            result = fromDB(key);
            if (result.isPresent()) {
                jedis.set(key.getBytes(), result.get().toBytes());
            }
        }
        return result;
    }

    private Optional<ObjectDTO> fromDB(String key) {
        Gauge.Timer timer = getObjFromDBGaugeTimer.labels("get_object_db").startTimer();
        Optional<ObjectDTO> fromDB = objectRepository.getFullObject(key);
        timer.setDuration();
        return fromDB;
    }
}
