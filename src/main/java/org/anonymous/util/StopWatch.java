package org.anonymous.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class StopWatch {

    private static final Map<String, Span> spans = new HashMap();

    public static void start(String name) {
        spans.put(name, new Span());
    }

    public static void stop(String name) {
        spans.get(name).stop();
    }

    public static long averageInNano(String prefix) {
        // TimeUnit.NANOSECONDS.toMillis(delayNS); for millis
        long total = 0;
        long cnt = 0;
        for (Map.Entry<String, Span> entry : spans.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                total += entry.getValue().duration;
                cnt++;
            }
        }
        return total / cnt;
    }

    public static long peakInNano(String prefix) {
        long peak = 0;
        for (Map.Entry<String, Span> entry : spans.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                if (entry.getValue().duration > peak) {
                    peak = entry.getValue().duration;
                }
            }
        }
        return peak;
    }

    public static long floorInNano(String prefix) {
        long floor = Long.MAX_VALUE;
        for (Map.Entry<String, Span> entry : spans.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                if (entry.getValue().duration < floor) {
                    floor = entry.getValue().duration;
                }
            }
        }
        return floor;
    }

    static class Span {
        private long start;
        private long duration;

        Span() {
            start = System.nanoTime();
        }

        void stop() {
            duration = System.nanoTime() - start;
        }
    }
}