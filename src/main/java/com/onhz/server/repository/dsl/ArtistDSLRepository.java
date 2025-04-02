package com.onhz.server.repository.dsl;


import com.onhz.server.entity.album.AlbumEntity;
import com.onhz.server.entity.artist.ArtistEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ArtistDSLRepository {
    List<Long> findAllIds(Long lastId, String lastOrderValue, Pageable pageable);
    List<ArtistEntity> findArtistsByKeyword(String keyword, Long lastId, String lastOrderValue, Pageable pageable);

}
