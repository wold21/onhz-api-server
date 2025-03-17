package com.onhz.server.service.artist;

import com.onhz.server.common.utils.PageUtils;
import com.onhz.server.dto.response.ArtistResponse;
import com.onhz.server.dto.response.ArtistTrackResponse;
import com.onhz.server.dto.response.TrackResponse;
import com.onhz.server.entity.artist.ArtistEntity;
import com.onhz.server.entity.artist.ArtistRatingSummaryEntity;
import com.onhz.server.entity.track.TrackEntity;
import com.onhz.server.entity.track.TrackRatingSummaryEntity;
import com.onhz.server.exception.NotFoundException;
import com.onhz.server.exception.example.ErrorCode;
import com.onhz.server.repository.ArtistRatingSummaryRepository;
import com.onhz.server.repository.ArtistRepository;
import com.onhz.server.repository.TrackRatingSummaryRepository;
import com.onhz.server.repository.TrackRepository;
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
    private final TrackRatingSummaryRepository trackRatingSummaryRepository;
    private final TrackRepository trackRepository;
    private final TrackService trackService;

    public List<ArtistResponse> getArtists(int offset, int limit, String orderBy){
        boolean isRating = orderBy.contains("rating");

        Page<Long> artistIds;
        if(isRating){
            Pageable pageable = PageUtils.createPageable(offset, limit, orderBy, ArtistRatingSummaryEntity.class);
            artistIds = artistRatingSummaryRepository.findAllIdsWithRating(pageable);
        } else {
            Pageable pageable = PageUtils.createPageable(offset, limit, orderBy, ArtistEntity.class);
            artistIds = artistRepository.findAllIds(pageable);
        }

        if (artistIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> artistIdList = artistIds.getContent();
        List<ArtistResponse> response = getArtistResponsesByIds(artistIdList);

        return response;
    }

    public ArtistResponse getArtist(Long artistId){
        return getArtistResponsesByIds(Collections.singletonList(artistId)).stream()
                .findFirst()
                .orElse(null);
    }

    public ArtistTrackResponse getArtistWithTracks(Long artistId, int offset, int limit, String orderBy){
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

        List<TrackResponse> tracks;
        if (trackIds.isEmpty()) {
            tracks = Collections.emptyList();
        } else {
            tracks = trackService.getTrackResponsesByIds(trackIds.getContent());
        }
        return ArtistTrackResponse.of(artist, tracks);

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
