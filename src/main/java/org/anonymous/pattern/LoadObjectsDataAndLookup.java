package org.anonymous.pattern;

import org.anonymous.connection.ConnectionProvider;
import org.anonymous.connection.HikariCPConnectionProvider;
import org.anonymous.module.ObjectRepository;
import org.anonymous.util.TimeKeeper;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.anonymous.stats.Statistics.log;

public class LoadObjectsDataAndLookup {

    private ConnectionProvider roConnectionProvider;
    private ConnectionProvider rwConnectionProvider;
    private ObjectRepository objectRepositiory;

    public LoadObjectsDataAndLookup(ConnectionProvider roConnectionProvider, ConnectionProvider rwConnectionProvider) {
        this.roConnectionProvider = roConnectionProvider;
        this.rwConnectionProvider = rwConnectionProvider;
        this.objectRepositiory = new ObjectRepository(roConnectionProvider, rwConnectionProvider);
    }

    public void loadObjectsAndLookup(int numberOfSec, int numberOfLookupOps, int lookupLimit) throws Exception {
        TimeKeeper timekeeper = new TimeKeeper("insert", false);
        objectRepositiory.load(numberOfSec, 10, 32000, timekeeper).join();
        log(timekeeper);

        timekeeper = new TimeKeeper("countRecs", false);
        long count = objectRepositiory.countRecs(timekeeper).get();
        System.out.println("no. of sec. records = " + count);
        log(timekeeper);

        timekeeper = new TimeKeeper("lookup/single-thread", false);
        List<String> secKeys = objectRepositiory.randomLookUpByPrefix(numberOfLookupOps, lookupLimit, timekeeper).get();
        System.out.println("no. of sec. matched = " + secKeys.size());
        log(timekeeper);

        timekeeper = new TimeKeeper("lookup/multi-thread", false);
        secKeys = objectRepositiory.randomLookUpByPrefixMultiThread(numberOfLookupOps, lookupLimit, timekeeper)
                .map(CompletableFuture::join).flatMap(list -> list.stream()).collect(Collectors.toList());
        System.out.println("no. of sec. matched = " + secKeys.size());
        log(timekeeper);

        timekeeper = new TimeKeeper("getSDBByKeys", false);
        objectRepositiory.getSDBByKey(secKeys.get(0), timekeeper).join();
        log(timekeeper);

        timekeeper = new TimeKeeper("getManySDBByKeys", false);
        objectRepositiory.getManySDBByKeys(secKeys, timekeeper).join();
        log(timekeeper);

        timekeeper = new TimeKeeper("getSDBMemByKey", false);
        objectRepositiory.getSDBMemByKey(secKeys.get(0), timekeeper).join();
        log(timekeeper);

        timekeeper = new TimeKeeper("getSDBMemsByKeys", false);
        objectRepositiory.getSDBMemsByKeys(secKeys, timekeeper).join();
        log(timekeeper);
    }


    private void randomLookUpByPrefix() {

    }

    public void close() {
        objectRepositiory.close();
    }

}