package com.onhz.server.controller.track;


import com.onhz.server.dto.response.ApiResponse;
import com.onhz.server.dto.response.album.AlbumDetailResponse;
import com.onhz.server.dto.response.track.TrackAlbumResponse;
import com.onhz.server.service.album.AlbumService;
import com.onhz.server.service.track.TrackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tracks")
@RequiredArgsConstructor
public class TrackController {
    private final TrackService trackService;
    @GetMapping("/{trackId}")
    @Operation(summary = "트랙 상세 조회", description = "")
    public ApiResponse<TrackAlbumResponse> getTracks(
            @Parameter(description = "트랙 ID", required = true, example = "1")
            @PathVariable(name="trackId") Long trackId) {
        TrackAlbumResponse result = trackService.getTrackByIdWithAlbum(trackId);
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

}
