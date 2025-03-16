package com.onhz.server.controller.review;

import com.onhz.server.common.enums.ReviewType;

import com.onhz.server.dto.request.ReviewRequest;
import com.onhz.server.dto.response.ApiResponse;
import com.onhz.server.dto.response.ReviewLatestResponse;
import com.onhz.server.dto.response.ReviewResponse;
import com.onhz.server.entity.user.UserEntity;
import com.onhz.server.service.review.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("")
    @Operation(summary = "(최신) 리뷰 리스트", description = "")
    public ApiResponse<List<ReviewLatestResponse>> getReviews(
            @RequestParam(name = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam(name = "limit", defaultValue = "10", required = false) int limit,
            @RequestParam(name = "order_by", defaultValue = "created_at") String orderBy) {
        List<ReviewLatestResponse> result = reviewService.getReviews(offset, limit, orderBy);
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{reviewId}")
    @Operation(summary = "리뷰 상세 정보 조회", description = "")
    public ApiResponse<ReviewResponse> getReviewDetail(
            @PathVariable(name="reviewId") Long reviewId,
            @AuthenticationPrincipal UserEntity user){
        ReviewResponse result = reviewService.getReviewDetail(user, reviewId);
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{reviewType}/{entityId}")
    @Operation(summary = "특정 아티스트/앨범/트랙 리뷰 리스트", description = "")
    public ApiResponse<List<ReviewResponse>> getEntityReviews(
            @Parameter(description = "리뷰 유형",
                    schema = @Schema(implementation = ReviewType.class))
            @PathVariable(name="reviewType") ReviewType reviewType,
            @Parameter(description = "리뷰 대상 ID (album_id or artist_id or track_id)")
            @PathVariable(name="entityId") Long entityId,
            @RequestParam(name="offset", defaultValue = "0", required = false) int offset,
            @RequestParam(name="limit", defaultValue = "10", required = false) int limit,
            @RequestParam(name = "order_by", defaultValue = "created_at") String orderBy,
            @AuthenticationPrincipal UserEntity user) {
        List<ReviewResponse> result = reviewService.getEntityReviews(user, reviewType, entityId, offset, limit, orderBy);
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @PostMapping("/{reviewType}/{entityId}")
    @Operation(summary = "리뷰 추가", description = "")
    public ApiResponse createReview(
            @Parameter(description = "리뷰 유형",
                    schema = @Schema(implementation = ReviewType.class))
            @PathVariable(name="reviewType") ReviewType reviewType,
            @Parameter(description = "리뷰 대상 ID (album_id or artist_id or track_id)")
            @PathVariable(name="entityId") Long entityId,
            @RequestBody ReviewRequest requestDto,
            @AuthenticationPrincipal UserEntity user){
        ReviewResponse result = reviewService.createReview(user, reviewType, entityId, requestDto);
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @PutMapping("/{reviewType}/{entityId}/{reviewId}")
    @Operation(summary = "리뷰 수정", description = "")
    public ApiResponse<Void> putReview(
            @Parameter(description = "리뷰 유형",
                    schema = @Schema(implementation = ReviewType.class))
            @PathVariable(name="reviewType") ReviewType reviewType,
            @Parameter(description = "리뷰 대상 ID (album_id or artist_id or track_id)")
            @PathVariable(name="entityId") Long entityId,
            @PathVariable(name="reviewId")  Long reviewId,
            @RequestBody ReviewRequest requestDto) {
        reviewService.updateReview(reviewId, requestDto);
        return ApiResponse.success(HttpStatus.OK, "success", null);
    }

    @DeleteMapping("/{reviewType}/{entityId}/{reviewId}")
    @Operation(summary = "리뷰 삭제", description = "")
    public ApiResponse<Void> deleteReview(
            @Parameter(description = "리뷰 유형",
                    schema = @Schema(implementation = ReviewType.class))
            @PathVariable(name="reviewType") ReviewType reviewType,
            @Parameter(description = "리뷰 대상 ID (album_id or artist_id or track_id)")
            @PathVariable(name="entityId") Long entityId,
            @PathVariable(name="reviewId")  Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ApiResponse.success(HttpStatus.OK, "success", null);
    }

    @PostMapping("/{reviewType}/{entityId}/{reviewId}/like")
    @Operation(summary = "리뷰 좋아요 추가/삭제", description = "")
    public ApiResponse<Void> postReviewLike(
            @Parameter(description = "리뷰 유형",
                    schema = @Schema(implementation = ReviewType.class))
            @PathVariable(name="reviewType") ReviewType reviewType,
            @Parameter(description = "리뷰 대상 ID (album_id or artist_id or track_id)")
            @PathVariable(name="entityId") Long entityId,
            @PathVariable(name="reviewId")  Long reviewId,
            @AuthenticationPrincipal UserEntity user){
        reviewService.toggleLike(user, reviewId);
        return ApiResponse.success(HttpStatus.OK, "success", null);
    }

}