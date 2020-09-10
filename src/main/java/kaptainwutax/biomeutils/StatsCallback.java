package kaptainwutax.biomeutils;

import com.google.common.util.concurrent.AtomicLongMap;

@FunctionalInterface
public interface StatsCallback {
    void log(AtomicLongMap<Object> messStats, String mess, long l);
}
