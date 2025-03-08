package com.onhz.server.controller.album;

import com.onhz.server.common.utils.Sort;
import com.onhz.server.dto.response.AlbumGenreResponse;
import com.onhz.server.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/albums")
public class AlbumController {
    @GetMapping("/")
    @Operation(summary = "앨범 리스트조회", description = "")
    public ApiResponse<List<AlbumGenreResponse>> getAlbums(
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam(defaultValue = "10", required = false) int limit,
            @RequestParam(name = "order_by", defaultValue = "rating_count,average_rating") String orderBy) {
        Sort sort = new Sort(orderBy);
        List<AlbumGenreResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{genreId}")
    @Operation(summary = "장르별 앨범 조회", description = "")
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
    @Operation(summary = "앨범 상세 조회", description = "")
    public ApiResponse<List<AlbumGenreResponse>> getAlbum(
            @PathVariable Long albumId) {
        List<AlbumGenreResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{albumId}/tracks")
    @Operation(summary = "앨범별 트랙 조회", description = "")
    public ApiResponse<List<AlbumGenreResponse>> getAlbumTracks(
            @PathVariable Long albumId) {
        List<AlbumGenreResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

}
