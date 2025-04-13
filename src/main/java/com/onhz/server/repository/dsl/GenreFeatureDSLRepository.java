package com.onhz.server.repository.dsl;

import com.onhz.server.entity.GenreFeaturedEntity;

import java.util.List;

public interface GenreFeatureDSLRepository {
    List<GenreFeaturedEntity> findFeatureGenresWithRated();
}
