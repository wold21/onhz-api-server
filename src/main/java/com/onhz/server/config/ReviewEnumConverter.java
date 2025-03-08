package com.onhz.server.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import com.onhz.server.common.enums.Review;

@Component
public class ReviewEnumConverter implements Converter<String, Review> {
    @Override
    public Review convert(String source) {
        return Review.valueOf(source.toUpperCase());
    }
}