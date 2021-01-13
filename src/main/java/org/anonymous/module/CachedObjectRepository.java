package org.anonymous.module;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import org.anonymous.domain.ObjectDTO;
import org.anonymous.grpc.CmdGetByNameExtResponse;
import org.redisson.Redisson;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class CachedObjectRepository implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CachedObjectRepository.class);

    private static final Gauge getObjFromCacheGaugeTimer = Gauge.build().name("get_object_cache").help("Get Object on Middleware from cache").labelNames("redis").register();
    private static final Gauge getObjFromDBGaugeTimer = Gauge.build().name("get_object_db").help("Get Object on Middleware from db").labelNames("db").register();
    private static final Counter cacheOps = Counter.build().name("get_object_cache_count").help("Count of GetObject from Cache").labelNames("redis").register();
    private static final Counter dbOps = Counter.build().name("get_object_db_count").help("Count of GetObject from DB").labelNames("db").register();
    private static final Counter jedisConns = Counter.build().name("redis_connection_count").help("Count of Redis Connections").labelNames("redis").register();
    public static final int MAX_TOTAL = 5000;

    private final ObjectRepository delegate;
    /*private final JedisPool replicaPool;
    private final JedisPool primaryPool;
    private final ThreadLocal<Jedis> jedisROConnection;
    private final ThreadLocal<Jedis> jedisRWConnection;*/

    private static RedissonClient redisson;
    private static RLocalCachedMap<String, ObjectDTO> objMap;

    public CachedObjectRepository(ObjectRepository objectRepository) {
        this.delegate = objectRepository;
        // max pool size and end point externalize
        // src/redis-cli -c -h aurora-sizing.uga7qd.ng.0001.use1.cache.amazonaws.com -p 6379

        /*JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(MAX_TOTAL);
        poolConfig.setMaxWaitMillis(Integer.MAX_VALUE);
        this.primaryPool = new JedisPool(poolConfig, System.getProperty("redis.pri"));
        this.replicaPool = new JedisPool(poolConfig, System.getProperty("redis.rep"));
        jedisROConnection = ThreadLocal.withInitial(() -> {
            jedisConns.labels("conn count").inc();
            return replicaPool.getResource();
        });
        jedisRWConnection = ThreadLocal.withInitial(() -> {
            jedisConns.labels("conn count").inc();
            return primaryPool.getResource();
        });*/

        Config config = new Config();
        config.setTransportMode(TransportMode.NIO).setNettyThreads(0);
        config.useReplicatedServers()
                .addNodeAddress("redis://aurora-sizing-001.uga7qd.0001.use1.cache.amazonaws.com:6379")
                .addNodeAddress("redis://rep-1.uga7qd.0001.use1.cache.amazonaws.com:6379")
                .addNodeAddress("redis://rep-2.uga7qd.0001.use1.cache.amazonaws.com:6379")
                .addNodeAddress("redis://rep-3.uga7qd.0001.use1.cache.amazonaws.com:6379")
                .addNodeAddress("redis://rep-4.uga7qd.0001.use1.cache.amazonaws.com:6379");

        redisson = Redisson.create(config);
        objMap = redisson.getLocalCachedMap("objMap", LocalCachedMapOptions.defaults());

    }

    @Override
    public void close() throws Exception {
        redisson.shutdown();
    }

    public List<String> lookup(String prefix, int typeid, int limit) {
        return delegate.lookup(prefix, typeid, limit);
    }

    public Optional<CmdGetByNameExtResponse.MsgOnSuccess> getFullObject(String key) {
        Optional<ObjectDTO> objectDTO = null;
        Gauge.Timer cacheTimer = getObjFromCacheGaugeTimer.labels("get_object_cache").startTimer();
        ObjectDTO fromCache = getFromRedis(key);
        if (null != fromCache) {
            objectDTO = Optional.of(fromCache);
            cacheTimer.setDuration();
            cacheOps.labels("get_object_cache").inc();
        } else {
            cacheTimer.close();
            Gauge.Timer dbTimer = getObjFromDBGaugeTimer.labels("get_object_db").startTimer();
            objectDTO = delegate.getFullObject(key);
            if (objectDTO.isPresent()) {
                dbTimer.setDuration();
                setToRedis(key, objectDTO);
                dbOps.labels("get_object_db").inc();
            } else {
                dbTimer.close();
            }
        }
        return Optional.ofNullable(objectDTO.get().toCmdGetByNameExtResponseMsgOnSuccess());
    }

    private void setToRedis(String key, Optional<ObjectDTO> objectDTO) {
        try {
            objMap.fastPut(key, objectDTO.get());
        } catch (Throwable th) {
            LOGGER.error("unexpected err in Redis set ", th);
        }
    }

    private ObjectDTO getFromRedis(String key) {
        ObjectDTO objectDTO = null;
        try {
            objectDTO = objMap.get(key);
        } catch (Throwable th) {
            LOGGER.error("unexpected err in Redis get ", th);
        }
        return objectDTO;
    }
}
