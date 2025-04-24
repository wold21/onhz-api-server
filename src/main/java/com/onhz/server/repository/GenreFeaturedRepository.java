package com.onhz.server.repository;

import com.onhz.server.entity.GenreFeaturedEntity;
import com.onhz.server.repository.dsl.GenreFeatureDSLRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GenreFeaturedRepository extends JpaRepository<GenreFeaturedEntity, Long>, GenreFeatureDSLRepository {
    Optional<GenreFeaturedEntity> findByCodeIgnoreCase(String code);
}
