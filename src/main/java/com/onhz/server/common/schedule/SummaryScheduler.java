package com.onhz.server.common.schedule;

import com.onhz.server.service.schedule.AlbumScheduleService;
import com.onhz.server.service.schedule.ArtistScheduleService;
import com.onhz.server.service.schedule.TrackScheduleService;
import com.onhz.server.service.schedule.UserScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SummaryScheduler {
    private final UserScheduleService userScheduleService;
    private final ArtistScheduleService artistScheduleService;
    private final AlbumScheduleService albumScheduleService;
    private final TrackScheduleService trackScheduleService;

//    @Scheduled(cron = "0 0 0/4 * * ?")
    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    public void updateUserSummary() {
        log.info("updateUserSummary start");
        userScheduleService.ratings();
        log.info("updateUserSummary end");
    }

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    public void updateArtistSummary() {
        log.info("updateArtistSummary start");
        artistScheduleService.ratings();
        log.info("updateArtistSummary end");
    }

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    public void updateAlbumSummary() {
        log.info("updateAlbumSummary start");
        albumScheduleService.ratings();
        log.info("updateAlbumSummary end");
    }

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    public void updateTrackSummary() {
        log.info("updateTrackSummary start");
        trackScheduleService.ratings();
        log.info("updateTrackSummary end");
    }
}
