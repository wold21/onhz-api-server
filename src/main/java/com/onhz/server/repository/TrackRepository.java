package com.onhz.server.repository;

import com.onhz.server.entity.track.TrackEntity;
import com.onhz.server.repository.dsl.TrackDSLRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackRepository extends JpaRepository<TrackEntity, Long>, TrackDSLRepository {
}
