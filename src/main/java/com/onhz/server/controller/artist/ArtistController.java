package com.onhz.server.controller.artist;

import com.onhz.server.common.utils.Sort;
import com.onhz.server.dto.response.ApiResponse;
import com.onhz.server.dto.response.ArtistResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/artists")
public class ArtistController {
    @GetMapping("/")
    public ApiResponse<List<ArtistResponse>> getArtist(
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam(defaultValue = "10", required = false) int limit,
            @RequestParam(name = "order_by", defaultValue = "rating_count,average_rating") String orderBy) {
        Sort sort = new Sort(orderBy);
        List<ArtistResponse> result = null;
        return ApiResponse.success(HttpStatus.OK, "success", null);
    }
}
