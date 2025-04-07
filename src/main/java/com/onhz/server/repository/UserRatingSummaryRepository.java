package com.onhz.server.repository;

import com.onhz.server.entity.user.UserEntity;
import com.onhz.server.entity.user.UserRatingSummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRatingSummaryRepository extends JpaRepository<UserRatingSummaryEntity, Long> {
    Optional<UserRatingSummaryEntity> findByUser(UserEntity user);
    UserRatingSummaryEntity findByUserId(Long userId);
}
