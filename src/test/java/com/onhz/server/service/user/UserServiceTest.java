package com.onhz.server.service.user;

import com.onhz.server.common.enums.ReviewType;
import com.onhz.server.dto.response.review.ReviewResponse;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

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
    @DisplayName("리뷰 리스트_최신순 정렬")
    @Transactional
    void getReviews() {
        List<ReviewResponse> result = userService.getUserReviews(10L, ReviewType.ARTIST,0 , 10, "created_at");
        for (ReviewResponse review : result){
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