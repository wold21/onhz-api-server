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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final SearchArtistService searchArtistService;
    private final SearchAlbumService searchAlbumService;
    private final SearchTrackService searchTrackService;
    public List<?> search(Long lastId, String lastOrderValue, int limit, String orderBy, String keyword, String type) {
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("검색어를 입력해주세요.");
        }
        if(type == null || type.isBlank()) {
            throw new IllegalArgumentException("검색 타입을 입력해주세요.");
        }
        switch (type.toLowerCase()) {
            case "artist":
                return searchArtistService.searchArtist(keyword, lastId, lastOrderValue, limit, orderBy);
            case "album":
                return searchAlbumService.searchAlbum(keyword, lastId, lastOrderValue, limit, orderBy);
            case "track":
                return searchTrackService.searchTrack(keyword, lastId, lastOrderValue, limit, orderBy);
            default:
                throw new IllegalArgumentException("허용되지 않은 검색 타입입니다.");
        }
    }
}
