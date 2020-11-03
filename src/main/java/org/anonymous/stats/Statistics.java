package org.anonymous.stats;

import org.anonymous.util.TimeKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Statistics {

    private static Logger LOGGER = LoggerFactory.getLogger(Statistics.class);


    public static TimeKeeper connect = new TimeKeeper("connect", true);
    public static TimeKeeper lookupByName = new TimeKeeper("lookupByName", true);
    public static TimeKeeper lookupByNameStream = new TimeKeeper("lookupByNameStream", true );
    public static TimeKeeper lookupByType = new TimeKeeper("lookupByType", true );
    public static TimeKeeper lookupByTypeStream = new TimeKeeper("lookupByTypeStream", true );
    public static TimeKeeper getObject = new TimeKeeper("getObject", true );
    public static TimeKeeper getObjectDB = new TimeKeeper("getObjectDB", true );
    public static TimeKeeper getObjectManyByName = new TimeKeeper("getObjectManyByName", true );
    public static TimeKeeper getObjectManyByNameStream = new TimeKeeper("getObjectManyByNameStream", true );
    public static TimeKeeper getObjectManyByNameExt = new TimeKeeper("getObjectManyByNameExt", true );
    public static TimeKeeper getObjectManyByNameExtStream = new TimeKeeper("getObjectManyByNameExtStream", true );
    public static TimeKeeper getObjectExt = new TimeKeeper("getObjectExt", true );

    public static void log(
            TimeKeeper timekeeper) {
        TimeKeeper.Result result = timekeeper.getStats();
        if(result.opsCount > 0 ) {
            LOGGER.info("Average Time Per '" + timekeeper.getOp() + "' = " + ( (double)result.avgDuration.getNano() / 1000000 ));
            LOGGER.info("Peak Time Per '" + timekeeper.getOp() + "' = " + ( (double)result.peak.getNano() / 1000000 ));
            LOGGER.info("Floor Time Per '" + timekeeper.getOp() + "' = " + ( (double)result.floor.getNano() / 1000000 ));
            LOGGER.info("Total Number of Ops " + result.opsCount);
            LOGGER.info("Through Put ( ops / sec )  " + (double)result.opsCount / INTERVAL );
            LOGGER.info("** Time is in millis ====================================================================");
        }
    }

    public static final int INTERVAL = 5;

    static {
        new Thread(() -> {

            while (true) {
                try {
                    Thread.sleep(INTERVAL * 1000 );
                    log(connect);
                    log(lookupByName);
                    log(lookupByNameStream);
                    log(lookupByType);
                    log(lookupByTypeStream);
                    log(getObject);
                    log(getObjectDB);
                    log(getObjectManyByName);
                    log(getObjectManyByNameStream);
                    log(getObjectManyByNameExt);
                    log(getObjectManyByNameExtStream);
                    log(getObjectExt);
                } catch (Throwable e) {
                    //LOGGER.error("", e);
                }
            }
        }).start();
    }
}
