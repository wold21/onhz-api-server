package com.onhz.server.common.utils;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiter {
    private final Map<String, List<Instant>> requestTimestamps = new ConcurrentHashMap<>();

    public boolean allowRequest(String key, int maxRequests, int timeWindowInSeconds) {
        Instant now = Instant.now();
        requestTimestamps.putIfAbsent(key, new java.util.ArrayList<>());
        List<Instant> timestamps = requestTimestamps.get(key);

        timestamps.removeIf(timestamp ->
                timestamp.isBefore(now.minusSeconds(timeWindowInSeconds)));

        if (timestamps.size() < maxRequests) {
            timestamps.add(now);
            return true;
        }

        return false;
    }
}
