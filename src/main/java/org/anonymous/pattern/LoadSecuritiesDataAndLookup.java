package org.anonymous.pattern;

import java.util.Properties;
import org.anonymous.connection.ConnectionProvider;
import java.sql.Connection;
import java.sql.SQLException;
import org.anonymous.module.SecurityRepository;
import org.anonymous.util.StopWatch;
import org.anonymous.util.TimeKeeper;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;
import java.util.List;

public class LoadSecuritiesDataAndLookup {

    private ConnectionProvider roConnectionProvider;
    private ConnectionProvider rwConnectionProvider;
    private SecurityRepository securityRepositiory;

    public LoadSecuritiesDataAndLookup(ConnectionProvider roConnectionProvider,
            ConnectionProvider rwConnectionProvider) {
        this.roConnectionProvider = roConnectionProvider;
        this.rwConnectionProvider = rwConnectionProvider;
        this.securityRepositiory = new SecurityRepository(roConnectionProvider, rwConnectionProvider);
    }

    public void loadSecurityAndLookup(int numberOfSec, int numberOfLookupOps, int lookupLimit) throws Exception {
        TimeKeeper timekeeper = new TimeKeeper();
        securityRepositiory.load(numberOfSec, 10, timekeeper).join();
        log("insert", timekeeper);

        timekeeper = new TimeKeeper();
        long count = securityRepositiory.countRecs(timekeeper).get();
        System.out.println("no. of sec. records = " + count);
        log("countRecs", timekeeper);

        timekeeper = new TimeKeeper();
        List<String> secKeys = securityRepositiory.randomLookUpByPrefix(numberOfLookupOps, lookupLimit, timekeeper)
                .get();
        System.out.println("no. of sec. matched = " + secKeys.size());
        log("lookup/single-thread", timekeeper);

        timekeeper = new TimeKeeper();
        secKeys = securityRepositiory.randomLookUpByPrefixMultiThread(numberOfLookupOps, lookupLimit, timekeeper)
                .map(CompletableFuture::join).flatMap(list -> list.stream()).collect(Collectors.toList());
        System.out.println("no. of sec. matched = " + secKeys.size());
        log("lookup/multi-thread", timekeeper);

        timekeeper = new TimeKeeper();
        securityRepositiory.getSDBByKey(secKeys.get(0), timekeeper).join();
        log("getSDBByKeys", timekeeper);

        timekeeper = new TimeKeeper();
        securityRepositiory.getManySDBByKeys(secKeys, timekeeper).join();
        log("getManySDBByKeys", timekeeper);

        timekeeper = new TimeKeeper();
        securityRepositiory.getSDBMemByKey(secKeys.get(0), timekeeper).join();
        log("getSDBMemByKey", timekeeper);

        timekeeper = new TimeKeeper();
        securityRepositiory.getSDBMemsByKeys(secKeys, timekeeper).join();
        log("getSDBMemsByKeys", timekeeper);
    }

    private void log(String op, TimeKeeper timekeeper) {
        System.out.println("Average Time Per '" + op + "' = " + TimeKeeper.humanReadableFormat(timekeeper.avg()));
        System.out.println("Peak Time Per '" + op + "' = " + TimeKeeper.humanReadableFormat(timekeeper.peak()));
        System.out.println("Floor Time Per '" + op + "' = " + TimeKeeper.humanReadableFormat(timekeeper.floor()));
        System.out.println("Total Time Spent for " + timekeeper.ops() + " '" + op + "'(s) = "
                + TimeKeeper.humanReadableFormat(timekeeper.lifetime()));
        System.out.println("====================================================================");
    }

    private void randomLookUpByPrefix() {

    }

    public void close() {
        securityRepositiory.close();
    }

}