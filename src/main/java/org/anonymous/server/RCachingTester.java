package org.anonymous.server;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import org.anonymous.connection.ConnectionProviderHolder;
import org.anonymous.connection.HikariCPConnectionProvider;
import org.anonymous.domain.ObjectDTO;
import org.anonymous.module.ObjectRepository;
import org.redisson.Redisson;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RCachingTester {

    private static final ConnectionProviderHolder connectionProviderHolder = HikariCPConnectionProvider.create();
    public static final String AURORA_SIZING_UGA7QD_NG_0001_USE1_CACHE_AMAZONAWS_COM = "aurora-sizing.uga7qd.ng.0001.use1.cache.amazonaws.com";
    private static RedissonClient redisson;
    private static RLocalCachedMap<String, ObjectDTO> objMap;
    public static final int MAX_TOTAL = 72;

    private static final Thread shutdownHook = new Thread(() -> {
        connectionProviderHolder.close();
        redisson.shutdown();
    });
    private static final ObjectRepository objectRepository = new ObjectRepository(connectionProviderHolder.roConnectionProvider, connectionProviderHolder.rwConnectionProvider);
    private static final Gauge getObjFromCacheGaugeTimer = Gauge.build().name("get_object_cache").help("Get Object on Middleware from cache").labelNames("redis").register();
    private static final Gauge getObjFromDBGaugeTimer = Gauge.build().name("get_object_db").help("Get Object on Middleware from db").labelNames("db").register();
    private static final Counter cacheOps = Counter.build().name("get_object_cache_count").help("Count of GetObject from Cache").labelNames("redis").register();
    private static final Counter dbOps = Counter.build().name("get_object_db_count").help("Count of GetObject from DB").labelNames("db").register();
    public static final String SEC_KEY = "232574-46439-1-1326302";



    public static ExecutorService cacheService = null;
    public static ExecutorService dbService = null;

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(shutdownHook);
        GrpcServer.startMetricsServer();
        RCachingTester cachingTester = new RCachingTester();

        Config config = new Config();
        config.setTransportMode(TransportMode.EPOLL).setNettyThreads(0);
        config.useReplicatedServers()
                .addNodeAddress("redis://aurora-sizing-001.uga7qd.0001.use1.cache.amazonaws.com:6379")
                .addNodeAddress("redis://rep-1.uga7qd.0001.use1.cache.amazonaws.com:6379")
                .addNodeAddress("redis://rep-2.uga7qd.0001.use1.cache.amazonaws.com:6379")
                .addNodeAddress("redis://rep-3.uga7qd.0001.use1.cache.amazonaws.com:6379")
                .addNodeAddress("redis://rep-4.uga7qd.0001.use1.cache.amazonaws.com:6379");

        redisson = Redisson.create(config);
        objMap = redisson.getLocalCachedMap("objMap", LocalCachedMapOptions.defaults());

        boolean useCache = "true".equalsIgnoreCase(System.getProperty("testCache"));
        if (useCache) {
            System.out.println("start test with cache");
            cacheService = Executors.newFixedThreadPool(MAX_TOTAL);
            int jobCount = MAX_TOTAL;
            int counter = 0;


            while (counter < jobCount) {
                cacheService.execute(() -> {
                    while (true) {
                        if (!SEC_KEY.equals(cachingTester.fromCache(SEC_KEY).get().name)) {
                            System.out.println("error from cache");
                        }
                    }
                });
                counter++;
            }
        } else {
            System.out.println("start test with db");
            dbService = Executors.newFixedThreadPool(MAX_TOTAL);
            int jobCount = MAX_TOTAL;
            int counter = 0;
            while (counter < jobCount) {
                dbService.execute(() -> {
                    while (true) {
                        if (!SEC_KEY.equals(cachingTester.fromDB(SEC_KEY).get().name)) {
                            System.out.println("error from db");
                        }
                    }
                });
                counter++;
            }
        }
    }

    private Optional<ObjectDTO> fromCache(String key) {
        Gauge.Timer timer = getObjFromCacheGaugeTimer.labels("get_object_cache").startTimer();
        ObjectDTO res = objMap.get(key);
        Optional<ObjectDTO> result = Optional.empty();
        if (null != res) {
            timer.setDuration();
            cacheOps.labels("get_object_cache").inc();
        } else {
            result = fromDB(key);
            if (result.isPresent()) {
                objMap.fastPut(key, result.get());
            }
        }
        timer.close();
        return result;
    }

    private Optional<ObjectDTO> fromDB(String key) {
        Gauge.Timer timer = getObjFromDBGaugeTimer.labels("get_object_db").startTimer();
        Optional<ObjectDTO> fromDB = objectRepository.getFullObject(key);
        timer.setDuration();
        dbOps.labels("get_object_db").inc();
        return fromDB;
    }
}
