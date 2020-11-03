package org.anonymous.stats;

import com.opencsv.CSVWriter;
import com.sun.beans.editors.DoubleEditor;
import org.anonymous.util.TimeKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
    public static TimeKeeper getObjectDBCloseResource = new TimeKeeper("getObjectDBCloseResource", logToFile );

    public static TimeKeeper getObjectExt = new TimeKeeper("getObjectExt", logToFile );
    public static TimeKeeper getObjectExtDBGetConnection = new TimeKeeper("getObjectExtDBGetConnection", logToFile );
    public static TimeKeeper getObjectExtDBPreparedStatementMake = new TimeKeeper("getObjectExtDBPreparedStatementMake", logToFile );
    public static TimeKeeper getObjectExtDBResultSetFetch = new TimeKeeper("getObjectExtDBResultSetFetch", logToFile );
    public static TimeKeeper getObjectExtDBCloseResource = new TimeKeeper("getObjectExtDBCloseResource", logToFile );

    
    public static TimeKeeper getObjectManyByName = new TimeKeeper("getObjectManyByName", logToFile );
    public static TimeKeeper getObjectManyByNameStream = new TimeKeeper("getObjectManyByNameStream", logToFile );
    public static TimeKeeper getObjectManyByNameExt = new TimeKeeper("getObjectManyByNameExt", logToFile );
    public static TimeKeeper getObjectManyByNameExtStream = new TimeKeeper("getObjectManyByNameExtStream", logToFile );

    private static final CSVWriter writer;

    static {
        File statsDump = new File("stats-consolidated-dump.csv");
        FileWriter outputfile = null;
        try {
            outputfile = new FileWriter(statsDump);
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer = new CSVWriter(outputfile);
        String[] header = { "Op-Type", "Average-Time", "Peak-Time", "Floor-Time", "Total Ops", "Through Put ( ops / sec )" };
        writer.writeNext(header);
    }


    public static void log(
            TimeKeeper timekeeper) {
        TimeKeeper.Result result = timekeeper.getStats();
        if(result.opsCount > 0 ) {

            double avgTime = ( (double)result.avgDuration.getNano() / 1000000 );
            double peakTime = ( (double)result.peak.getNano() / 1000000 );
            double floorTime = ( (double)result.floor.getNano() / 1000000 );
            double throughput = (double)result.opsCount / INTERVAL;

            LOGGER.info("Average Time Per '" + timekeeper.getOp() + "' = " + avgTime);
            LOGGER.info("Peak Time Per '" + timekeeper.getOp() + "' = " + peakTime);
            LOGGER.info("Floor Time Per '" + timekeeper.getOp() + "' = " + floorTime );
            LOGGER.info("Total Number of Ops " + result.opsCount);
            LOGGER.info("Through Put ( ops / sec )  " +  throughput);
            LOGGER.info("** Time is in millis ====================================================================");


            String[] data = { timekeeper.getOp(), Double.toString(avgTime), Double.toString(peakTime),
                    Double.toString(floorTime), Long.toString(result.opsCount), Double.toString(throughput) };
            writer.writeNext(data);
        }
    }

    public static final int INTERVAL = 30;

    static {
        new Thread(() -> {

            while (true) {
                try {
                    Thread.sleep(INTERVAL * 1000 );

                    LOGGER.info("");
                    LOGGER.info("");


                    log(connect);
                    log(lookupByName);
                    log(lookupByNameStream);
                    log(lookupByType);
                    log(lookupByTypeStream);

                    log(getObject);
                    log(getObjectDBGetConnection);
                    log(getObjectDBPreparedStatementMake);
                    log(getObjectDBResultSetFetch);
                    log(getObjectDBCloseResource);

                    log(getObjectExt);
                    log(getObjectExtDBGetConnection);
                    log(getObjectExtDBPreparedStatementMake);
                    log(getObjectExtDBResultSetFetch);
                    log(getObjectExtDBCloseResource);

                    log(getObjectManyByName);
                    log(getObjectManyByNameStream);
                    log(getObjectManyByNameExt);
                    log(getObjectManyByNameExtStream);



                } catch (Throwable e) {
                    //LOGGER.error("", e);
                }
            }
        }).start();
    }
}
