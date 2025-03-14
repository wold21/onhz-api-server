package com.onhz.server.service.artist;

import com.onhz.server.common.utils.PageUtils;
import com.onhz.server.dto.response.AlbumGenreResponse;
import com.onhz.server.dto.response.ArtistResponse;
import com.onhz.server.entity.album.AlbumEntity;
import com.onhz.server.entity.album.AlbumRatingSummaryEntity;
import com.onhz.server.entity.artist.ArtistEntity;
import com.onhz.server.entity.artist.ArtistRatingSummaryEntity;
import com.onhz.server.repository.ArtistRatingSummaryRepository;
import com.onhz.server.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArtistService {
    private final ArtistRepository artistRepository;
    private final ArtistRatingSummaryRepository artistRatingSummaryRepository;

    public List<ArtistResponse> getArtist(int offset, int limit, String orderBy){
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

        List<ArtistEntity> artists = artistRepository.findAllById(artistIds.getContent());

        Map<Long, ArtistEntity> atristMap = artists.stream()
                .collect(Collectors.toMap(ArtistEntity::getId, Function.identity()));

        List<ArtistResponse> response = artistIds.getContent().stream()
                .map(atristMap::get)
                .filter(Objects::nonNull)
                .map(ArtistResponse::from)
                .toList();
        return response;
    }
}
