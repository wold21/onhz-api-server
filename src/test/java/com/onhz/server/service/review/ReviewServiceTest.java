package com.onhz.server.service.review;

import com.onhz.server.common.enums.Review;
import com.onhz.server.dto.request.ReviewRequest;
import com.onhz.server.dto.response.ReviewResponse;
import com.onhz.server.entity.review.ReviewEntity;
import com.onhz.server.entity.user.UserEntity;
import com.onhz.server.repository.ReviewRepository;
import com.onhz.server.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;


@SpringBootTest
@ActiveProfiles("dev")
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {

        testUser = userRepository.findByEmail("Layne.Grady@hotmail.com")
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    void resultToString(ReviewResponse review){
        System.out.println("- 리뷰 정보\t(id / content / rating / entity_id / review_type)");
        System.out.println("\t\t\t" + String.format("%d / %s / %f / %d / %s",
                review.getId(),
                review.getContent(),
                review.getRating(),
                review.getEntityId(),
                review.getReview()));
    }

    @Test
    @DisplayName("특정 아티스트/앨범/트랙 리뷰 리스트")
    @Transactional
    void getReviews() {
        List<ReviewResponse> result = reviewService.getReviews(Review.ARTIST, 1L, 0, 10, "created_at");
        for (ReviewResponse review : result){
            resultToString(review);
        }
    }

    @Test
    @DisplayName("특정 아티스트/앨범/트랙 리뷰 리스트")
    @Transactional
    void getReviewDetail() {
        ReviewResponse review = reviewService.getReviewDetail(3L);
        resultToString(review);
    }
    void getReviewDetail(Long id) {
        ReviewResponse review = reviewService.getReviewDetail(id);
        resultToString(review);
    }

    @Test
    @DisplayName("리뷰 등록 검증_content&rating")
    @Transactional
    @Rollback(false)
    void createReviewTest_content_rating() {
        testUser = entityManager.merge(testUser);

        ReviewRequest request = new ReviewRequest("이 앨범 최고예요!", 4.5);
        ReviewResponse response = reviewService.createReview(testUser, Review.ALBUM, 100L, request);

        entityManager.flush();
        entityManager.clear();

        Optional<ReviewEntity> savedReview = reviewRepository.findById(response.getId());
        savedReview.ifPresent(review -> {
            System.out.println("- 등록 완료 !\nID: " + review.getId());
        });
    }
    @Test
    @DisplayName("리뷰 등록 검증_content")
    @Transactional
    @Rollback(false)
    void createReviewTest_content() {
        testUser = entityManager.merge(testUser);

        ReviewRequest request = new ReviewRequest("이 앨범 최고", null);
        ReviewResponse response = reviewService.createReview(testUser, Review.ALBUM, 100L, request);

        entityManager.flush();
        entityManager.clear();

        Optional<ReviewEntity> savedReview = reviewRepository.findById(response.getId());
        savedReview.ifPresent(review -> {
            System.out.println("- 등록 완료 !\nID: " + review.getId());
        });
    }
    @Test
    @DisplayName("리뷰 등록 검증_rating")
    @Transactional
    @Rollback(false)
    void createReviewTest_rating() {
        testUser = entityManager.merge(testUser);

        ReviewRequest request = new ReviewRequest(null, 3.0);
        ReviewResponse response = reviewService.createReview(testUser, Review.ALBUM, 100L, request);

        entityManager.flush();
        entityManager.clear();

        Optional<ReviewEntity> savedReview = reviewRepository.findById(response.getId());
        savedReview.ifPresent(review -> {
            System.out.println("- 등록 완료 !\nID: " + review.getId());
        });
    }

    @Test
    @DisplayName("리뷰 등록 검증_동일 사용자 등록 불가")
    @Transactional
    @Rollback(false)
    void createReviewTest_() {
        testUser = entityManager.merge(testUser);

        ReviewRequest request = new ReviewRequest(null, 3.0);
        ReviewResponse response = reviewService.createReview(testUser, Review.ARTIST, 81L, request);

        entityManager.flush();
        entityManager.clear();

        Optional<ReviewEntity> savedReview = reviewRepository.findById(response.getId());
        savedReview.ifPresent(review -> {
            System.out.println("- 등록 완료 !\nID: " + review.getId());
        });
    }

    @Test
    @DisplayName("리뷰 수정 검증_content&rating")
    @Transactional
    @Rollback(false)
    public void updateReview_content_rating() {
        Long reviewId = 3L;
        ReviewRequest request = new ReviewRequest("우우~~", 1.0);
        reviewService.updateReview(reviewId, request);
        System.out.println("- 수정 완료 !");
        getReviewDetail(reviewId);
    }
    @Test
    @DisplayName("리뷰 수정 검증_content")
    @Transactional
    @Rollback(false)
    public void updateReview_content() {
        Long reviewId = 3L;
        ReviewRequest request = new ReviewRequest("GOOD", null);
        reviewService.updateReview(reviewId, request);
        System.out.println("- 수정 완료 !");
        getReviewDetail(reviewId);
    }
    @Test
    @DisplayName("리뷰 수정 검증_rating")
    @Transactional
    @Rollback(false)
    public void updateReview_rating() {
        Long reviewId = 3L;
        ReviewRequest request = new ReviewRequest(null,5.0);
        reviewService.updateReview(reviewId, request);
        System.out.println("- 수정 완료 !");
        getReviewDetail(reviewId);
    }


    @Test
    @DisplayName("리뷰 삭제 검증")
    @Transactional
    @Rollback(false)
    public void deleteReview(Long reviewId) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));
        System.out.println("- 삭제 완료 !");
        reviewRepository.delete(review);
    }

}