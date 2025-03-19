package com.onhz.server.controller.album;

import com.onhz.server.dto.response.album.AlbumDetailResponse;
import com.onhz.server.dto.response.album.AlbumGenreArtistResponse;
import com.onhz.server.dto.response.ApiResponse;
import com.onhz.server.service.album.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/albums")
@RequiredArgsConstructor
public class AlbumController {
    private final AlbumService albumService;

    @GetMapping
    @Operation(summary = "앨범 리스트조회", description = "")
    public ApiResponse<List<AlbumGenreArtistResponse>> getAlbums(
            @RequestParam(name = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam(name = "limit", defaultValue = "10", required = false) int limit,
            @RequestParam(name = "order_by", defaultValue = "rating_count,average_rating") String orderBy) {
        List<AlbumGenreArtistResponse> response = albumService.getAlbums(offset, limit, orderBy);
        return ApiResponse.success(HttpStatus.OK, "success", response);
    }

    @GetMapping("/genre/{genreCode}")
    @Operation(summary = "장르별 앨범 조회", description = "")
    public ApiResponse<List<AlbumGenreArtistResponse>> getAlbumsWithGenre(
            @Parameter(description = "장르", required = true, example = "rock")
            @PathVariable String genreCode,
            @RequestParam(name = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam(name = "limit", defaultValue = "12", required = false) int limit,
            @RequestParam(name = "order_by", defaultValue = "rating_count,average_rating") String orderBy) {
        List<AlbumGenreArtistResponse> result = albumService.getAlbumsWithGenre(offset, limit, orderBy, genreCode);
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{albumId}")
    @Operation(summary = "앨범 상세 조회", description = "")
    public ApiResponse<AlbumDetailResponse> getAlbum(
            @Parameter(description = "앨범 ID", required = true, example = "1")
            @PathVariable Long albumId) {
        AlbumDetailResponse result = albumService.getAlbumWithDetail(albumId);
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/tracks/{albumId}")
    @Operation(summary = "앨범별 트랙 조회", description = "")
    public ApiResponse<List<AlbumGenreArtistResponse>> getAlbumTracks(
            @Parameter(description = "앨범 ID", required = true, example = "1")
            @PathVariable Long albumId) {
        List<AlbumGenreArtistResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

}
