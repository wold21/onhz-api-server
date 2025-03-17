package com.onhz.server.entity;

import java.time.LocalDateTime;

public interface RatingSummaryEntity {
    Long getId();
    Double getAverageRating();
    Integer getRatingCount();
    Object getRatingDist();
    LocalDateTime getLastUpdated();
}