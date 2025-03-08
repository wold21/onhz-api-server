package com.onhz.server.controller.review;

import com.onhz.server.common.enums.Review;

import com.onhz.server.dto.request.ReviewRequest;
import com.onhz.server.dto.response.ApiResponse;
import com.onhz.server.dto.response.ReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    @GetMapping("")
    @Operation(summary = "(최신) 리뷰 리스트", description = "")
    public ApiResponse<List<ReviewResponse>> getReviews(
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam(defaultValue = "10", required = false) int limit,
            @RequestParam(name = "order_by", defaultValue = "created_at") String orderBy) {

        List<ReviewResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{reviewId}")
    @Operation(summary = "리뷰 상세 정보 조회", description = "")
    public ApiResponse<ReviewResponse> getReviewDetail(
            @PathVariable Long reviewId) {
        ReviewResponse result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{reviewType}/{entityId}")
    @Operation(summary = "타입별 리뷰 조회", description = "")
    public ApiResponse<List<ReviewResponse>> getReviews(
            @PathVariable Review reviewType,
            @PathVariable Long entityId,
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam(defaultValue = "10", required = false) int limit,
            @RequestParam(name = "order_by", defaultValue = "created_at") String orderBy) {

        List<ReviewResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @PostMapping("/{reviewType}/{entityId}")
    @Operation(summary = "리뷰 추가", description = "")
    public ApiResponse createReview(
            @PathVariable Review reviewType,
            @PathVariable Long entityId,
            @RequestBody ReviewRequest requestDto){
        List<ReviewResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @PutMapping("/{reviewType}/{entityId}/{reviewId}")
    @Operation(summary = "리뷰 수정", description = "")
    public ApiResponse<Void> putReview(
            @PathVariable Review reviewType,
            @PathVariable Long entityId,
            @PathVariable Long reviewId,
            @RequestBody ReviewRequest requestDto) {
        return ApiResponse.success(HttpStatus.OK, "success", null);
    }

    @DeleteMapping("/{reviewType}/{entityId}/{reviewId}")
    @Operation(summary = "리뷰 삭제", description = "")
    public ApiResponse<Void> deleteReview(
            @PathVariable Review reviewType,
            @PathVariable Long entityId,
            @PathVariable Long reviewId) {
        return ApiResponse.success(HttpStatus.OK, "success", null);
    }

    @PostMapping("/{reviewType}/{entityId}/{reviewId}/like")
    @Operation(summary = "리뷰 좋아요 추가/삭제", description = "")
    public ApiResponse<Void> postReviewLike(
            @PathVariable Review reviewType,
            @PathVariable Long entityId,
            @PathVariable Long reviewId) {
        return ApiResponse.success(HttpStatus.OK, "success", null);
    }

}