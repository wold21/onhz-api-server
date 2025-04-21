package com.onhz.server.service;

import com.onhz.server.dto.response.SearchResponse;
import com.onhz.server.dto.response.album.AlbumGenreArtistResponse;
import com.onhz.server.dto.response.artist.ArtistSimpleResponse;
import com.onhz.server.entity.album.AlbumEntity;
import com.onhz.server.entity.track.TrackEntity;
import com.onhz.server.service.search.SearchAlbumService;
import com.onhz.server.service.search.SearchArtistService;
import com.onhz.server.service.search.SearchService;
import com.onhz.server.service.search.SearchTrackService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional(readOnly = true)
public class SearchServiceTest {
    @Autowired
    private SearchService searchService;
    @Autowired
    private SearchArtistService searchArtistService;
    @Autowired
    private SearchAlbumService searchAlbumService;
    @Autowired
    private SearchTrackService searchTrackService;

    @Test
    @DisplayName("아티스트 검색")
    void searchArtist(){
        //given
        Long lastId = null;
        String lastOrderValue = null;
        int limit = 5;
        String orderBy = "createdAt";
        String keyword = "nem";
        String type = "artist";

        //when
        List result = searchArtistService.searchArtist(keyword, lastId, lastOrderValue, limit, orderBy);

        //then
        assertNotNull(result);
    }

    @Test
    @DisplayName("앨범 검색")
    void searchAlbum(){
        //given
        Long lastId = null;
        String lastOrderValue = null;
        int limit = 5;
        String orderBy = "createdAt";
        String keyword = "mo";
        String type = "album";

        //when
        List<?> result = searchAlbumService.searchAlbum(keyword, lastId, lastOrderValue, limit, orderBy);

        //then
        assertNotNull(result);
    }

    @Test
    @DisplayName("트랙 검색")
    void searchTrack(){
        //given
        Long lastId = null;
        String lastOrderValue = null;
        int limit = 5;
        String orderBy = "createdAt";
        String keyword = "mo";
        String type = "track";

        //when
        List<?> result = searchTrackService.searchTrack(keyword, lastId, lastOrderValue, limit, orderBy);

        //then
        assertNotNull(result);
    }
}
