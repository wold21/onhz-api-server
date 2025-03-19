package com.onhz.server.service.album;


import com.onhz.server.common.utils.PageUtils;
import com.onhz.server.dto.response.album.AlbumDetailResponse;
import com.onhz.server.dto.response.album.AlbumGenreArtistResponse;
import com.onhz.server.dto.response.album.AlbumResponse;
import com.onhz.server.entity.album.AlbumEntity;
import com.onhz.server.entity.album.AlbumRatingSummaryEntity;
import com.onhz.server.exception.ErrorCode;
import com.onhz.server.exception.NotFoundException;
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

    public List<AlbumGenreArtistResponse> getAlbums(int offset, int limit, String orderBy) {
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

        List<AlbumGenreArtistResponse> response = getAlbumWithGenreAndArtist(albumIds);
        return response;
    }

    public AlbumDetailResponse getAlbumWithDetail(Long albumId) {
        AlbumEntity album = getAlbumWithGenreAndArtist(albumId);
        AlbumRatingSummaryEntity ratingSummary = getAlbumRatingSummary(albumId);
        return AlbumDetailResponse.of(album, ratingSummary);

    }



    public List<AlbumGenreArtistResponse> getAlbumsWithGenre(int offset, int limit, String orderBy, String genreCode) {
        boolean isRating = orderBy.contains("rating");

        Page<Long> albumIds;

        if (isRating) {
            Pageable pageable = PageUtils.createPageable(offset, limit);
            albumIds = albumRatingSummaryRepository.findAllIdsWithRatingAndGenre(genreCode, pageable);
        } else {
            Pageable pageable = PageUtils.createPageable(offset, limit, orderBy, AlbumEntity.class);
            albumIds = albumRepository.findAlbumIdsByGenreCode(genreCode, pageable);
        }

        if (albumIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<AlbumGenreArtistResponse> response = getAlbumWithGenreAndArtist(albumIds);
        return response;
    }

    private List<AlbumGenreArtistResponse> getAlbumWithGenreAndArtist(Page<Long> albumIds) {
        List<AlbumEntity> albums = albumRepository.findByIdInWithGenresAndArtists(albumIds.getContent());

        return albums.stream()
                .map(AlbumGenreArtistResponse::from)
                .collect(Collectors.toList());
    }

    public List<AlbumResponse> getAlbumsByIdsWithGenres(List<Long> ids) {
        List<AlbumEntity> albums = albumRepository.findAllById(ids);

        Map<Long, AlbumEntity> albumMap = albums.stream()
                .collect(Collectors.toMap(AlbumEntity::getId, Function.identity()));

        return ids.stream()
                .map(albumMap::get)
                .filter(Objects::nonNull)
                .map(AlbumResponse::from)
                .collect(Collectors.toList());
    }

    private AlbumEntity getAlbumWithGenreAndArtist(Long albumId) {
        return albumRepository.findAlbumDetailsById(albumId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_EXCEPTION,
                        "앨범을 찾을 수 없습니다."));
    }
    private AlbumRatingSummaryEntity getAlbumRatingSummary(Long albumId) {
        return albumRatingSummaryRepository.findByAlbumId(albumId)
                .orElseGet(() -> AlbumRatingSummaryEntity.createEmpty(albumId));
    }

}
