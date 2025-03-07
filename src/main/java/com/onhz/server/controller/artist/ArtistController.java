package com.onhz.server.controller.artist;

import com.onhz.server.common.utils.Sort;
import com.onhz.server.dto.response.ApiResponse;
import com.onhz.server.dto.response.ArtistResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/artists")
public class ArtistController {
    @GetMapping("/")
    public ApiResponse<List<ArtistResponse>> getArtists(
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam(defaultValue = "10", required = false) int limit,
            @RequestParam(name = "order_by", defaultValue = "rating_count,average_rating") String orderBy) {
        Sort sort = new Sort(orderBy);
        List<ArtistResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{artistId}")
    public ApiResponse<ArtistResponse> getArtist(
            @PathVariable Long artistId) {
        ArtistResponse result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{artistId}/reviews")
    public ApiResponse<List<ArtistResponse>> getArtistReviews(
            @PathVariable Long artistId,
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam(defaultValue = "10", required = false) int limit,
            @RequestParam(name = "order_by", defaultValue = "rating_count,average_rating") String orderBy) {
        Sort sort = new Sort(orderBy);
        List<ArtistResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{artistId}/reviews/{reviewId}")
    public ApiResponse<List<ArtistResponse>> getArtistReviewDetail(
            @PathVariable Long artistId,
            @PathVariable Long reviewId) {
        List<ArtistResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @PostMapping("/{artistId}/reviews")
    public ApiResponse<List<ArtistResponse>> postArtistReview(
            @PathVariable Long artistId,
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam(defaultValue = "10", required = false) int limit,
            @RequestParam(name = "order_by", defaultValue = "created_at") String orderBy) {
        Sort sort = new Sort(orderBy);
        List<ArtistResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @PutMapping("/{artistId}/reviews/{reviewId}")
    public ApiResponse<List<ArtistResponse>> putArtistReview(
            @PathVariable Long artistId,
            @PathVariable Long reviewId) {
        List<ArtistResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @DeleteMapping("/{artistId}/reviews/{reviewId}")
    public ApiResponse<List<ArtistResponse>> deleteArtistReview(
            @PathVariable Long artistId,
            @PathVariable Long reviewId) {
        List<ArtistResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @PostMapping("/{artistId}/reviews/{reviewId}/like")
    public ApiResponse<List<ArtistResponse>> postArtistReviewLike(
            @PathVariable Long artistId,
            @PathVariable Long reviewId) {
        List<ArtistResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{artistId}/ratings/{userId}")
    public ApiResponse<List<ArtistResponse>> getArtistUserRatings(
            @PathVariable Long artistId,
            @PathVariable Long userId) {
        List<ArtistResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{artistId}/tracks")
    public ApiResponse<List<ArtistResponse>> getArtistTracks(
            @PathVariable Long artistId,
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam(defaultValue = "10", required = false) int limit,
            @RequestParam(name = "order_by", defaultValue = "rating, created_at") String orderBy) {
        Sort sort = new Sort(orderBy);
        List<ArtistResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{artistId}/albums")
    public ApiResponse<List<ArtistResponse>> getArtistAlbums(
            @PathVariable Long artistId,
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam(defaultValue = "10", required = false) int limit,
            @RequestParam(name = "order_by", defaultValue = "rating, created_at") String orderBy) {
        Sort sort = new Sort(orderBy);
        List<ArtistResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }
}
