package com.onhz.server.service.album;

import com.onhz.server.dto.response.album.AlbumDetailResponse;
import com.onhz.server.dto.response.album.AlbumGenreArtistResponse;
import com.onhz.server.dto.response.track.TrackResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional(readOnly = true)
public class AlbumServiceTest {
    @Autowired
    private AlbumService albumService;

    
    @Test
    @DisplayName("앨범 조회")
    void getAlbums(){
        //given
        Long lastId = null;
        String lastOrderValue = null;
        int limit = 5;
        String orderBy = "createdAt";

        //when
        List<AlbumGenreArtistResponse> result =  albumService.getAlbums(lastId, lastOrderValue, limit, orderBy);

        //then
        assert(!result.isEmpty());
        assert(result.size() == limit);
    }

    @Test
    @DisplayName("인기 앨범 조회")
    void getAlbumsWithRating(){
        //given
        Long lastId = null;
        String lastOrderValue = null;
        int limit = 5;
        String orderBy = "ratingCount,averageRating";

        //when
        List<AlbumGenreArtistResponse> result =  albumService.getAlbums(lastId, lastOrderValue, limit, orderBy);

        //then
        assert(!result.isEmpty());
        assert(result.size() == limit);
    }

//    @Test
//    @DisplayName("앨범 장르 조회")
//    void getAlbumsWithGenre(){
//        //given
//        int offset = 0;
//        int limit = 5;
//        String orderBy = "createdAt";
//        String genreCode = "ROCK";
//
//        //when
//        List<AlbumDetailResponse> result =  albumService.getAlbumsWithGenreAndArtist(offset, limit, orderBy, genreCode);
//
//        //then
//        assert(!result.isEmpty());
//        assert(result.size() == limit);
//    }

//    @Test
//    @DisplayName("인기앨범 장르 조회")
//    void getAlbumsWithGenreAndRating(){
//        //given
//        int offset = 0;
//        int limit = 5;
//        String orderBy = "ratingCount,averageRating";
//        String genreCode = "ROCK";
//
//        //when
//        List<AlbumDetailResponse> result =  albumService.getAlbumsWithGenreAndArtist(offset, limit, orderBy, genreCode);
//
//        //then
//        assert(!result.isEmpty());
//        assert(result.size() == limit);
//    }

    @Test
    @DisplayName("앨범 상세 조회")
    void getAlbumsDetail(){
        //given
        Long albumId = 1L;

        //when
        AlbumDetailResponse result =  albumService.getAlbumWithDetail(albumId);

        //then
        assert(result != null);
    }

    @Test
    @DisplayName("트랙 앨범(아티스트 포함) 상세 조회")
    void getAlbumByTrackId(){
        //given
        Long trackId = 97L;

        //when
        AlbumDetailResponse result =  albumService.getAlbumByTrackIdWithDetail(trackId);

        //then
        assert(result != null);
    }
}
