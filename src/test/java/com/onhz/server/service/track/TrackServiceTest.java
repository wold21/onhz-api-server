package com.onhz.server.service.track;

import com.onhz.server.dto.response.album.AlbumDetailResponse;
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
public class TrackServiceTest {
    @Autowired
    private TrackService trackService;

    @Test
    @DisplayName("앨범별 트랙 조회")
    void getTracksByAlbumId(){
        //given
        Long albumId = 3L;

        //when
        List<TrackResponse> result =  trackService.getTracksByAlbumId(albumId);

        //then
        assert(!result.isEmpty());
    }
}
