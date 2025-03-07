package com.onhz.server.common.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class OAuthUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Map<String, Object> extractMap(Map<String, Object> attributes, String key) {
        return objectMapper.convertValue(MapUtils.getObject(attributes, key, new HashMap<>()),
                new TypeReference<Map<String, Object>>() {}
        );
    }
}
