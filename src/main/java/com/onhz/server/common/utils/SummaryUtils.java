package com.onhz.server.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class SummaryUtils {
    private final ObjectMapper objectMapper;

    private Map<String, Integer> initializeDistributionMap() {
        Map<String, Integer> distribution = new LinkedHashMap<>();
        for (double i = 0.0; i <= 5.0; i += 0.5) {
            distribution.put(String.valueOf(i), 0);
        }
        return distribution;
    }

    public <T> Map<String, Integer> calculateRatingDistributionFromObjects(
            List<T> items,
            Function<T, Double> ratingExtractor) {

        Map<String, Integer> distribution = initializeDistributionMap();

        for (T item : items) {
            Double rating = ratingExtractor.apply(item);
            if (rating != null) {
                double roundedRating = Math.round(rating * 2) / 2.0;
                String key = String.valueOf(roundedRating);
                distribution.put(key, distribution.getOrDefault(key, 0) + 1);
            }
        }

        return distribution;
    }

    public String convertToJson(Map<String, Integer> distribution) {
        try {
            return objectMapper.writeValueAsString(distribution);
        } catch (Exception e) {
            log.info("JSON 변환 중 오류가 발생했습니다.");
            throw new IllegalArgumentException("JSON 변환 중 오류가 발생했습니다.", e);
        }
    }
}
