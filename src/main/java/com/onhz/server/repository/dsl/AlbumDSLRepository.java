package com.onhz.server.repository.dsl;


import com.onhz.server.entity.album.AlbumEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AlbumDSLRepository {
    List<Long> findAllIds(Long lastId, String lastOrderValue, Pageable pageable);
    List<Long> findAlbumIdsByArtistId(Long artistId, Long lastId, String lastOrderValue, Pageable pageable);
    List<AlbumEntity> findByIdInWithGenresAndArtists(@Param("ids") List<Long> ids);
    Optional<AlbumEntity> findAlbumDetailsById(Long albumId);
    List<AlbumEntity> findAlbumsByKeyword(String keyword, Long lastId, String lastOrderValue, Pageable pageable);
}
