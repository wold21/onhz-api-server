package com.onhz.server.service;

import com.onhz.server.dto.response.SearchResponse;
import com.onhz.server.dto.response.album.AlbumGenreArtistResponse;
import com.onhz.server.dto.response.artist.ArtistSimpleResponse;
import com.onhz.server.entity.album.AlbumEntity;
import com.onhz.server.entity.track.TrackEntity;
import com.onhz.server.service.search.SearchService;
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
        List result = searchService.searchArtist(keyword, type, lastId, lastOrderValue, limit, orderBy);

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
        List<?> result = searchService.searchAlbum(keyword, type, lastId, lastOrderValue, limit, orderBy);

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
        List<?> result = searchService.searchTrack(keyword, type, lastId, lastOrderValue, limit, orderBy);

        //then
        assertNotNull(result);
    }
}
