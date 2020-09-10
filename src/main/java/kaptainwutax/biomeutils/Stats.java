package kaptainwutax.biomeutils;

import com.google.common.util.concurrent.AtomicLongMap;

public class Stats {

    // simple way to obain statistics
    private static final AtomicLongMap<Object> messStats = AtomicLongMap.create();

    public static StatsCallback statsCallback;

    public static void incr(String mess) {
        long c = messStats.incrementAndGet(mess);
        if (statsCallback != null) {
            statsCallback.log(messStats, mess, c);
        }
    }

    public static AtomicLongMap<Object> getMessStats() {
        return messStats;
    }

    public static long getCount(String mess) {
        return messStats.get(mess);
    }
}