package com.onhz.server.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchResponse<T> {
    private String type;
    private String keyword;
    private T results;
}
