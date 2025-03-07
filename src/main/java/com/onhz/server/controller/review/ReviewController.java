package com.onhz.server.controller.review;

import com.onhz.server.common.enums.Review;
import com.onhz.server.common.utils.Sort;
import com.onhz.server.dto.request.ReviewRequest;
import com.onhz.server.dto.response.AlbumGenreResponse;
import com.onhz.server.dto.response.ApiResponse;
import com.onhz.server.dto.response.ReviewResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/reviews")
public class ReviewController {

    @GetMapping("/")
    public ApiResponse<List<ReviewResponse>> getReviews(
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam(defaultValue = "10", required = false) int limit,
            @RequestParam(name = "order_by", defaultValue = "created_at") String orderBy) {
        Sort sort = new Sort(orderBy);
        List<ReviewResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{reviewId}")
    public ApiResponse<ReviewResponse> getReviewDetail(
            @PathVariable Long reviewId) {
        ReviewResponse result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{reviewType}/{entityId}")
    public ApiResponse<List<ReviewResponse>> getReviews(
            @PathVariable Review reviewType,
            @PathVariable Long entityId,
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam(defaultValue = "10", required = false) int limit,
            @RequestParam(name = "order_by", defaultValue = "created_at") String orderBy) {
        Sort sort = new Sort(orderBy);
        List<ReviewResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{reviewType}/{entityId}/users/{userId}")
    public ApiResponse<List<ReviewResponse>> getReviewWithUserId(
            @PathVariable Review reviewType,
            @PathVariable Long entityId,
            @PathVariable Long userId){
        List<ReviewResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @PostMapping("/{reviewType}/{entityId}")
    public ApiResponse createReview(
            @PathVariable Review reviewType,
            @PathVariable Long entityId,
            @RequestBody ReviewRequest requestDto){
        List<ReviewResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @PutMapping("/{reviewType}/{entityId}/{reviewId}")
    public ApiResponse<Void> putReview(
            @PathVariable Review reviewType,
            @PathVariable Long entityId,
            @PathVariable Long reviewId,
            @RequestBody ReviewRequest requestDto) {
        return ApiResponse.success(HttpStatus.OK, "success", null);
    }

    @DeleteMapping("/{reviewType}/{entityId}/{reviewId}")
    public ApiResponse<Void> deleteReview(
            @PathVariable Review reviewType,
            @PathVariable Long entityId,
            @PathVariable Long reviewId) {
        return ApiResponse.success(HttpStatus.OK, "success", null);
    }

    @PostMapping("/{reviewType}/{entityId}/{reviewId}/like")
    public ApiResponse<Void> postReviewLike(
            @PathVariable Review reviewType,
            @PathVariable Long entityId,
            @PathVariable Long reviewId) {
        return ApiResponse.success(HttpStatus.OK, "success", null);
    }

    @GetMapping("/{reviewType}/{entityId}/{reviewId}/ratings/{userId}")
    public ApiResponse<Integer> getUserRatings(
            @PathVariable Review reviewType,
            @PathVariable Long entityId,
            @PathVariable Long reviewId,
            @PathVariable Long userId) {
        Integer rating = null;
        return ApiResponse.success(HttpStatus.OK, "success", rating);
    }


    @GetMapping("/{reviewType}/{entityId}/summary")
    public ApiResponse<List<AlbumGenreResponse>> getReviewsSummary(
            @PathVariable Review reviewType,
            @PathVariable Long entityId) {
        List<AlbumGenreResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }
}