package org.anonymous.stats;

import com.opencsv.CSVWriter;
import com.zaxxer.hikari.HikariPoolMXBean;
import org.anonymous.util.TimeKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;

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
    private static HikariPoolMXBean rwPoolProxy;
    private static HikariPoolMXBean roPoolProxy;

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
        writer.flushQuietly();

        try {
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName rwPoolName = new ObjectName("com.zaxxer.hikari:type=Pool (rwPool)");
            ObjectName roPoolName = new ObjectName("com.zaxxer.hikari:type=Pool (roPool)");

            rwPoolProxy = JMX.newMXBeanProxy(mBeanServer, rwPoolName, HikariPoolMXBean.class);
            roPoolProxy = JMX.newMXBeanProxy(mBeanServer, roPoolName, HikariPoolMXBean.class);
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        }

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
            writer.flushQuietly();
        }
    }

    public static final int INTERVAL = 20;

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


                    LOGGER.info("");

                    LOGGER.info("RW Connection Pool Stats ");
                    LOGGER.info("Total Connections = " + rwPoolProxy.getTotalConnections());
                    LOGGER.info("Idle Connections = " + rwPoolProxy.getIdleConnections());
                    LOGGER.info("Active Connections = " + rwPoolProxy.getActiveConnections());
                    LOGGER.info("Threads Awaiting Connections = " + rwPoolProxy.getThreadsAwaitingConnection());

                    LOGGER.info("");

                    LOGGER.info("RO Connection Pool Stats ");
                    LOGGER.info("Total Connections = " + roPoolProxy.getTotalConnections());
                    LOGGER.info("Idle Connections = " + roPoolProxy.getIdleConnections());
                    LOGGER.info("Active Connections = " + roPoolProxy.getActiveConnections());
                    LOGGER.info("Threads Awaiting Connections = " + roPoolProxy.getThreadsAwaitingConnection());



                } catch (Throwable e) {
                    //LOGGER.error("", e);
                }
            }
        }).start();
    }
}
