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

    public static final int MAX_SPANS_FOR_CONDENSE = 30;
    private final String op;
    private Instant creationTime = Instant.now();

    private AtomicLong clicks = new AtomicLong(0);
    private Duration avgDuration = Duration.ZERO;
    private int opsCount = 0;

    private Duration peak = Duration.ZERO;
    private Duration floor = ChronoUnit.FOREVER.getDuration();

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

    public void condense() {
        Duration span;
        Duration totalDuration = Duration.ZERO;
        int spanCount = 1;
        while ((span = durations.poll()) != null && spanCount <= MAX_SPANS_FOR_CONDENSE) {
            totalDuration = totalDuration.plus(span);

            if (span.compareTo(peak) > 0) {
                peak = span;
            }

            if (span.compareTo(floor) < 0) {
                floor = span;
            }

            spanCount++;
        }
        avgDuration = totalDuration.dividedBy(spanCount);
        opsCount = spanCount;
    }

    public Duration peak() {
        return peak;
    }

    public Duration floor() {
        return floor;
    }

    public Duration avg() {
        return avgDuration;
    }

    public long opsCount() {
        return opsCount;
    }

    public Duration lifetime() {
        return Duration.between(creationTime, Instant.now());
    }

    public static String humanReadableFormat(Duration duration) {
        return duration.toString().substring(2).replaceAll("(\\d[HMS])(?!$)", "$1 ").toLowerCase();
    }

    public String getOp() {
        return op;
    }
}