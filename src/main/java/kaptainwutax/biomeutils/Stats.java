package kaptainwutax.biomeutils;

import com.google.common.util.concurrent.AtomicLongMap;

public class Stats {

    // simple way to obain statistics
    private static final AtomicLongMap<Object> messStats = AtomicLongMap.create();

    public static void incr(String mess) {
        long c = messStats.incrementAndGet(mess);
        if (c % Math.pow(10.0, Math.log10(c)) == 0.0) {
//        if(c % 10_000 == 0) {
            System.out.println("Times of message: $mess: $c");
        }
    }

    public static AtomicLongMap<Object> getMessStats() {
        return messStats;
    }

    public static long getCount(String mess) {
        return messStats.get(mess);
    }
}