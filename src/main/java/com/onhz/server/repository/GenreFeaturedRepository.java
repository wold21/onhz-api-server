package com.onhz.server.repository;

import com.onhz.server.entity.GenreFeaturedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GenreFeaturedRepository extends JpaRepository<GenreFeaturedEntity, Long> {
    Optional<GenreFeaturedEntity> findByCode(String code);
}
