package com.onhz.server.service.album;

import com.onhz.server.dto.response.AlbumGenreResponse;
import com.onhz.server.dto.response.ArtistResponse;
import com.onhz.server.dto.response.ArtistSimpleResponse;
import com.onhz.server.dto.response.GenreResponse;
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

    void resultToString(AlbumGenreResponse album){
        System.out.println("- 앨범 정보\t(albumId / title / releaseDate / createdAt / coverPath)");
        System.out.println("\t\t\t" + String.format("%d / %s / %s / %s / %s",
                album.getId(),
                album.getTitle(),
                album.getReleaseDate(),
                album.getCreatedAt(),
                album.getCoverPath()));

        System.out.println("- 장르 정보\t(genreId / code / name)");
        for(GenreResponse genre : album.getGenres()){
            System.out.println("\t\t\t" + String.format("%d / %s / %s",
                    genre.getId(),
                    genre.getCode(),
                    genre.getName()));
        }

        System.out.println("- 가수 정보\t(artistId / name / profilePath / role)");
        for(ArtistSimpleResponse artist : album.getArtists()){
            System.out.println("\t\t\t" + String.format("%d / %s / %s / %s",
                    artist.getId(),
                    artist.getName(),
                    artist.getProfilePath(),
                    artist.getRole()));
        }
        System.out.println("\n");
    }

    
    @Test
    @DisplayName("앨범 조회")
    void getAlbums(){
        //given
        int offset = 0;
        int limit = 5;
        String orderBy = "created_at";

        //when
        List<AlbumGenreResponse> result =  albumService.getAlbums(offset, limit, orderBy);

        //then
        assert(!result.isEmpty());
        assert(result.size() == limit);

        for(AlbumGenreResponse album : result){
            resultToString(album);
        }
    }

    @Test
    @DisplayName("인기 앨범 조회")
    void getAlbumsWithRating(){
        //given
        int offset = 0;
        int limit = 5;
        String orderBy = "rating_count,average_rating";

        //when
        List<AlbumGenreResponse> result =  albumService.getAlbums(offset, limit, orderBy);

        //then
        assert(!result.isEmpty());
        assert(result.size() == limit);

        for(AlbumGenreResponse album : result){
            resultToString(album);
        }
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
        List<AlbumGenreResponse> result =  albumService.getAlbumsWithGenre(offset, limit, orderBy, genreCode);

        //then
        assert(!result.isEmpty());
        assert(result.size() == limit);

        for(AlbumGenreResponse album : result){
            resultToString(album);
        }
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
        List<AlbumGenreResponse> result =  albumService.getAlbumsWithGenre(offset, limit, orderBy, genreCode);

        //then
        assert(!result.isEmpty());
        assert(result.size() == limit);

        for(AlbumGenreResponse album : result){
            resultToString(album);
        }
    }
}
