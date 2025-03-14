package com.onhz.server.controller.album;

import com.onhz.server.dto.response.AlbumGenreResponse;
import com.onhz.server.dto.response.ApiResponse;
import com.onhz.server.entity.album.AlbumEntity;
import com.onhz.server.service.album.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/albums")
@RequiredArgsConstructor
public class AlbumController {
    private final AlbumService albumService;

    @GetMapping
    @Operation(summary = "앨범 리스트조회", description = "")
    public ApiResponse<List<AlbumGenreResponse>> getAlbums(
            @RequestParam(name = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam(name = "limit", defaultValue = "10", required = false) int limit,
            @RequestParam(name = "order_by", defaultValue = "rating_count,average_rating") String orderBy) {
        List<AlbumGenreResponse> response = albumService.getAlbums(offset, limit, orderBy);
        return ApiResponse.success(HttpStatus.OK, "success", response);
    }

    @GetMapping("/genre/{genre}")
    @Operation(summary = "장르별 앨범 조회", description = "")
    public ApiResponse<List<AlbumGenreResponse>> getAlbumsWithGenre(
            @PathVariable(name = "genre_name") String genreCode,
            @RequestParam(name = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam(name = "limit", defaultValue = "12", required = false) int limit,
            @RequestParam(name = "order_by", defaultValue = "rating_count,average_rating") String orderBy) {
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

    @GetMapping("/tracks/{albumId}")
    @Operation(summary = "앨범별 트랙 조회", description = "")
    public ApiResponse<List<AlbumGenreResponse>> getAlbumTracks(
            @PathVariable Long albumId) {
        List<AlbumGenreResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

}
