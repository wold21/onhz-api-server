package com.onhz.server.repository;

import com.onhz.server.entity.user.UserSocialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSocialSessionRepository  extends JpaRepository<UserSocialEntity, Long> {
    Optional<UserSocialEntity> findByUserId(Long userId);
}
