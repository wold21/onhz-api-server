package com.onhz.server.service.artist;

import com.onhz.server.common.utils.PageUtils;
import com.onhz.server.dto.response.album.AlbumResponse;
import com.onhz.server.dto.response.artist.ArtistResponse;
import com.onhz.server.dto.response.track.TrackDetailResponse;
import com.onhz.server.entity.album.AlbumEntity;
import com.onhz.server.entity.album.AlbumRatingSummaryEntity;
import com.onhz.server.entity.artist.ArtistEntity;
import com.onhz.server.entity.artist.ArtistRatingSummaryEntity;
import com.onhz.server.entity.track.TrackEntity;
import com.onhz.server.exception.NotFoundException;
import com.onhz.server.exception.ErrorCode;
import com.onhz.server.repository.*;
import com.onhz.server.service.album.AlbumService;
import com.onhz.server.service.track.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArtistService {
    private final ArtistRepository artistRepository;
    private final ArtistRatingSummaryRepository artistRatingSummaryRepository;
    private final AlbumRatingSummaryRepository albumRatingSummaryRepository;
    private final TrackRatingSummaryRepository trackRatingSummaryRepository;
    private final AlbumRepository albumRepository;
    private final AlbumService albumService;
    private final TrackRepository trackRepository;
    private final TrackService trackService;

    public List<ArtistResponse> getArtists(Long lastId, String lastOrderValue, int limit, String orderBy){
        boolean isRating = orderBy.contains("rating");

        List<Long> artistIds;
        if(isRating){
            if(lastId != null && lastOrderValue != null) {
                throw new IllegalArgumentException("페이징 조회할 수 없습니다.");
            }
            Pageable pageable = PageUtils.createPageable(0, limit, orderBy, ArtistRatingSummaryEntity.class);
            artistIds =  artistRatingSummaryRepository.findAllIdsWithRating(pageable);
        } else {
            Pageable pageable = PageUtils.createPageable(0, limit, orderBy, ArtistEntity.class);
            artistIds =  artistRepository.findAllIds(lastId, lastOrderValue, pageable);
        }

        if (artistIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<ArtistResponse> response = getArtistResponsesByIds(artistIds);

        return response;
    }

    public ArtistResponse getArtist(Long artistId){
        return getArtistResponsesByIds(Collections.singletonList(artistId)).stream()
                .findFirst()
                .orElse(null);
    }

    public List<TrackDetailResponse> getArtistWithTracks(Long artistId, Long lastId, String lastOrderValue, int limit, String orderBy){
        artistRepository.findById(artistId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_EXCEPTION, "아티스트를 찾을 수 없습니다."));

        boolean isRating = orderBy.contains("rating");

        List<Long> trackIds;
        if(isRating){
            if(lastId != null && lastOrderValue != null){
                throw new IllegalArgumentException("페이징 조회할 수 없습니다.");
            }
            // 인기 조회
            trackIds = findTrackIdsByArtistIdWithRating(artistId, lastId, lastOrderValue, limit, orderBy);
        } else {
            // 정렬 조건이 rating이 아닐 경우
            trackIds = findTrackIdsByArtistId(artistId, lastId, lastOrderValue, limit, orderBy);
        }
        if(trackIds == null || trackIds.isEmpty()){
            return Collections.emptyList();
        }

        List<TrackDetailResponse> tracks = trackRepository.getTracksWithArtistsAndRatingByIds(trackIds);
        return tracks;
    }
    private List<Long> findTrackIdsByArtistIdWithRating(Long artistId, Long lastId, String lastOrderValue, int limit, String orderBy){
        Pageable pageable = PageUtils.createPageable(0, limit, orderBy, ArtistRatingSummaryEntity.class);
        List<Long> results = trackRatingSummaryRepository.findTrackIdsByArtistIdWithRating(artistId, pageable);
        // 스케쥴링 혹은 리뷰가 없는 경우 데이터가 없기 때문에 이때는 정렬 조건을 createdAt로 사용하여 조회
        if (results == null || results.isEmpty()) {
            return findTrackIdsByArtistId(artistId, lastId, lastOrderValue, limit, "createdAt");
        }
        return results;
    }
    private List<Long> findTrackIdsByArtistId(Long artistId, Long lastId, String lastOrderValue, int limit, String orderBy){
        Pageable pageable = PageUtils.createPageable(0, limit, orderBy, TrackEntity.class);
        return trackRepository.findTrackIdsByArtistId(artistId, lastId, lastOrderValue, pageable);
    }

    public List<AlbumResponse> getArtistWithAlbums(Long artistId, Long lastId, String lastOrderValue, int limit, String orderBy){
        artistRepository.findById(artistId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_EXCEPTION, "아티스트를 찾을 수 없습니다."));

        boolean isRating = orderBy.contains("rating");

        List<Long> albumIds;
        if(isRating){
            if(lastId != null && lastOrderValue != null){
                throw new IllegalArgumentException("페이징 조회할 수 없습니다.");
            }
            // 인기 조회
            albumIds = findAlbumIdsByArtistIdWithRating(artistId, lastId, lastOrderValue, limit, orderBy);
        } else {
            // 정렬 조건이 rating이 아닐 경우
            albumIds = findAlbumIdsByArtistId(artistId, lastId, lastOrderValue, limit, orderBy);
        }

        List<AlbumResponse> albums = albumService.getAlbumsByIdsWithGenres(albumIds);
        return albums;

    }
    private List<Long> findAlbumIdsByArtistIdWithRating(Long artistId, Long lastId, String lastOrderValue, int limit, String orderBy){
        Pageable pageable = PageUtils.createPageable(0, limit, orderBy, AlbumRatingSummaryEntity.class);
        List<Long> results = albumRatingSummaryRepository.findAlbumIdsByArtistIdWithRating(artistId, pageable);
        // 스케쥴링 혹은 리뷰가 없는 경우 데이터가 없기 때문에 이때는 정렬 조건을 createdAt로 사용하여 조회
        if (results == null || results.isEmpty()) {
            return findAlbumIdsByArtistId(artistId, lastId, lastOrderValue, limit, "createdAt");
        }
        return results;
    }
    private List<Long> findAlbumIdsByArtistId(Long artistId, Long lastId, String lastOrderValue, int limit, String orderBy){
        Pageable pageable = PageUtils.createPageable(0, limit, orderBy, AlbumEntity.class);
        return albumRepository.findAlbumIdsByArtistId(artistId, lastId, lastOrderValue, pageable);
    }


    private List<ArtistResponse> getArtistResponsesByIds(List<Long> ids) {
        List<ArtistEntity> artistEntities = artistRepository.findAllById(ids);

        Map<Long, ArtistEntity> artistMap = artistEntities.stream()
                .collect(Collectors.toMap(ArtistEntity::getId, Function.identity()));

        return ids.stream()
                .map(artistMap::get)
                .filter(Objects::nonNull)
                .map(ArtistResponse::from)
                .collect(Collectors.toList());
    }
}
