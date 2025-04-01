package com.onhz.server.controller.artist;


import com.onhz.server.dto.response.ApiResponse;
import com.onhz.server.dto.response.album.AlbumResponse;
import com.onhz.server.dto.response.artist.ArtistAlbumResponse;
import com.onhz.server.dto.response.artist.ArtistResponse;
import com.onhz.server.dto.response.artist.ArtistTrackResponse;
import com.onhz.server.dto.response.track.TrackDetailResponse;
import com.onhz.server.dto.response.track.TrackResponse;
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
            @Parameter(description = "이전 페이지 마지막 데이터의 ID 값\n * 첫번째 페이지, lastId = null")
            @RequestParam(name = "lastId", required = false) Long lastId,
            @Parameter(description = "이전 페이지 마지막 데이터의 orderBy 로 설정된 값\n * 첫번째 페이지, lastOrderValue = null ")
            @RequestParam(name = "lastOrderValue", required = false) String lastOrderValue,
            @RequestParam(name = "limit", defaultValue = "10", required = false) int limit,
            @RequestParam(name = "orderBy", defaultValue = "createdAt") String orderBy) {
        List<ArtistResponse> result = artistService.getArtists(lastId, lastOrderValue, limit, orderBy);
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{artistId}")
    @Operation(summary = "아티스트 상세 조회", description = "")
    public ApiResponse<ArtistResponse> getArtist(
            @Parameter(description = "아티스트 ID", required = true, example = "1")
            @PathVariable(name="artistId") Long artistId) {
        ArtistResponse result = artistService.getArtist(artistId);
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{artistId}/tracks")
    @Operation(summary = "아티스트별 트랙 조회", description = "")
    public ApiResponse<List<TrackDetailResponse>> getArtistWithTracks(
            @Parameter(description = "아티스트 ID", required = true, example = "1")
            @PathVariable(name="artistId") Long artistId,
            @Parameter(description = "이전 페이지 마지막 데이터의 ID 값\n * 첫번째 페이지, lastId = null")
            @RequestParam(name = "lastId", required = false) Long lastId,
            @Parameter(description = "이전 페이지 마지막 데이터의 orderBy 로 설정된 값\n * 첫번째 페이지, lastOrderValue = null ")
            @RequestParam(name = "lastOrderValue", required = false) String lastOrderValue,
            @RequestParam(name = "limit", defaultValue = "10", required = false) int limit,
            @RequestParam(name = "orderBy", defaultValue = "createdAt") String orderBy) {
        List<TrackDetailResponse> result = artistService.getArtistWithTracks(artistId, lastId, lastOrderValue, limit, orderBy);
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{artistId}/albums")
    @Operation(summary = "아티스트별 앨범 조회", description = "")
    public ApiResponse<List<AlbumResponse>> getArtistAlbums(
            @Parameter(description = "아티스트 ID", required = true, example = "1")
            @PathVariable(name="artistId") Long artistId,
            @Parameter(description = "이전 페이지 마지막 데이터의 ID 값\n * 첫번째 페이지, lastId = null")
            @RequestParam(name = "lastId", required = false) Long lastId,
            @Parameter(description = "이전 페이지 마지막 데이터의 orderBy 로 설정된 값\n * 첫번째 페이지, lastOrderValue = null ")
            @RequestParam(name = "lastOrderValue", required = false) String lastOrderValue,
            @RequestParam(name = "limit", defaultValue = "10", required = false) int limit,
            @RequestParam(name = "orderBy", defaultValue = "createdAt") String orderBy) {

        List<AlbumResponse> result = artistService.getArtistWithAlbums(artistId, lastId, lastOrderValue, limit, orderBy);
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }
}
