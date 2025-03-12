package com.onhz.server.repository;

import com.onhz.server.entity.artist.ArtistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ArtistRepository extends JpaRepository<ArtistEntity, Long> {
}
