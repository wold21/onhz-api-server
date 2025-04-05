package com.onhz.server.service.user;

import com.onhz.server.common.enums.ReviewType;
import com.onhz.server.dto.response.review.ReviewResponse;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;


@SpringBootTest
@ActiveProfiles("dev")
class UserServiceTest {

    @Autowired
    private UserService userService;

    void resultToString(ReviewResponse review){
        System.out.println("- 리뷰 정보\t(id / rating / createdAt)");
        System.out.println("\t\t\t" + String.format("%d / %f / %s",
                review.getId(),
                review.getRating(),
                review.getCreatedAt()));
    }

    @Test
    @DisplayName("22번 유저의 아티스트 리뷰 리스트_최신순 정렬_offset paging")
    @Transactional
    void getReviews_offsetPaging() {
        List<ReviewResponse> offsetResult = userService.getUserReviews(22L, ReviewType.ARTIST, null, null, 2, "createdAt");
        for (ReviewResponse review : offsetResult){
            resultToString(review);
        }
    }
    @Test
    @DisplayName("22번 유저의 아티스트 리뷰 리스트_최신순 정렬_cursor paging_초기 데이터 호출")
    @Transactional
    void getReviews_cursorPaging_firstPage() {
        List<ReviewResponse> cursorResult = userService.getUserReviews(22L, ReviewType.ARTIST, null, null, 2, "createdAt");
        for (ReviewResponse review : cursorResult){
            resultToString(review);
        }
    }

    @Test
    @DisplayName("22번 유저의 아티스트 리뷰 리스트_최신 순 정렬_별점 2025-03-10T22:51:57.904404, review_id 264 다음의 데이터 2개")
    @Transactional
    void getReviews_cursorPaging_createdAt() {
        Long userId = 22L;
        ReviewType reviewType = ReviewType.ARTIST;
        Long cursor = 264L;
        String lastOrderValue = "2025-03-10T22:51:57.904404";
        int limit = 2;
        String orderBy = "createdAt";

        List<ReviewResponse> cursorResult = userService.getUserReviews(userId, reviewType, cursor, lastOrderValue, limit, orderBy);

        for (ReviewResponse review : cursorResult){
            resultToString(review);
        }
    }


    @Test
    @DisplayName("22번 유저의 아티스트 리뷰 리스트_별점 높은 순 정렬_별점 4.5점 이하의 review_id 27 다음의 데이터 2개")
    @Transactional
    void getReviews_cursorPaging_rating() {
        Long userId = 22L;
        ReviewType reviewType = ReviewType.ARTIST;
        Long cursor = 27L;
        String lastOrderValue = "4.5";
        int limit = 2;
        String orderBy = "rating";

        List<ReviewResponse> cursorResult = userService.getUserReviews(userId, reviewType, cursor, lastOrderValue, limit, orderBy);

        for (ReviewResponse review : cursorResult){
            resultToString(review);
        }
    }


//    @Test
//    @DisplayName("유저 삭제")
//    @Transactional
//    void deleteUser() {
//        Long userId = 14L;
//        userService.deleteUserById(userId);
//    }

        @Test
        @DisplayName("유저별 평균 별점 및 분포도 조회")
        @Transactional
        void deleteUser() {
            Long userId = 20L;
            userService.getUserRatings(userId);
        }
}