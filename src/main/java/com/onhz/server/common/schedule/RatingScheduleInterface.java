package com.onhz.server.common.schedule;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RatingScheduleInterface {
    void ratings();
    void batch(Page<?> entities);
    void entityInsertAndUpdate(Long entityId);
    Page<?> findEntitiesWithReviews(Pageable pageable);
}
