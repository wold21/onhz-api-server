package com.onhz.server.repository;

import com.onhz.server.entity.GenreEntity;
import com.onhz.server.entity.SocialEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GenreRepository extends JpaRepository<GenreEntity, Long> {
}
