package com.onhz.server.controller.track;


import com.onhz.server.dto.response.ApiResponse;
import com.onhz.server.dto.response.album.AlbumDetailResponse;
import com.onhz.server.service.album.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tracks")
@RequiredArgsConstructor
public class TrackController {
    private final AlbumService albumService;
    @GetMapping("/{trackId}")
    @Operation(summary = "트랙 상세 조회", description = "")
    public ApiResponse<AlbumDetailResponse> getTracks(
            @Parameter(description = "트랙 ID", required = true, example = "1")
            @PathVariable(name="trackId") Long trackId) {
        AlbumDetailResponse result = albumService.getAlbumByTrackIdWithDetail(trackId);
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

}
