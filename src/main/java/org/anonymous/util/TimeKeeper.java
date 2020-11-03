package org.anonymous.util;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentHashMap;

public class TimeKeeper {

    private final String op;
    private Instant resetTime = Instant.now();

    private AtomicLong clicks = new AtomicLong(0);
    private Map<Long, Instant> starts = new ConcurrentHashMap<>();
    private Queue<Duration> durations = new ConcurrentLinkedDeque<>();

    private final boolean logToFile;
    private static final CSVWriter writer;

    static {
        File statsDump = new File("stats-dump.csv");
        FileWriter outputfile = null;
        try {
            outputfile = new FileWriter(statsDump);
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer = new CSVWriter(outputfile);
        String[] header = { "Op-Type", "Span-Durations", "Group", "Group-Peak", "Group-Floor", "Group-Average" };
        writer.writeNext(header);
    }

    public TimeKeeper(String op, boolean logToFile) {
        this.op = op;
        this.logToFile = logToFile;
    }

    public long start() {
        long click = clicks.addAndGet(1);
        starts.put(click, Instant.now());
        return click;
    }

    public Duration stop(long id) {
        Instant now = Instant.now();
        Duration span = null;
        Instant start = starts.get(id);
        if (null != start) {
            span = Duration.between(start, now);
            durations.add(span);
        } else {
            System.out.println("Span Id Not Valid " + id);
        }
        return span;
    }

    public Result getStats() {
        Duration span;
        Result result = new Result();

        long spanCount = clicks.get();
        long counter = 0;

        List<String[]> dataForCSV = new ArrayList<>();
        String groupId = UUID.randomUUID().toString();

        while (counter < spanCount) {
            span = durations.poll();
            result.totalDuration = result.totalDuration.plus(span);

            if (span.compareTo(result.peak) > 0) {
                result.peak = span;
            }

            if (span.compareTo(result.floor) < 0) {
                result.floor = span;
            }
            counter++;
            clicks.decrementAndGet();

            if ( logToFile ) {
                String[] data = {getOp(), Integer.toString(span.getNano()), groupId, "", "", "" };
                dataForCSV.add(data);
            }
        }

        if (spanCount > 0 ) {
            result.avgDuration = result.totalDuration.dividedBy(spanCount);
        }

        result.opsCount = spanCount;

        if ( logToFile ) {
            String[] data = {getOp(), "", groupId, Integer.toString(result.peak.getNano()), Integer.toString(result.floor.getNano()), Integer.toString(result.avgDuration.getNano())};
            dataForCSV.add(data);
            synchronized (TimeKeeper.class) {
                writer.writeAll(dataForCSV);
            }
        }
        return result;
    }

    public static class Result {
        public Duration totalDuration = Duration.ZERO;
        public Duration peak = Duration.ZERO;
        public Duration floor = ChronoUnit.FOREVER.getDuration();
        public Duration avgDuration = Duration.ZERO;
        public long opsCount;
    }

    public static String humanReadableFormat(Duration duration) {
        return duration.toString().substring(2).replaceAll("(\\d[HMS])(?!$)", "$1 ").toLowerCase();
    }

    public String getOp() {
        return op;
    }

    public void finalize() {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}