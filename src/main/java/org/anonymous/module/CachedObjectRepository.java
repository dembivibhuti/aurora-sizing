package org.anonymous.module;

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

    private final ObjectRepository delegate;
    private final JedisPool replicaPool;
    private final JedisPool primaryPool;

    public CachedObjectRepository(ObjectRepository objectRepository) {
        this.delegate = objectRepository;
        // max pool size and end point externalize
        // src/redis-cli -c -h aurora-sizing.uga7qd.ng.0001.use1.cache.amazonaws.com -p 6379

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10000);
        poolConfig.setMaxWaitMillis(Integer.MAX_VALUE);
        this.primaryPool = new JedisPool(poolConfig, System.getProperty("redis.pri"));
        this.replicaPool = new JedisPool(poolConfig, System.getProperty("redis.rep"));
    }

    @Override
    public void close() throws Exception {
        primaryPool.close();
        replicaPool.close();
    }

    public List<String> lookup(String prefix, int typeid, int limit) {
        return delegate.lookup(prefix, typeid, limit);
    }

    public Optional<CmdGetByNameExtResponse.MsgOnSuccess> getFullObject(String key) {
        Optional<ObjectDTO> objectDTO = null;
        try (Jedis jedis = replicaPool.getResource()) {
            byte[] fromCache = jedis.get(key.getBytes());
            if (null != fromCache) {
                objectDTO = Optional.of(ObjectDTO.fromBytes(fromCache));
            } else {
                objectDTO = delegate.getFullObject(key);
                if (objectDTO.isPresent()) {
                    try(Jedis primary = primaryPool.getResource()) {
                        primary.set(key.getBytes(), objectDTO.get().toBytes());
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("error in de-serializing the redis payload", e);
        }
        if (objectDTO.isPresent()) {
            return Optional.of(objectDTO.get().toCmdGetByNameExtResponseMsgOnSuccess());
        } else {
            LOGGER.error("failed to find Obj. by key from both sources {}", key);
        }
        return Optional.empty();
    }
}
