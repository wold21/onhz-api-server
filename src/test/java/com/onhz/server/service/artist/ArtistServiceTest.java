package com.onhz.server.service.artist;


import com.onhz.server.dto.response.*;
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
public class ArtistServiceTest {
    @Autowired
    private ArtistService artistService;

    void resultToStringWithAlbum(ArtistAlbumResponse artistAlbum){
        System.out.println("- 아티스트 정보\t(artistId / name / country / createdAt / profilePath)");
        System.out.println("\t\t\t" + String.format("%d / %s / %s / %s / %s",
                artistAlbum.getId(),
                artistAlbum.getName(),
                artistAlbum.getCountry(),
                artistAlbum.getCreatedAt(),
                artistAlbum.getProfilePath()));
        System.out.println("- 앨범 정보\t(trackId / title / releaseDate)");
        for(AlbumResponse album : artistAlbum.getAlbums()){
            System.out.println("\t\t\t" + String.format("%d / %s / %s",
                    album.getId(),
                    album.getTitle(),
                    album.getReleaseDate()));
        }
        System.out.println("\n");
    }

    void resultToStringWithTracks(ArtistTrackResponse artistTrack){
        System.out.println("- 아티스트 정보\t(artistId / name / country / createdAt / profilePath)");
        System.out.println("\t\t\t" + String.format("%d / %s / %s / %s / %s",
                artistTrack.getId(),
                artistTrack.getName(),
                artistTrack.getCountry(),
                artistTrack.getCreatedAt(),
                artistTrack.getProfilePath()));
        System.out.println("- 노래 정보\t(trackId / name / duration)");
        for(TrackResponse track : artistTrack.getTracks()){
            System.out.println("\t\t\t" + String.format("%d / %s / %s",
                    track.getId(),
                    track.getTrackName(),
                    track.getDuration()));
        }
        System.out.println("\n");
    }
    void resultToString(ArtistResponse artist){
        System.out.println("- 아티스트 정보\t(artistId / name / country / createdAt / profilePath)");
        System.out.println("\t\t\t" + String.format("%d / %s / %s / %s / %s",
                artist.getId(),
                artist.getName(),
                artist.getCountry(),
                artist.getCreatedAt(),
                artist.getProfilePath()));
        System.out.println("\n");
    }

    @Test
    @DisplayName("아티스트 페이징 조회")
    void getArtists(){
        //given
        int offset = 0;
        int limit = 5;
        String orderBy = "created_at";

        //when
        List<ArtistResponse> result =  artistService.getArtists(offset, limit, orderBy);

        //then
        assert(!result.isEmpty());
        assert(result.size() == limit);

        for(ArtistResponse artist : result){
            resultToString(artist);
        }
    }

    @Test
    @DisplayName("인기 아티스트 페이징 조회")
    void getAlbumsWithRating(){
        //given
        int offset = 0;
        int limit = 5;
        String orderBy = "rating_count,average_rating";

        //when
        List<ArtistResponse> result =  artistService.getArtists(offset, limit, orderBy);

        //then
        assert(!result.isEmpty());
        assert(result.size() == limit);

        for(ArtistResponse artist : result){
            resultToString(artist);
        }
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
        resultToString(result);
    }

    @Test
    @DisplayName("아티스트별 트랙 페이징 조회")
    void getArtistWithTracks(){
        //given
        int offset = 0;
        int limit = 5;
        Long artistId = 1L;
        String orderBy = "created_at";

        //when
        ArtistTrackResponse result =  artistService.getArtistWithTracks(artistId, offset, limit, orderBy);

        //then
        assert(result != null);
        resultToStringWithTracks(result);
    }

    @Test
    @DisplayName("아티스트별 인기 트랙 페이징 조회")
    void getArtistWithRatingTracks(){
        //given
        int offset = 0;
        int limit = 5;
        Long artistId = 1L;
        String orderBy = "rating_count,average_rating";

        //when
        ArtistTrackResponse result =  artistService.getArtistWithTracks(artistId, offset, limit, orderBy);

        //then
        assert(result != null);
        resultToStringWithTracks(result);
    }

    @Test
    @DisplayName("아티스트별 앨범 페이징 조회")
    void getArtistWithAlbum(){
        //given
        int offset = 0;
        int limit = 5;
        Long artistId = 1L;
        String orderBy = "created_at";

        //when
        ArtistAlbumResponse result =  artistService.getArtistWithAlbums(artistId, offset, limit, orderBy);

        //then
        assert(result != null);
        resultToStringWithAlbum(result);
    }

    @Test
    @DisplayName("아티스트별 인기 앨범 페이징 조회")
    void getArtistWithRatingAlbum(){
        //given
        int offset = 0;
        int limit = 5;
        Long artistId = 1L;
        String orderBy = "rating_count,average_rating";

        //when
        ArtistAlbumResponse result =  artistService.getArtistWithAlbums(artistId, offset, limit, orderBy);

        //then
        assert(result != null);
        resultToStringWithAlbum(result);
    }
}
