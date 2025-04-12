package com.onhz.server.controller.common;

import com.onhz.server.dto.response.ApiResponse;
import com.onhz.server.dto.response.common.CodeResponse;
import com.onhz.server.dto.response.common.GenreFeaturedResponse;
import com.onhz.server.dto.response.common.GenreFeaturedSimpleResponse;
import com.onhz.server.service.common.CommonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/common")
@RequiredArgsConstructor
public class CommonController {
    private final CommonService commonService;
    @GetMapping("/code/{code}")
    @Operation(summary = "코드 리스트", description = "")
    public ApiResponse<List<CodeResponse>> getCodes(
            @Parameter(description = "코드", required = true, example = "genre")
            @PathVariable(name="code") String code) {
        List<CodeResponse> result = commonService.getCodeByType(code);
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }


    @GetMapping("/genres/featured")
    @Operation(summary = "주요 장르 리스트조회", description = "")
    public ApiResponse<List<GenreFeaturedSimpleResponse>> getGenreFeatures() {
        List<GenreFeaturedSimpleResponse> result = commonService.getGenreFeatures();
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/genres/featured/{genreCode}")
    @Operation(summary = "주요 장르 단건조회", description = "")
    public ApiResponse<GenreFeaturedResponse> getGenreFeature(
            @Parameter(description = "대표 장르", required = true, example = "rock")
            @PathVariable(name="genreCode") String genreCode) {
        GenreFeaturedResponse result = commonService.getGenreFeature(genreCode);
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }
}
