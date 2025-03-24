package com.onhz.server.repository;

import com.onhz.server.entity.user.UserDeletedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDeletedRepository extends JpaRepository<UserDeletedEntity, Long> {
}
