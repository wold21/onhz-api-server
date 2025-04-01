package com.onhz.server.service.artist;

import com.onhz.server.common.utils.PageUtils;
import com.onhz.server.dto.response.album.AlbumResponse;
import com.onhz.server.dto.response.artist.ArtistResponse;
import com.onhz.server.dto.response.track.TrackDetailResponse;
import com.onhz.server.dto.response.track.TrackResponse;
import com.onhz.server.entity.album.AlbumEntity;
import com.onhz.server.entity.album.AlbumRatingSummaryEntity;
import com.onhz.server.entity.artist.ArtistEntity;
import com.onhz.server.entity.artist.ArtistRatingSummaryEntity;
import com.onhz.server.entity.artist.ArtistTrackEntity;
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
            Pageable pageable = PageUtils.createPageable(0, limit, orderBy, ArtistRatingSummaryEntity.class);
            trackIds = trackRatingSummaryRepository.findTrackIdsByArtistIdWithRating(artistId, pageable);
        } else {
            Pageable pageable = PageUtils.createPageable(0, limit, orderBy, TrackEntity.class);
            trackIds = trackRepository.findTrackIdsByArtistId(artistId, lastId, lastOrderValue, pageable);;
        }
        if(trackIds == null || trackIds.isEmpty()){
            return Collections.emptyList();
        }

        List<TrackDetailResponse> tracks = trackRepository.getTracksWithArtistsAndRatingByIds(trackIds);
        return tracks;
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
            Pageable pageable = PageUtils.createPageable(0, limit, orderBy, AlbumRatingSummaryEntity.class);
            albumIds = albumRatingSummaryRepository.findAlbumIdsByArtistIdWithRating(artistId, pageable);
        } else {
            Pageable pageable = PageUtils.createPageable(0, limit, orderBy, AlbumEntity.class);
            albumIds = albumRepository.findAlbumIdsByArtistId(artistId, lastId, lastOrderValue, pageable);
        }

        List<AlbumResponse> albums = albumService.getAlbumsByIdsWithGenres(albumIds);
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
