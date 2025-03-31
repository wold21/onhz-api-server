package com.onhz.server.repository;

import com.onhz.server.entity.artist.ArtistEntity;
import com.onhz.server.repository.dsl.ArtistDSLRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ArtistRepository extends JpaRepository<ArtistEntity, Long>, ArtistDSLRepository { }
