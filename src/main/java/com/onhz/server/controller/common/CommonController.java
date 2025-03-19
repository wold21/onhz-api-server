package com.onhz.server.controller.common;

import com.onhz.server.dto.response.ApiResponse;
import com.onhz.server.dto.response.common.CodeResponse;
import com.onhz.server.dto.response.common.GenreCatalogResponse;
import com.onhz.server.dto.response.common.GenreCatalogSimpleResponse;
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
    @GetMapping("/code/{codeType}")
    @Operation(summary = "코드 리스트", description = "")
    public ApiResponse<List<CodeResponse>> getCodes(
            @Parameter(description = "코드", required = true, example = "genre")
            @PathVariable String codeType) {
        List<CodeResponse> result = commonService.getCodeByType(codeType);
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/catalog/genres")
    @Operation(summary = "코드 리스트", description = "")
    public ApiResponse<List<GenreCatalogSimpleResponse>> getCodes() {
        List<GenreCatalogSimpleResponse> result = commonService.getGenreCatalogs();
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }

    @GetMapping("/catalog/genres/{genreCode}")
    @Operation(summary = "코드 리스트", description = "")
    public ApiResponse<GenreCatalogResponse> getGenreCatalog(
            @Parameter(description = "대표 장르", required = true, example = "rock")
            @PathVariable String genreCode) {
        GenreCatalogResponse result = commonService.getGenreCatalog(genreCode);
        return ApiResponse.success(HttpStatus.OK, "success", result);
    }
}
