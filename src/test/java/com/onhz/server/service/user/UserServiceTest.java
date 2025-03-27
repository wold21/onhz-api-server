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

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("dev")
class UserServiceTest {

    @Autowired
    private UserService userService;

    void resultToString(ReviewResponse review){
        System.out.println("- 리뷰 정보\t(id / content / rating / entity_id / review_type / like_count / is_like)");
        System.out.println("\t\t\t" + String.format("%d / %s / %f / %d / %s / %d / %s",
                review.getId(),
                review.getContent(),
                review.getRating(),
                review.getEntityId(),
                review.getReviewType(),
                review.getLikeCount(),
                review.getIsLiked()));
    }

    @Test
    @DisplayName("22번 유저의 아티스트 리뷰 리스트_최신순 정렬_offset paging")
    @Transactional
    void getReviews_offsetPaging() {
        List<ReviewResponse> offsetResult = userService.getUserReviews(22L, ReviewType.ARTIST,false, null, null, 0 , 2, "created_at");
        for (ReviewResponse review : offsetResult){
            resultToString(review);
        }
    }
    @Test
    @DisplayName("22번 유저의 아티스트 리뷰 리스트_최신순 정렬_cursor paging_초기 데이터 호출")
    @Transactional
    void getReviews_cursorPaging_firstPage() {
        List<ReviewResponse> cursorResult = userService.getUserReviews(22L, ReviewType.ARTIST,true, null, null, 0 , 2, "created_at");
        for (ReviewResponse review : cursorResult){
            resultToString(review);
        }
    }

    @Test
    @DisplayName("22번 유저의 아티스트 리뷰 리스트_별점 높은 순 정렬_별점 4.5점 이하의 review_id 27 다음의 데이터 2개")
    @Transactional
    void getReviews_cursorPaging() {
        Long userId = 22L;
        ReviewType reviewType = ReviewType.ARTIST;
        Long cursor = 27L;
        String cursorValue = "4";
        int offset = 0;
        int limit = 2;
        String orderBy = "rating";

        List<ReviewResponse> cursorResult = userService.getUserReviews(userId, reviewType,true, cursor, cursorValue, offset , limit, orderBy);

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
}