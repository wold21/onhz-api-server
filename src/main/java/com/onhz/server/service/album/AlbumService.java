package com.onhz.server.service.album;


import com.onhz.server.common.utils.PageUtils;
import com.onhz.server.dto.response.album.AlbumDetailResponse;
import com.onhz.server.dto.response.album.AlbumFeaturedResponse;
import com.onhz.server.dto.response.album.AlbumGenreArtistResponse;
import com.onhz.server.dto.response.album.AlbumResponse;
import com.onhz.server.entity.album.AlbumEntity;
import com.onhz.server.entity.album.AlbumRatingSummaryEntity;
import com.onhz.server.exception.ErrorCode;
import com.onhz.server.exception.NotFoundException;
import com.onhz.server.repository.AlbumRatingSummaryRepository;
import com.onhz.server.repository.AlbumRepository;
import com.onhz.server.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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

    public List<AlbumGenreArtistResponse> getAlbums(Long lastId, String lastOrderValue, int limit, String orderBy) {
        boolean isRating = orderBy.contains("rating");

        List<Long> albumIds;

        if (isRating) {
            if(lastId != null && lastOrderValue != null) {
                throw new IllegalArgumentException("페이징 조회할 수 없습니다.");
            }
            albumIds = getAlbumIdsWithRating(limit, orderBy);
        } else {
            albumIds = getAlbumsAllIds(lastId, lastOrderValue, limit, orderBy);
        }

        if (albumIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<AlbumGenreArtistResponse> response = getAlbumWithGenreAndArtist(albumIds);
        return response;
    }

    private List<Long> getAlbumIdsWithRating(int limit, String orderBy) {
        Pageable pageable = PageUtils.createPageable(0, limit, orderBy, AlbumRatingSummaryEntity.class);
        return albumRatingSummaryRepository.findAllIdsWithRating(pageable);
    }

    private List<Long> getAlbumsAllIds(Long lastId, String lastOrderValue, int limit, String orderBy) {
        Pageable pageable = PageUtils.createPageable(0, limit, orderBy, AlbumEntity.class);
        return albumRepository.findAllIds(lastId, lastOrderValue, pageable);
    }

    public AlbumDetailResponse getAlbumByTrackIdWithDetail(Long trackId) {
        AlbumEntity albums = albumRepository.findByTracksId(trackId);
        return getAlbumWithDetail(albums.getId());
    }

    public AlbumDetailResponse getAlbumWithDetail(Long albumId) {
        AlbumEntity album = getAlbumWithGenreAndArtist(albumId);
        return AlbumDetailResponse.of(album);

    }

//    public List<AlbumDetailResponse> getAlbumsWithGenreAndArtist(int offset, int limit, String orderBy, String genreCode) {
//        boolean isRating = orderBy.contains("rating");
//
//        Page<Long> albumIds;
//
//        if (isRating) {
//            Pageable pageable = PageUtils.createPageable(offset, limit);
//            albumIds = albumRatingSummaryRepository.findAllIdsWithRatingAndGenre(genreCode, pageable);
//        } else {
//            Pageable pageable = PageUtils.createPageable(offset, limit, orderBy, AlbumEntity.class);
//            albumIds = albumRepository.findAlbumIdsByGenreCode(genreCode, pageable);
//        }
//
//        if (albumIds.isEmpty()) {
//            return Collections.emptyList();
//        }
//
//        List<AlbumEntity> albums = albumRepository.findByIdInWithGenresAndArtists(albumIds.getContent());
//
//        return albums.stream()
//                .map(AlbumDetailResponse::of)
//                .collect(Collectors.toList());
//    }

    public List<AlbumFeaturedResponse> getAlbumsWithFeatured(int offset, int limit, String orderBy, String genreCode) {
        Pageable pageable = PageUtils.createPageable(offset, limit, orderBy, AlbumRatingSummaryEntity.class);
        Page<Long> albumIds = albumRatingSummaryRepository.findAllIdsWithRatingAndGenre(genreCode, pageable);

        if (albumIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<AlbumEntity> albums = albumRepository.findByIdInWithGenresAndArtists(albumIds.getContent());
        Map<Long, Double> ratingMap = albumRatingSummaryRepository.findByAlbumIdIn(albumIds.getContent())
                .stream()
                .collect(Collectors.toMap(
                        AlbumRatingSummaryEntity::getId,
                        AlbumRatingSummaryEntity::getAverageRating,
                        (existing, replacement) -> existing
                ));

        return albums.stream()
                .map(album -> AlbumFeaturedResponse.of(album,
                        ratingMap.getOrDefault(album.getId(), 0.0)))
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

    private List<AlbumGenreArtistResponse> getAlbumWithGenreAndArtist(List<Long> albumIds) {
        List<AlbumEntity> albums = albumRepository.findByIdInWithGenresAndArtists(albumIds);

        return albums.stream()
                .map(AlbumGenreArtistResponse::from)
                .collect(Collectors.toList());
    }

    private AlbumEntity getAlbumWithGenreAndArtist(Long albumId) {
        return albumRepository.findAlbumDetailsById(albumId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_EXCEPTION,
                        "앨범을 찾을 수 없습니다."));
    }

}
