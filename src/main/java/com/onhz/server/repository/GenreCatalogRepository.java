package com.onhz.server.repository;

import com.onhz.server.entity.GenreCatalogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GenreCatalogRepository extends JpaRepository<GenreCatalogEntity, Long> {
    Optional<GenreCatalogEntity> findByCode(String code);
}
