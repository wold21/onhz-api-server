package com.onhz.server.repository;

import com.onhz.server.entity.SocialEntity;
import com.onhz.server.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialRepository extends JpaRepository<SocialEntity, Long> {
    Optional<SocialEntity> findByCode(String code);
}
