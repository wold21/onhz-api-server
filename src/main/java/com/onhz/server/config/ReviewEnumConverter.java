package com.onhz.server.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import com.onhz.server.common.enums.ReviewType;

@Component
public class ReviewEnumConverter implements Converter<String, ReviewType> {
    @Override
    public ReviewType convert(String source) {
        return ReviewType.valueOf(source.toUpperCase());
    }
}