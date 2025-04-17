package com.onhz.server.common.schedule;

import com.onhz.server.service.schedule.UserScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserScheduler {
    private final UserScheduleService userScheduleService;

    @Scheduled(cron = "0 */5 * * * ?")
//    @Scheduled(cron = "0 0 3 * * ?")
    public void deleteExpiredSessions() {
        log.info("deleteExpiredSessions start");
        userScheduleService.deleteExpiredSessions();
        log.info("deleteExpiredSessions end");
    }

    @Scheduled(cron = "0 */5 * * * ?")
//    @Scheduled(cron = "0 0 3 * * ?")
    public void deleteDelUsers() {
        log.info("deleteDeletedUsers start");
        userScheduleService.deleteDelUsers();
        log.info("deleteDeletedUsers end");
    }
}
