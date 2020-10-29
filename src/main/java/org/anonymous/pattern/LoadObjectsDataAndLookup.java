package org.anonymous.pattern;

import java.util.Properties;
import org.anonymous.connection.ConnectionProvider;
import java.sql.Connection;
import java.sql.SQLException;
import org.anonymous.module.ObjectRepository;
import org.anonymous.util.StopWatch;
import org.anonymous.util.TimeKeeper;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;
import java.util.List;

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
        TimeKeeper timekeeper = new TimeKeeper("insert");
        objectRepositiory.load(numberOfSec, 10, 32000, timekeeper).join();
        log(timekeeper);

        timekeeper = new TimeKeeper("countRecs");
        long count = objectRepositiory.countRecs(timekeeper).get();
        System.out.println("no. of sec. records = " + count);
        log(timekeeper);

        timekeeper = new TimeKeeper("lookup/single-thread");
        List<String> secKeys = objectRepositiory.randomLookUpByPrefix(numberOfLookupOps, lookupLimit, timekeeper).get();
        System.out.println("no. of sec. matched = " + secKeys.size());
        log(timekeeper);

        timekeeper = new TimeKeeper("lookup/multi-thread");
        secKeys = objectRepositiory.randomLookUpByPrefixMultiThread(numberOfLookupOps, lookupLimit, timekeeper)
                .map(CompletableFuture::join).flatMap(list -> list.stream()).collect(Collectors.toList());
        System.out.println("no. of sec. matched = " + secKeys.size());
        log(timekeeper);

        timekeeper = new TimeKeeper("getSDBByKeys");
        objectRepositiory.getSDBByKey(secKeys.get(0), timekeeper).join();
        log(timekeeper);

        timekeeper = new TimeKeeper("getManySDBByKeys");
        objectRepositiory.getManySDBByKeys(secKeys, timekeeper).join();
        log(timekeeper);

        timekeeper = new TimeKeeper("getSDBMemByKey");
        objectRepositiory.getSDBMemByKey(secKeys.get(0), timekeeper).join();
        log(timekeeper);

        timekeeper = new TimeKeeper("getSDBMemsByKeys");
        objectRepositiory.getSDBMemsByKeys(secKeys, timekeeper).join();
        log(timekeeper);
    }



    private void randomLookUpByPrefix() {

    }

    public void close() {
        objectRepositiory.close();
    }

}