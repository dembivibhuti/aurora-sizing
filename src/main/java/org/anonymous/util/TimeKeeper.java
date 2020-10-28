package org.anonymous.util;

import java.util.Map;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentHashMap;

public class TimeKeeper {

    public static final int MAX_SPANS_FOR_CONDENSE = 2000;
    private final String op;
    private Instant resetTime = Instant.now();

    private AtomicLong clicks = new AtomicLong(0);
    private Map<Long, Instant> starts = new ConcurrentHashMap<>();
    private Queue<Duration> durations = new ConcurrentLinkedDeque<>();

    public TimeKeeper(String op) {
        this.op = op;
    }

    public long start() {
        Instant now = Instant.now();
        long click = clicks.addAndGet(1);
        starts.put(click, now);
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
        }

        if (spanCount > 0 ) {
            result.avgDuration = result.totalDuration.dividedBy(spanCount);
        }

        result.opsCount = spanCount;
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
}