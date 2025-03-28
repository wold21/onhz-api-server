package com.onhz.server.service.artist;


import com.onhz.server.dto.response.album.AlbumResponse;
import com.onhz.server.dto.response.artist.ArtistResponse;
import com.onhz.server.dto.response.track.TrackResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.sound.midi.Track;
import java.util.List;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional(readOnly = true)
public class ArtistServiceTest {
    @Autowired
    private ArtistService artistService;

    @Test
    @DisplayName("아티스트 페이징 조회")
    void getArtists(){
        //given
        int offset = 0;
        int limit = 5;
        String orderBy = "createdAt";

        //when
        List<ArtistResponse> result =  artistService.getArtists(offset, limit, orderBy);

        //then
        assert(!result.isEmpty());
        assert(result.size() == limit);
    }

    @Test
    @DisplayName("인기 아티스트 페이징 조회")
    void getAlbumsWithRating(){
        //given
        int offset = 0;
        int limit = 5;
        String orderBy = "ratingCount,averageRating";

        //when
        List<ArtistResponse> result =  artistService.getArtists(offset, limit, orderBy);

        //then
        assert(!result.isEmpty());
        assert(result.size() == limit);
    }

    @Test
    @DisplayName("아티스트 단일 조회")
    void getArtist(){
        //given
        Long artistId = 1L;

        //when
        ArtistResponse result =  artistService.getArtist(artistId);

        //then
        assert(result != null);
    }

    @Test
    @DisplayName("아티스트별 트랙 페이징 조회")
    void getArtistWithTracks(){
        //given
        int offset = 0;
        int limit = 5;
        Long artistId = 1L;
        String orderBy = "createdAt";

        //when
        List<TrackResponse> result =  artistService.getArtistWithTracks(artistId, offset, limit, orderBy);

        //then
        assert(result != null);
    }

    @Test
    @DisplayName("아티스트별 인기 트랙 페이징 조회")
    void getArtistWithRatingTracks(){
        //given
        int offset = 0;
        int limit = 5;
        Long artistId = 1L;
        String orderBy = "ratingCount,averageRating";

        //when
        List<TrackResponse> result =  artistService.getArtistWithTracks(artistId, offset, limit, orderBy);

        //then
        assert(result != null);
    }

    @Test
    @DisplayName("아티스트별 앨범 페이징 조회")
    void getArtistWithAlbum(){
        //given
        int offset = 0;
        int limit = 5;
        Long artistId = 1L;
        String orderBy = "createdAt";

        //when
        List<AlbumResponse> result =  artistService.getArtistWithAlbums(artistId, offset, limit, orderBy);

        //then
        assert(result != null);
    }

    @Test
    @DisplayName("아티스트별 인기 앨범 페이징 조회")
    void getArtistWithRatingAlbum(){
        //given
        int offset = 0;
        int limit = 5;
        Long artistId = 1L;
        String orderBy = "ratingCount,averageRating";

        //when
        List<AlbumResponse> result =  artistService.getArtistWithAlbums(artistId, offset, limit, orderBy);

        //then
        assert(result != null);
    }
}
