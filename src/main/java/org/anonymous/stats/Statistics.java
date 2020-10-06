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
        LOGGER.info("Average Time Per '" + timekeeper.getOp() + "' = " + TimeKeeper.humanReadableFormat(timekeeper.avg()));
        LOGGER.info("Peak Time Per '" + timekeeper.getOp() + "' = " + TimeKeeper.humanReadableFormat(timekeeper.peak()));
        LOGGER.info("Floor Time Per '" + timekeeper.getOp() + "' = " + TimeKeeper.humanReadableFormat(timekeeper.floor()));
        LOGGER.info("Total Time Spent for " + timekeeper.opsCount() + " '" + timekeeper.getOp() + "'(s) = "
                + TimeKeeper.humanReadableFormat(timekeeper.lifetime()));
        LOGGER.info("====================================================================");
    }

    static {
        new Thread(() -> {

            while (true) {

                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    LOGGER.error("", e);
                }
                connect.condense();
                log(connect);

                lookupByName.condense();
                log(lookupByName);

                lookupByNameStream.condense();
                log(lookupByNameStream);

                lookupByType.condense();
                log(lookupByType);

                lookupByTypeStream.condense();
                log(lookupByTypeStream);

                getObject.condense();
                log(getObject);

                getObjectManyByName.condense();
                log(getObjectManyByName);

                getObjectManyByNameStream.condense();
                log(getObjectManyByNameStream);

                getObjectManyByNameExt.condense();
                log(getObjectManyByNameExt);

                getObjectManyByNameExtStream.condense();
                log(getObjectManyByNameExtStream);

                getObjectExt.condense();
                log(getObjectExt);
            }


        }).start();
    }
}
