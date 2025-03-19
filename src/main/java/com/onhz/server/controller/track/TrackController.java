package com.onhz.server.controller.track;


import com.onhz.server.dto.response.ApiResponse;
import com.onhz.server.dto.response.track.TrackResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tracks")
public class TrackController {
    @GetMapping("/{trackId}")
    @Operation(summary = "트랙 상세 조회", description = "")
    public ApiResponse<TrackResponse> getTracks(
            @Parameter(description = "트랙 ID", required = true, example = "1")
            @PathVariable Long trackId) {
        TrackResponse result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

}
