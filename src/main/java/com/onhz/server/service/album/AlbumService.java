package com.onhz.server.service.album;


import com.onhz.server.common.utils.PageUtils;
import com.onhz.server.dto.response.AlbumGenreResponse;
import com.onhz.server.entity.album.AlbumEntity;
import com.onhz.server.repository.album.AlbumRepository;
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

    public List<AlbumGenreResponse> getAlbums(int offset, int limit, String orderBy) {
        if(!PageUtils.isValidSortField(orderBy)) {
            throw new IllegalArgumentException("정렬조건이 올바르지 않습니다.");
        }
        Pageable pageable = PageUtils.createPageable(offset, limit, orderBy);

        boolean isRating = orderBy.contains("rating");

        Page<Long> albumIds;

        if (isRating) {
            albumIds = albumRepository.findAllIdsWithRating(pageable);
        } else {
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
