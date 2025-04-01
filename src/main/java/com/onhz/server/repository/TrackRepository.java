package com.onhz.server.repository;

import com.onhz.server.entity.track.TrackEntity;
import com.onhz.server.repository.dsl.TrackDSLRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrackRepository extends JpaRepository<TrackEntity, Long>, TrackDSLRepository {
    List<TrackEntity> findTrackByAlbumIdOrderByTrackRank(Long albumId);
}
