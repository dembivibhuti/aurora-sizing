package org.anonymous.module;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import org.anonymous.domain.ObjectDTO;
import org.anonymous.grpc.CmdGetByNameExtResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
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
    //    private final JedisPool replicaPool;
    private final JedisPool primaryPool;
    private final ThreadLocal<Jedis> jedisConnection;


    public CachedObjectRepository(ObjectRepository objectRepository) {
        this.delegate = objectRepository;
        // max pool size and end point externalize
        // src/redis-cli -c -h aurora-sizing.uga7qd.ng.0001.use1.cache.amazonaws.com -p 6379

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(MAX_TOTAL);
        poolConfig.setMaxWaitMillis(Integer.MAX_VALUE);
        this.primaryPool = new JedisPool(poolConfig, System.getProperty("redis.pri"));
        //this.replicaPool = new JedisPool(poolConfig, System.getProperty("redis.rep"));
        jedisConnection = ThreadLocal.withInitial(() -> {
            jedisConns.labels("conn count").inc();
            return primaryPool.getResource();
        });
    }

    @Override
    public void close() throws Exception {
        primaryPool.close();
        //replicaPool.close();
    }

    public List<String> lookup(String prefix, int typeid, int limit) {
        return delegate.lookup(prefix, typeid, limit);
    }

    public Optional<CmdGetByNameExtResponse.MsgOnSuccess> getFullObject(String key) {
        Optional<ObjectDTO> objectDTO = null;
        Gauge.Timer cacheTimer = getObjFromCacheGaugeTimer.labels("get_object_cache").startTimer();
        byte[] fromCache = jedisConnection.get().get(key.getBytes());
        if (null != fromCache) {
            try {
                objectDTO = Optional.of(ObjectDTO.fromBytes(fromCache));
                cacheTimer.setDuration();
                cacheOps.labels("get_object_cache").inc();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            cacheTimer.close();
            Gauge.Timer dbTimer = getObjFromDBGaugeTimer.labels("get_object_db").startTimer();
            objectDTO = delegate.getFullObject(key);
            if (objectDTO.isPresent()) {
                dbTimer.setDuration();
                jedisConnection.get().set(key.getBytes(), objectDTO.get().toBytes());
                dbOps.labels("get_object_db").inc();
            } else {
                dbTimer.close();
            }
        }
        return Optional.ofNullable(objectDTO.get().toCmdGetByNameExtResponseMsgOnSuccess());
    }
}
