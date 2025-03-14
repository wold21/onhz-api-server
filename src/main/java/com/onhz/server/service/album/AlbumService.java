package com.onhz.server.service.album;


import com.onhz.server.common.utils.PageUtils;
import com.onhz.server.dto.response.AlbumGenreResponse;
import com.onhz.server.entity.album.AlbumEntity;
import com.onhz.server.entity.album.AlbumRatingSummaryEntity;
import com.onhz.server.repository.AlbumRatingSummaryRepository;
import com.onhz.server.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final AlbumRatingSummaryRepository albumRatingSummaryRepository;

    public List<AlbumGenreResponse> getAlbums(int offset, int limit, String orderBy) {
        boolean isRating = orderBy.contains("rating");

        Page<Long> albumIds;

        if (isRating) {
            Pageable pageable = PageUtils.createPageable(offset, limit, orderBy, AlbumRatingSummaryEntity.class);
            albumIds = albumRatingSummaryRepository.findAllIdsWithRating(pageable);
        } else {
            Pageable pageable = PageUtils.createPageable(offset, limit, orderBy, AlbumEntity.class);
            albumIds = albumRepository.findAllIds(pageable);
        }

        if (albumIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<AlbumEntity> albums = albumRepository.findByIdInWithAlbumGenres(albumIds.getContent());

        Map<Long, AlbumEntity> albumMap = albums.stream()
                .collect(Collectors.toMap(AlbumEntity::getId, Function.identity()));

        List<AlbumGenreResponse> response = albumIds.getContent().stream()
                .map(albumMap::get)
                .filter(Objects::nonNull)
                .map(AlbumGenreResponse::from)
                .toList();
        return response;
    }

}
