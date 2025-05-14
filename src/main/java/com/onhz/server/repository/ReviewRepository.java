package com.onhz.server.repository;

import com.onhz.server.common.enums.ReviewType;
import com.onhz.server.entity.review.ReviewEntity;
import com.onhz.server.entity.user.UserEntity;
import com.onhz.server.entity.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    List<ReviewEntity> findByUserId(Long userId);

    @Query("select distinct r.user from ReviewEntity r where r.rating is not null")
    Page<UserEntity> findDistinctUserIdsByRatingIsNotNull(Pageable pageable);

    @Query("""
    select distinct r.entityId from ReviewEntity r 
    where r.reviewType = :reviewType and r.rating is not null
    """)
    Page<Long> findDistinctEntityIdsByReviewTypeAndRatingIsNotNull(@Param("reviewType") ReviewType reviewType, Pageable pageable);

    List<ReviewEntity> findByReviewTypeAndEntityId(ReviewType reviewType, Long entityId);
    Optional<ReviewEntity> findByUserAndEntityIdAndReviewType(UserEntity user, Long entityId, ReviewType reviewType);

    long countByReviewTypeAndEntityIdAndRatingIsNotNull(ReviewType reviewType, Long entityId);
    long countByUserIdAndRatingIsNotNull(Long userId);
}
