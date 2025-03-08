package com.onhz.server.controller.artist;


import com.onhz.server.dto.response.ApiResponse;
import com.onhz.server.dto.response.ArtistResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/artists")
public class ArtistController {
    @GetMapping("/")
    @Operation(summary = "아티스트 리스트", description = "")
    public ApiResponse<List<ArtistResponse>> getArtists(
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam(defaultValue = "10", required = false) int limit,
            @RequestParam(name = "order_by", defaultValue = "rating_count,average_rating") String orderBy) {

        List<ArtistResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{artistId}")
    @Operation(summary = "아티스트 상세 조회", description = "")
    public ApiResponse<ArtistResponse> getArtist(
            @PathVariable Long artistId) {
        ArtistResponse result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{artistId}/tracks")
    @Operation(summary = "아티스트별 트랙 조회", description = "")
    public ApiResponse<List<ArtistResponse>> getArtistTracks(
            @PathVariable Long artistId,
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam(defaultValue = "10", required = false) int limit,
            @RequestParam(name = "order_by", defaultValue = "rating, created_at") String orderBy) {

        List<ArtistResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{artistId}/albums")
    @Operation(summary = "아티스트별 앨범 조회", description = "")
    public ApiResponse<List<ArtistResponse>> getArtistAlbums(
            @PathVariable Long artistId,
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam(defaultValue = "10", required = false) int limit,
            @RequestParam(name = "order_by", defaultValue = "rating, created_at") String orderBy) {

        List<ArtistResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }
}
