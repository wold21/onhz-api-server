package com.onhz.server.repository;

import com.onhz.server.entity.artist.ArtistEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface ArtistRepository extends JpaRepository<ArtistEntity, Long> {
    @Query(value = "SELECT a.id FROM ArtistEntity a")
    Page<Long> findAllIds(Pageable pageable);
}
