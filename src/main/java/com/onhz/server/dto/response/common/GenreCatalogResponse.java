package com.onhz.server.dto.response.common;

import com.onhz.server.entity.GenreCatalogEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class GenreCatalogResponse {
    private final Long id;
    private final String code;
    private final String name;
    private final String description;
    private final String imagePath;

    public static GenreCatalogResponse of(GenreCatalogEntity genreCatalog) {
        return GenreCatalogResponse.builder()
                .id(genreCatalog.getId())
                .code(genreCatalog.getCode())
                .name(genreCatalog.getName())
                .description(genreCatalog.getDescription())
                .imagePath(genreCatalog.getImagePath())
                .build();
    }
}
