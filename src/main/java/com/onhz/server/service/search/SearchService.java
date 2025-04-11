package com.onhz.server.service.search;

import com.onhz.server.common.utils.PageUtils;
import com.onhz.server.dto.response.SearchResponse;
import com.onhz.server.dto.response.album.AlbumDetailResponse;
import com.onhz.server.dto.response.album.AlbumResponse;
import com.onhz.server.dto.response.artist.ArtistSimpleResponse;
import com.onhz.server.dto.response.track.TrackDetailResponse;
import com.onhz.server.dto.response.track.TrackResponse;
import com.onhz.server.entity.album.AlbumEntity;
import com.onhz.server.entity.artist.ArtistEntity;
import com.onhz.server.entity.track.TrackEntity;
import com.onhz.server.repository.AlbumRepository;
import com.onhz.server.repository.ArtistRepository;
import com.onhz.server.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final TrackRepository trackRepository;
    public List<?> search(Long lastId, String lastOrderValue, int limit, String orderBy, String keyword, String type) {
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("검색어를 입력해주세요.");
        }
        if(type == null || type.isBlank()) {
            throw new IllegalArgumentException("검색 타입을 입력해주세요.");
        }
        switch (type.toLowerCase()) {
            case "artist":
                return searchArtist(keyword, type, lastId, lastOrderValue, limit, orderBy);
            case "album":
                return searchAlbum(keyword, type, lastId, lastOrderValue, limit, orderBy);
            case "track":
                return searchTrack(keyword, type, lastId, lastOrderValue, limit, orderBy);
            default:
                throw new IllegalArgumentException("허용되지 않은 검색 타입입니다.");
        }
    }

    public List searchArtist(String keyword, String type, Long lastId, String lastOrderValue, int limit, String orderBy) {
        Pageable pageable = PageUtils.createPageable(0, limit, orderBy, ArtistEntity.class);
        List<ArtistEntity> artists = artistRepository.findArtistsByKeyword(keyword, lastId, lastOrderValue, pageable);
        return artists.stream().map(ArtistSimpleResponse::from).toList();
    }

    public List searchAlbum(String keyword, String type, Long lastId, String lastOrderValue, int limit, String orderBy) {
        Pageable pageable = PageUtils.createPageable(0, limit, orderBy, AlbumEntity.class);
        List<AlbumEntity> albums = albumRepository.findAlbumsByKeyword(keyword, lastId, lastOrderValue, pageable);
        return albums.stream().map(AlbumResponse::from).toList();
    }

    public List searchTrack(String keyword, String type, Long lastId, String lastOrderValue, int limit, String orderBy) {
        Pageable pageable = PageUtils.createPageable(0, limit, orderBy, TrackEntity.class);
        List<TrackDetailResponse> tracks = trackRepository.findTracksWithDetailsByKeyword(keyword, lastId, lastOrderValue, pageable);
        return tracks;
    }


}
