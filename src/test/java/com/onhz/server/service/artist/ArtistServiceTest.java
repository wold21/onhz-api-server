package com.onhz.server.service.artist;


import com.onhz.server.dto.response.AlbumGenreResponse;
import com.onhz.server.dto.response.ArtistResponse;
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
public class ArtistServiceTest {
    @Autowired
    private ArtistService artistService;

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
    void getArtist(){
        //given
        int offset = 0;
        int limit = 5;
        String orderBy = "created_at";

        //when
        List<ArtistResponse> result =  artistService.getArtist(offset, limit, orderBy);

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
        List<ArtistResponse> result =  artistService.getArtist(offset, limit, orderBy);

        //then
        assert(!result.isEmpty());
        assert(result.size() == limit);

        for(ArtistResponse artist : result){
            resultToString(artist);
        }
    }
}
