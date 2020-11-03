package org.anonymous.stats;

import org.anonymous.util.TimeKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Statistics {

    private static Logger LOGGER = LoggerFactory.getLogger(Statistics.class);

    private static final boolean logToFile = Boolean.parseBoolean(System.getProperty("metricsFileDump"));

    public static TimeKeeper connect = new TimeKeeper("connect", logToFile);
    public static TimeKeeper lookupByName = new TimeKeeper("lookupByName", logToFile);
    public static TimeKeeper lookupByNameStream = new TimeKeeper("lookupByNameStream", logToFile );
    public static TimeKeeper lookupByType = new TimeKeeper("lookupByType", logToFile );
    public static TimeKeeper lookupByTypeStream = new TimeKeeper("lookupByTypeStream", logToFile );

    public static TimeKeeper getObject = new TimeKeeper("getObject", logToFile );
    public static TimeKeeper getObjectDBGetConnection = new TimeKeeper("getObjectDBGetConnection", logToFile );
    public static TimeKeeper getObjectDBPreparedStatementMake = new TimeKeeper("getObjectDBPreparedStatementMake", logToFile );
    public static TimeKeeper getObjectDBResultSetFetch = new TimeKeeper("getObjectDBResultSetFetch", logToFile );
    public static TimeKeeper getObjectDBPreparedCloseResource = new TimeKeeper("getObjectDBPreparedCloseResource", logToFile );

    
    public static TimeKeeper getObjectManyByName = new TimeKeeper("getObjectManyByName", logToFile );
    public static TimeKeeper getObjectManyByNameStream = new TimeKeeper("getObjectManyByNameStream", logToFile );
    public static TimeKeeper getObjectManyByNameExt = new TimeKeeper("getObjectManyByNameExt", logToFile );
    public static TimeKeeper getObjectManyByNameExtStream = new TimeKeeper("getObjectManyByNameExtStream", logToFile );
    public static TimeKeeper getObjectExt = new TimeKeeper("getObjectExt", logToFile );

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

    public static final int INTERVAL = 20;

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
                    log(getObjectDBGetConnection);
                    log(getObjectDBPreparedStatementMake);
                    log(getObjectDBResultSetFetch);
                    log(getObjectDBPreparedCloseResource);

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
