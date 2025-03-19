package com.onhz.server.repository.dsl;


import com.onhz.server.entity.album.AlbumEntity;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AlbumDSLRepository {
    List<AlbumEntity> findByIdInWithGenresAndArtists(@Param("ids") List<Long> ids);
    Optional<AlbumEntity> findAlbumDetailsById(Long albumId);
}
