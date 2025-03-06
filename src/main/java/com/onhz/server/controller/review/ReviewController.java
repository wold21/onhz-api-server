package com.onhz.server.controller.review;

import com.onhz.server.common.enums.Review;
import com.onhz.server.common.utils.Sort;
import com.onhz.server.dto.response.ApiResponse;
import com.onhz.server.dto.response.ReviewResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/review")
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

    @GetMapping("/{reviewType}/{entityId}/{userId}")
    public ApiResponse<List<ReviewResponse>> getReviewWithUserId(
            @PathVariable Review reviewType,
            @PathVariable Long entityId,
            @PathVariable Long userId){
        List<ReviewResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

}