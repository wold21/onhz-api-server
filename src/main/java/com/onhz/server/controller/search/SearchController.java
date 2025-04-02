package com.onhz.server.controller.search;

import com.onhz.server.dto.response.ApiResponse;
import com.onhz.server.dto.response.SearchResponse;
import com.onhz.server.service.search.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;
    @GetMapping
    @Operation(summary = "검색 (아티스트, 앨범, 트랙)", description = "")
    public ApiResponse<SearchResponse<?>> search(
            @Parameter(description = "이전 페이지 마지막 데이터의 ID 값\n * 첫번째 페이지, lastId = null")
            @RequestParam(name = "lastId", required = false) Long lastId,
            @Parameter(description = "이전 페이지 마지막 데이터의 orderBy 로 설정된 값\n * 첫번째 페이지, lastOrderValue = null ")
            @RequestParam(name = "lastOrderValue", required = false) String lastOrderValue,
            @RequestParam(name = "limit", defaultValue = "10", required = false) int limit,
            @RequestParam(name = "orderBy", defaultValue = "createdAt") String orderBy,
            @Parameter(description = "검색어")
            @RequestParam(name = "keyword") String keyword,
            @Parameter(description = "검색 타입\n * artist, album, track")
            @RequestParam(name = "type", defaultValue = "album") String type
    ) {
        SearchResponse<?> results = searchService.search(lastId, lastOrderValue, limit, orderBy, keyword, type);
        return new ApiResponse(HttpStatus.OK.value(), "success", results);
    }
}
