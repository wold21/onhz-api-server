package com.onhz.server.service.artist;

import com.onhz.server.common.utils.PageUtils;
import com.onhz.server.dto.response.album.AlbumResponse;
import com.onhz.server.dto.response.artist.ArtistResponse;
import com.onhz.server.dto.response.track.TrackResponse;
import com.onhz.server.entity.album.AlbumEntity;
import com.onhz.server.entity.album.AlbumRatingSummaryEntity;
import com.onhz.server.entity.artist.ArtistEntity;
import com.onhz.server.entity.artist.ArtistRatingSummaryEntity;
import com.onhz.server.entity.track.TrackEntity;
import com.onhz.server.entity.track.TrackRatingSummaryEntity;
import com.onhz.server.exception.NotFoundException;
import com.onhz.server.exception.ErrorCode;
import com.onhz.server.repository.*;
import com.onhz.server.service.album.AlbumService;
import com.onhz.server.service.track.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
            artistIds = getArtistIdsWithRating(limit, orderBy);
        } else {
            artistIds = getArtistAllIds(lastId, lastOrderValue, limit, orderBy);
        }

        if (artistIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<ArtistResponse> response = getArtistResponsesByIds(artistIds);

        return response;
    }

    private List<Long> getArtistIdsWithRating(int limit, String orderBy) {
        Pageable pageable = PageUtils.createPageable(0, limit, orderBy, ArtistRatingSummaryEntity.class);
        return artistRatingSummaryRepository.findAllIdsWithRating(pageable);
    }

    private List<Long> getArtistAllIds(Long lastId, String lastOrderValue, int limit, String orderBy) {
        Pageable pageable = PageUtils.createPageable(0, limit, orderBy, ArtistEntity.class);
        return artistRepository.findAllIds(lastId, lastOrderValue, pageable);
    }

    public ArtistResponse getArtist(Long artistId){
        return getArtistResponsesByIds(Collections.singletonList(artistId)).stream()
                .findFirst()
                .orElse(null);
    }

    public List<TrackResponse> getArtistWithTracks(Long artistId, int offset, int limit, String orderBy){
        ArtistEntity artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_EXCEPTION, "아티스트를 찾을 수 없습니다."));

        boolean isRating = orderBy.contains("rating");

        Page<Long> trackIds;
        if(isRating){
            Pageable pageable = PageUtils.createPageable(offset, limit, orderBy, TrackRatingSummaryEntity.class);
            trackIds = trackRatingSummaryRepository.findTrackIdsByArtistIdWithRating(artistId, pageable);
        } else {
            Pageable pageable = PageUtils.createPageable(offset, limit, orderBy, TrackEntity.class);
            trackIds = trackRepository.findTrackIdsByArtistId(artistId, pageable);
        }

        List<TrackResponse> tracks = trackService.getTrackResponsesByIds(trackIds.getContent());
        return tracks;

    }

    public List<AlbumResponse> getArtistWithAlbums(Long artistId, int offset, int limit, String orderBy){
        ArtistEntity artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_EXCEPTION, "아티스트를 찾을 수 없습니다."));

        boolean isRating = orderBy.contains("rating");

        Page<Long> albumIds;
        if(isRating){
            Pageable pageable = PageUtils.createPageable(offset, limit, orderBy, AlbumRatingSummaryEntity.class);
            albumIds = albumRatingSummaryRepository.findAlbumIdsByArtistIdWithRating(artistId, pageable);
        } else {
            Pageable pageable = PageUtils.createPageable(offset, limit, orderBy, AlbumEntity.class);
            albumIds = albumRepository.findAlbumIdsByArtistId(artistId, pageable);
        }

        List<AlbumResponse> albums = albumService.getAlbumsByIdsWithGenres(albumIds.getContent());
        return albums;

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
