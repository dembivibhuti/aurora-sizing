package org.anonymous.module;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import org.anonymous.domain.IndexRecDTO;
import org.anonymous.domain.ObjectDTO;
import org.anonymous.grpc.CmdGetByNameExtResponse;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import static redis.clients.jedis.ScanParams.SCAN_POINTER_START;

public class NearCachedObjectRepository implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(NearCachedObjectRepository.class);

    private static final Gauge getObjFromCacheGaugeTimer = Gauge.build().name("get_object_cache").help("Get Object on Middleware from cache").labelNames("redis").register();
    private static final Gauge getObjFromDBGaugeTimer = Gauge.build().name("get_object_db").help("Get Object on Middleware from db").labelNames("db").register();
    private static final Gauge getObjFromNearCacheGaugeTimer = Gauge.build().name("get_object_near_cache").help("Get Object on Middleware from near cache").labelNames("ehcache").register();
    private static final Gauge setObjToCacheGaugeTimer = Gauge.build().name("set_object_cache").help("Set Object to cache").labelNames("cache").register();
    private static final Gauge setObjToNearCacheGaugeTimer = Gauge.build().name("set_object_near_cache").help("Set Object to near cache").labelNames("ehcache").register();
    private static final Counter cacheOps = Counter.build().name("get_object_cache_count").help("Count of GetObject from Cache").labelNames("redis").register();
    private static final Counter nearCacheOps = Counter.build().name("get_object_near_cache_count").help("Count of GetObject from Near Cache").labelNames("ehcache").register();
    private static final Counter dbOps = Counter.build().name("get_object_db_count").help("Count of GetObject from DB").labelNames("db").register();
    private static final Counter jedisConns = Counter.build().name("redis_connection_count").help("Count of Redis Connections").labelNames("redis").register();
    public static final int MAX_TOTAL = 5000;
    public static final String OBJ_MAP = "objMap";
    public static final String IDX_MAP = "idxMap";

    private final ObjectRepository delegate;
    private final JedisPool replicaPool;
    private final JedisPool primaryPool;
    private final ThreadLocal<Jedis> jedisROConnection;
    private final ThreadLocal<Jedis> jedisRWConnection;
    private final Cache<String, ObjectDTO> objMapCache;
    /*private final Cache<String, Map> indexMapCache;*/

    /*private static RedissonClient redisson;
    private static RLocalCachedMap<String, ObjectDTO> objMap;*/

    public NearCachedObjectRepository(ObjectRepository objectRepository) {
        this.delegate = objectRepository;
        // max pool size and end point externalize
        // src/redis-cli -c -h aurora-sizing.uga7qd.ng.0001.use1.cache.amazonaws.com -p 6379

        JedisPoolConfig poolConfig = new JedisPoolConfig();
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
        });

        /*Config config = new Config();
        config.setTransportMode(TransportMode.EPOLL).setNettyThreads(500);
        config.useReplicatedServers()
                .addNodeAddress("redis://aurora-sizing-001.uga7qd.0001.use1.cache.amazonaws.com:6379")
                .addNodeAddress("redis://rep-1.uga7qd.0001.use1.cache.amazonaws.com:6379")
                .addNodeAddress("redis://rep-2.uga7qd.0001.use1.cache.amazonaws.com:6379")
                .addNodeAddress("redis://rep-3.uga7qd.0001.use1.cache.amazonaws.com:6379")
                .addNodeAddress("redis://rep-4.uga7qd.0001.use1.cache.amazonaws.com:6379")
                .setSlaveConnectionPoolSize(1000)
                .setMasterConnectionPoolSize(1000);

        redisson = Redisson.create(config);
        objMap = redisson.getLocalCachedMap(OBJ_MAP, LocalCachedMapOptions.defaults());*/


        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache(OBJ_MAP,
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, ObjectDTO.class, ResourcePoolsBuilder.newResourcePoolsBuilder()
                                .heap(10, MemoryUnit.GB)
                                .offheap(30, MemoryUnit.GB)))
                /*.withCache(IDX_MAP, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, ObjectDTO.class, ResourcePoolsBuilder.newResourcePoolsBuilder()
                        .heap(10, MemoryUnit.GB)
                        .offheap(30, MemoryUnit.GB)))*/
                .build();
        cacheManager.init();
        objMapCache = cacheManager.getCache(OBJ_MAP, String.class, ObjectDTO.class);
        /*indexMapCache = cacheManager.getCache(IDX_MAP, String.class, Map.class);*/
        warmupNearCache();
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

        ObjectDTO fromNearCache = getFromNearCache(key);
        if( null != fromNearCache ) {
            objectDTO = Optional.of(fromNearCache);
        } else {
            ObjectDTO fromCache = getFromRedis(key);
            if (null != fromCache) {
                objectDTO = Optional.of(fromCache);
                setToNearCache(key, fromCache);
            } else {
                Gauge.Timer dbTimer = getObjFromDBGaugeTimer.labels("get_object_db").startTimer();
                objectDTO = delegate.getFullObject(key);
                if (objectDTO.isPresent()) {
                    dbTimer.setDuration();
                    setToRedis(key, objectDTO.get());
                    setToNearCache(key, objectDTO.get());
                    dbOps.labels("get_object_db").inc();
                }
            }
        }
        return Optional.ofNullable(objectDTO.get().toCmdGetByNameExtResponseMsgOnSuccess());
    }

    private void setToRedis(String key, ObjectDTO objectDTO) {
        Gauge.Timer setCacheTimer = setObjToCacheGaugeTimer.labels("set_object_cache").startTimer();
        try {
            jedisRWConnection.get().hsetnx(OBJ_MAP.getBytes(), key.getBytes(), objectDTO.toBytes());
        } catch (Throwable th) {
            LOGGER.error("unexpected err in Redis set ", th);
        }
        setCacheTimer.setDuration();
    }

    private ObjectDTO getFromRedis(String key) {
        Gauge.Timer cacheTimer = getObjFromCacheGaugeTimer.labels("get_object_cache").startTimer();
        ObjectDTO objectDTO = null;
        try {
            byte[] bytes = jedisROConnection.get().hget(OBJ_MAP.getBytes(), key.getBytes());
            if ( null != bytes ) {
                objectDTO = ObjectDTO.fromBytes(bytes);
                cacheTimer.setDuration();
                cacheOps.labels("get_object_cache").inc();
            }
        } catch (Throwable th) {
            LOGGER.error("unexpected err in Redis get ", th);
        }
        return objectDTO;
    }

    private ObjectDTO getFromNearCache(String key) {
        Gauge.Timer getNearCacheTimer = getObjFromNearCacheGaugeTimer.labels("get_object_near_cache").startTimer();
        ObjectDTO objectDTO = objMapCache.get(key);
        if(null != objectDTO ) {
            getNearCacheTimer.setDuration();
            nearCacheOps.labels("get_object_near_cache").inc();
        }
        return objectDTO;
    }

    private void setToNearCache(String key, ObjectDTO objectDTO) {
        Gauge.Timer setCacheTimer = setObjToNearCacheGaugeTimer.labels("set_object_near_cache").startTimer();
        objMapCache.put(key, objectDTO);
        setCacheTimer.setDuration();
    }

    private void warmupNearCache() {

        String[] indexes = {"Table_BBI", "Table_BG", "Table_PB", "Table_PNT", "Table_TETID", "Table_TMID", "Table_TST",
                "Table_TT", "Table_EBBI", "Table_MIMID"};
        LOGGER.info("Populating Index Data to Far Cache");
        int batchSize = 10000;

        Arrays.asList(indexes).parallelStream().forEach(index -> {
            int offset = 0;
            while(true) {
                List<IndexRecDTO> recs = delegate.getIndexRecordMany("", index, batchSize, offset);
                recs.parallelStream().forEach(indexRecDTO ->
                        jedisRWConnection.get().hset(index.getBytes(), indexRecDTO.objKey.getBytes(), indexRecDTO.toBytes()));
                if (recs.size() < batchSize ) {
                    break;
                }
                offset += batchSize;
                System.out.print(" Inserted " + batchSize + " Index Records for " + index + " to Far Cache \r");
            }
            LOGGER.info(" Completed Index Data for " + index + " to Far Cache");
        });
        LOGGER.info("All Index Data Populated to Far Cache");

        new Thread(() -> {
            long start = System.currentTimeMillis();
            int chunkSize = 100000;
            ExecutorService warmupService = Executors.newFixedThreadPool(32);

            long recCnt = delegate.countRecs();
            AtomicLong remainingRecCCnt = new AtomicLong(recCnt);
            LOGGER.info("Cache Warm Up | Target Record Count = {}", recCnt);
            long chunkCount = ( recCnt / chunkSize ) + 1;
            for(int i = 1; i <= chunkCount ; i++) {
                List<String> keys = delegate.getObjKeys(chunkSize, i * chunkSize);
                warmupService.execute(() -> {
                    keys.stream().forEach(key -> getFullObject(key));
                    long remainingCount = remainingRecCCnt.addAndGet(keys.size() * -1 );
                    System.out.print("Cache Warm Up | Remaining Keys " + remainingCount +  "(" + (((double)recCnt - (double)remainingCount) / (double)recCnt ) * 100 + "%)" + " Time = " + (System.currentTimeMillis() - start ) / (1000 * 60 )+ " mins \r");
                });
            }
        }).start();
    }

    public List<IndexRecDTO> getIndexRecordMany(String recordName, String tableName) {
        List<IndexRecDTO> ans = new ArrayList<>();
        ScanParams scanParams = new ScanParams().count(10).match(recordName + "*");
        String cur = SCAN_POINTER_START;
        do {
            ScanResult<Map.Entry<String, String>> scanResult = jedisROConnection.get().hscan(tableName, cur, scanParams);

            scanResult.getResult().stream().forEach(stringStringEntry -> {
                IndexRecDTO indexRecDTO = IndexRecDTO.fromBytes(stringStringEntry.getValue().getBytes());
                ans.add(indexRecDTO);
            });
            cur = scanResult.getCursor();
        } while (!cur.equals(SCAN_POINTER_START) && ans.size() < 100);

        return ans;
    }
}
