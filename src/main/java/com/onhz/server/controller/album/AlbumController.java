package com.onhz.server.controller.album;

import com.onhz.server.common.utils.Sort;
import com.onhz.server.dto.response.AlbumGenreResponse;
import com.onhz.server.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/albums")
public class AlbumController {
    @GetMapping("/")
    public ApiResponse<List<AlbumGenreResponse>> getAlbums(
            @PathVariable Long genreId,
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam(defaultValue = "10", required = false) int limit,
            @RequestParam(name = "order_by", defaultValue = "rating_count,average_rating") String orderBy) {
        Sort sort = new Sort(orderBy);
        List<AlbumGenreResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{genreId}")
    public ApiResponse<List<AlbumGenreResponse>> getAlbumsWithGenre(
            @PathVariable Long genreId,
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam(defaultValue = "12", required = false) int limit,
            @RequestParam(name = "order_by", defaultValue = "rating_count,average_rating") String orderBy) {
        Sort sort = new Sort(orderBy);
        List<AlbumGenreResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{albumId}")
    public ApiResponse<List<AlbumGenreResponse>> getAlbum(
            @PathVariable Long albumId) {
        List<AlbumGenreResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{albumId}/tracks")
    public ApiResponse<List<AlbumGenreResponse>> getAlbumTracks(
            @PathVariable Long albumId) {
        List<AlbumGenreResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{albumId}/reviews")
    public ApiResponse<List<AlbumGenreResponse>> getAlbumReviews(
            @PathVariable Long albumId) {
        List<AlbumGenreResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{albumId}/reviews/summary")
    public ApiResponse<List<AlbumGenreResponse>> getAlbumReviewsSummary(
            @PathVariable Long albumId) {
        List<AlbumGenreResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{albumId}/reviews/{reviewId}")
    public ApiResponse<List<AlbumGenreResponse>> getAlbumReviewDetail(
            @PathVariable Long albumId,
            @PathVariable Long reviewId){
        List<AlbumGenreResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @PostMapping("/{albumId}/reviews")
    public ApiResponse<List<AlbumGenreResponse>> postAlbumReviews(
            @PathVariable Long albumId) {
        List<AlbumGenreResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @PutMapping("/{albumId}/reviews/{reviewId}")
    public ApiResponse<List<AlbumGenreResponse>> putAlbumReview(
            @PathVariable Long albumId,
            @PathVariable Long reviewId) {
        List<AlbumGenreResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @DeleteMapping("/{albumId}/reviews/{reviewId}")
    public ApiResponse<List<AlbumGenreResponse>> deleteAlbumReview(
            @PathVariable Long albumId,
            @PathVariable Long reviewId) {
        List<AlbumGenreResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{albumId}/ratings")
    public ApiResponse<List<AlbumGenreResponse>> getAlbumRatings(
            @PathVariable Long albumId) {
        List<AlbumGenreResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{albumId}/ratings/{userId}")
    public ApiResponse<List<AlbumGenreResponse>> getAlbumUserRatings(
            @PathVariable Long albumId,
            @PathVariable Long userId) {
        List<AlbumGenreResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @PostMapping("/{albumId}/reviews/{reviewId}/like")
    public ApiResponse<List<AlbumGenreResponse>> postAlbumReviewLike(
            @PathVariable Long albumId,
            @PathVariable Long reviewId) {
        List<AlbumGenreResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }
}
