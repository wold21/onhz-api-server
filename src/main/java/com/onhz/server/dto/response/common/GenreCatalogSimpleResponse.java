package com.onhz.server.dto.response.common;

import com.onhz.server.entity.GenreCatalogEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class GenreCatalogSimpleResponse {
    private final Long id;
    private final String code;
    private final String name;
    private final String imagePath;


    public static GenreCatalogSimpleResponse of(GenreCatalogEntity genreCatalog) {
        return GenreCatalogSimpleResponse.builder()
                .id(genreCatalog.getId())
                .code(genreCatalog.getCode())
                .name(genreCatalog.getName())
                .imagePath(genreCatalog.getImagePath())
                .build();
    }

    public static List<GenreCatalogSimpleResponse> fromList(List<GenreCatalogEntity> genreCatalogs) {
        return genreCatalogs.stream()
                .map(GenreCatalogSimpleResponse::of)
                .collect(Collectors.toList());
    }
}
