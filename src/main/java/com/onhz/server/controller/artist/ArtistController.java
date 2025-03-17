package com.onhz.server.controller.artist;


import com.onhz.server.dto.response.ApiResponse;
import com.onhz.server.dto.response.ArtistResponse;
import com.onhz.server.dto.response.ArtistTrackResponse;
import com.onhz.server.service.artist.ArtistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/artists")
@RequiredArgsConstructor
public class ArtistController {
    private final ArtistService artistService;
    @GetMapping
    @Operation(summary = "아티스트 리스트", description = "")
    public ApiResponse<List<ArtistResponse>> getArtists(
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam(defaultValue = "10", required = false) int limit,
            @RequestParam(name = "order_by", defaultValue = "created_at") String orderBy) {

        List<ArtistResponse> result = artistService.getArtists(offset, limit, orderBy);
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{artistId}")
    @Operation(summary = "아티스트 상세 조회", description = "")
    public ApiResponse<ArtistResponse> getArtist(
            @Parameter(description = "아티스트 ID", required = true, example = "1")
            @PathVariable Long artistId) {
        ArtistResponse result = artistService.getArtist(artistId);
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{artistId}/tracks")
    @Operation(summary = "아티스트별 트랙 조회", description = "")
    public ApiResponse<ArtistTrackResponse> getArtistWithTracks(
            @Parameter(description = "아티스트 ID", required = true, example = "1")
            @PathVariable Long artistId,
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam(defaultValue = "10", required = false) int limit,
            @RequestParam(name = "order_by", defaultValue = "rating_count,average_rating") String orderBy) {

        ArtistTrackResponse result = artistService.getArtistWithTracks(artistId, offset, limit, orderBy);
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{artistId}/albums")
    @Operation(summary = "아티스트별 앨범 조회", description = "")
    public ApiResponse<List<ArtistResponse>> getArtistAlbums(
            @Parameter(description = "아티스트 ID", required = true, example = "1")
            @PathVariable Long artistId,
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam(defaultValue = "10", required = false) int limit,
            @RequestParam(name = "order_by", defaultValue = "rating, created_at") String orderBy) {

        List<ArtistResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }
}
