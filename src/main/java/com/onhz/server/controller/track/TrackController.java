package com.onhz.server.controller.track;

import com.onhz.server.common.utils.Sort;
import com.onhz.server.dto.response.ApiResponse;
import com.onhz.server.dto.response.TrackResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/tracks")
public class TrackController {
    @GetMapping("/{trackId}")
    public ApiResponse<TrackResponse> getTracks(
            @PathVariable Long trackId) {
        TrackResponse result = null;
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

}
