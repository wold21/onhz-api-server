package com.onhz.server.repository;

import com.onhz.server.entity.user.UserDeletedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface UserDeletedRepository extends JpaRepository<UserDeletedEntity, Long> {
    List<UserDeletedEntity> findByDeletedAtBefore(LocalDateTime dateTime);
}
