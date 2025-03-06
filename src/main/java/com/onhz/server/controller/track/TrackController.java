package com.onhz.server.controller.track;

import com.onhz.server.common.utils.Sort;
import com.onhz.server.dto.response.ApiResponse;
import com.onhz.server.dto.response.TrackResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/tracks")
public class TrackController {
    @GetMapping("/{trackId}")
    public ApiResponse<TrackResponse> getTracks(
            @PathVariable Long trackId) {
        TrackResponse result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{trackId}/reviews")
    public ApiResponse<TrackResponse> getTrackReviews(
            @PathVariable Long trackId,
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam(defaultValue = "10", required = false) int limit,
            @RequestParam(name = "order_by", defaultValue = "created_at") String orderBy){
        Sort sort = new Sort(orderBy);
        TrackResponse result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{trackId}/reviews/{reviewId}")
    public ApiResponse<TrackResponse> getTrackReviewDetail(
            @PathVariable Long trackId,
            @PathVariable Long reviewId){
        TrackResponse result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @PostMapping("/{trackId}/reviews")
    public ApiResponse<TrackResponse> postTrackReview(
            @PathVariable Long trackId,
            @PathVariable Long reviewId){
        TrackResponse result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @PutMapping("/{trackId}/reviews/{reviewId}")
    public ApiResponse<TrackResponse> putTrackReview(
            @PathVariable Long trackId,
            @PathVariable Long reviewId){
        TrackResponse result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @DeleteMapping("/{trackId}/reviews/{reviewId}")
    public ApiResponse<TrackResponse> deleteTrackReview(
            @PathVariable Long trackId,
            @PathVariable Long reviewId){
        TrackResponse result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @PostMapping("/{trackId}/reviews/{reviewId}/like")
    public ApiResponse<TrackResponse> postTrackReviewLike(
            @PathVariable Long trackId,
            @PathVariable Long reviewId){
        TrackResponse result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{trackId}/ratings/{userId}")
    public ApiResponse<TrackResponse> getTrackUserRatings(
            @PathVariable Long trackId,
            @PathVariable Long userId){
        TrackResponse result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }
}
