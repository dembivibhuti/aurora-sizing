package org.anonymous.stats;

import org.anonymous.util.TimeKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Statistics {

    private static Logger LOGGER = LoggerFactory.getLogger(Statistics.class);

    public static TimeKeeper connect = new TimeKeeper("connect");
    public static TimeKeeper lookupByName = new TimeKeeper("lookupByName");
    public static TimeKeeper lookupByNameStream = new TimeKeeper("lookupByNameStream");
    public static TimeKeeper lookupByType = new TimeKeeper("lookupByType");
    public static TimeKeeper lookupByTypeStream = new TimeKeeper("lookupByTypeStream");
    public static TimeKeeper getObject = new TimeKeeper("getObject");
    public static TimeKeeper getObjectManyByName = new TimeKeeper("getObjectManyByName");
    public static TimeKeeper getObjectManyByNameStream = new TimeKeeper("getObjectManyByNameStream");
    public static TimeKeeper getObjectManyByNameExt = new TimeKeeper("getObjectManyByNameExt");
    public static TimeKeeper getObjectManyByNameExtStream = new TimeKeeper("getObjectManyByNameExtStream");
    public static TimeKeeper getObjectExt = new TimeKeeper("getObjectExt");

    public static void log(
            TimeKeeper timekeeper) {
        TimeKeeper.Result result = timekeeper.getStats();
        if(result.opsCount > 0 ) {
            LOGGER.info("Average Time Per '" + timekeeper.getOp() + "' = " + ( (double)result.avgDuration.getNano() / 1000000 ));
            LOGGER.info("Peak Time Per '" + timekeeper.getOp() + "' = " + ( (double)result.peak.getNano() / 1000000 ));
            LOGGER.info("Floor Time Per '" + timekeeper.getOp() + "' = " + ( (double)result.floor.getNano() / 1000000 ));
            LOGGER.info("Total Number of Ops " + result.opsCount);
            LOGGER.info("** Time is in millis ====================================================================");
        }
    }

    static {
        new Thread(() -> {

            while (true) {

                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    LOGGER.error("", e);
                }
                log(connect);
                log(lookupByName);
                log(lookupByNameStream);
                log(lookupByType);
                log(lookupByTypeStream);
                log(getObject);
                log(getObjectManyByName);
                log(getObjectManyByNameStream);
                log(getObjectManyByNameExt);
                log(getObjectManyByNameExtStream);
                log(getObjectExt);
            }
        }).start();
    }
}
