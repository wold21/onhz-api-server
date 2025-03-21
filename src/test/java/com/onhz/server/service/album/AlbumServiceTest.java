package com.onhz.server.service.album;

import com.onhz.server.dto.response.album.AlbumDetailResponse;
import com.onhz.server.dto.response.album.AlbumGenreArtistResponse;
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
        int offset = 0;
        int limit = 5;
        String orderBy = "created_at";

        //when
        List<AlbumGenreArtistResponse> result =  albumService.getAlbums(offset, limit, orderBy);

        //then
        assert(!result.isEmpty());
        assert(result.size() == limit);
    }

    @Test
    @DisplayName("인기 앨범 조회")
    void getAlbumsWithRating(){
        //given
        int offset = 0;
        int limit = 5;
        String orderBy = "rating_count,average_rating";

        //when
        List<AlbumGenreArtistResponse> result =  albumService.getAlbums(offset, limit, orderBy);

        //then
        assert(!result.isEmpty());
        assert(result.size() == limit);
    }

    @Test
    @DisplayName("앨범 장르 조회")
    void getAlbumsWithGenre(){
        //given
        int offset = 0;
        int limit = 5;
        String orderBy = "created_at";
        String genreCode = "ROCK";

        //when
        List<AlbumDetailResponse> result =  albumService.getAlbumsWithGenreAndArtist(offset, limit, orderBy, genreCode);

        //then
        assert(!result.isEmpty());
        assert(result.size() == limit);
    }

    @Test
    @DisplayName("인기앨범 장르 조회")
    void getAlbumsWithGenreAndRating(){
        //given
        int offset = 0;
        int limit = 5;
        String orderBy = "rating_count,average_rating";
        String genreCode = "ROCK";

        //when
        List<AlbumDetailResponse> result =  albumService.getAlbumsWithGenreAndArtist(offset, limit, orderBy, genreCode);

        //then
        assert(!result.isEmpty());
        assert(result.size() == limit);
    }

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
}
