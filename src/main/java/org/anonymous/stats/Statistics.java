package org.anonymous.stats;

import org.anonymous.server.GrpcServer;
import org.anonymous.util.TimeKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Statistics {

    private static Logger LOGGER = LoggerFactory.getLogger(Statistics.class);

    public static void log(
            TimeKeeper timekeeper) {
        LOGGER.info("Average Time Per '" + timekeeper.getOp() + "' = " + TimeKeeper.humanReadableFormat(timekeeper.avg()));
        LOGGER.info("Peak Time Per '" + timekeeper.getOp() + "' = " + TimeKeeper.humanReadableFormat(timekeeper.peak()));
        LOGGER.info("Floor Time Per '" + timekeeper.getOp() + "' = " + TimeKeeper.humanReadableFormat(timekeeper.floor()));
        LOGGER.info("Total Time Spent for " + timekeeper.ops() + " '" + timekeeper.getOp() + "'(s) = "
                + TimeKeeper.humanReadableFormat(timekeeper.lifetime()));
        LOGGER.info("====================================================================");
    }
}
