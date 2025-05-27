package com.nagornov.CorporateMessenger.domain.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SnowflakeIdGenerator {
    private static final long EPOCH = 1700000000000L;
    private static final long WORKER_ID = 1L;
    private static final long WORKER_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    private long lastTimestamp = -1L;
    private long sequence = 0L;

    public synchronized static long nextId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                while ((timestamp = System.currentTimeMillis()) <= lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - EPOCH) << TIMESTAMP_LEFT_SHIFT)
                | (WORKER_ID << WORKER_ID_SHIFT)
                | sequence;
    }
}

