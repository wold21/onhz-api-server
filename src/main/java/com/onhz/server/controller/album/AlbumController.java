package com.onhz.server.controller.album;

import com.onhz.server.dto.response.album.AlbumFeaturedResponse;
import com.onhz.server.dto.response.album.AlbumDetailResponse;
import com.onhz.server.dto.response.album.AlbumGenreArtistResponse;
import com.onhz.server.dto.response.ApiResponse;
import com.onhz.server.dto.response.track.TrackResponse;
import com.onhz.server.service.album.AlbumService;
import com.onhz.server.service.track.TrackService;
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
    private final TrackService trackService;

    @GetMapping
    @Operation(summary = "앨범 리스트조회", description = "")
    public ApiResponse<List<AlbumGenreArtistResponse>> getAlbums(
            @Parameter(description = "이전 페이지 마지막 데이터의 ID 값\n * 첫번째 페이지, lastId = null")
            @RequestParam(name = "lastId", required = false) Long lastId,
            @Parameter(description = "이전 페이지 마지막 데이터의 orderBy 로 설정된 값\n * 첫번째 페이지, lastOrderValue = null ")
            @RequestParam(name = "lastOrderValue", required = false) String lastOrderValue,
            @RequestParam(name = "limit", defaultValue = "10", required = false) int limit,
            @RequestParam(name = "orderBy", defaultValue = "createdAt") String orderBy) {
        List<AlbumGenreArtistResponse> response = albumService.getAlbums(lastId, lastOrderValue, limit, orderBy);
        return ApiResponse.success(HttpStatus.OK, "success", response);
    }

//    @GetMapping("/genre/{genreCode}")
//    @Operation(summary = "장르별 앨범 조회", description = "")
//    public ApiResponse<List<AlbumDetailResponse>> getAlbumsWithGenre(
//            @Parameter(description = "장르", required = true, example = "rock")
//            @PathVariable(name="genreCode") String genreCode,
//            @RequestParam(name = "offset", defaultValue = "0", required = false) int offset,
//            @RequestParam(name = "limit", defaultValue = "12", required = false) int limit,
//            @RequestParam(name = "orderBy", defaultValue = "ratingCount,averageRating") String orderBy) {
//        List<AlbumDetailResponse> result = albumService.getAlbumsWithGenreAndArtist(offset, limit, orderBy, genreCode);
//        return ApiResponse.success(HttpStatus.OK, "success", result);
//    }

    @GetMapping("/{albumId}")
    @Operation(summary = "앨범 상세 조회", description = "")
    public ApiResponse<AlbumDetailResponse> getAlbum(
            @Parameter(description = "앨범 ID", required = true, example = "1")
            @PathVariable(name="albumId") Long albumId) {
        AlbumDetailResponse result = albumService.getAlbumWithDetail(albumId);
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/{albumId}/tracks")
    @Operation(summary = "앨범별 트랙 조회", description = "")
    public ApiResponse<List<TrackResponse>> getAlbumTracks(
            @Parameter(description = "앨범 ID", required = true, example = "1")
            @PathVariable(name="albumId") Long albumId) {
        List<TrackResponse> result = trackService.getTracksByAlbumId(albumId);
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/genre/{genreCode}/featured")
    @Operation(summary = "주요 장르별 앨범 조회", description = "")
    public ApiResponse<List<AlbumFeaturedResponse>> getAlbumsWithDetailAndRating(
            @Parameter(description = "장르", required = true, example = "rock")
            @PathVariable(name="genreCode") String genreCode,
            @RequestParam(name = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam(name = "limit", defaultValue = "30", required = false) int limit,
            @RequestParam(name = "orderBy", defaultValue = "ratingCount,averageRating") String orderBy) {
        List<AlbumFeaturedResponse> result = albumService.getAlbumsWithFeatured(offset, limit, orderBy, genreCode);
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

}
