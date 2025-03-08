package com.onhz.server.common.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SortUtils {
    public static Pageable createPageable(int offset, int limit, String orderBy) {
        return PageRequest.of(
                offset,
                limit,
                parseSort(orderBy)
        );
    }

    private static Sort parseSort(String orderBy) {
        if (!StringUtils.hasText(orderBy)) {
            return Sort.by(Sort.Direction.DESC, "created_at");
        }

        List<Sort.Order> orders = Arrays.stream(orderBy.split(","))
                .map(SortUtils::createOrder)
                .collect(Collectors.toList());

        return Sort.by(orders);
    }

    private static Sort.Order createOrder(String field) {
        boolean isDesc = field.startsWith("-");
        String originField = isDesc ? field.substring(1) : field;
        String convertField = convertField(originField);

        return isDesc ? Sort.Order.desc(convertField) : Sort.Order.asc(convertField);
    }

    private static String convertField(String field) {
        boolean nextUpper = false;
        StringBuilder sb = new StringBuilder();
        for(char c : field.toCharArray()){
            if(c == '_') {
                nextUpper = true;
            } else {
                sb.append(nextUpper ? Character.toUpperCase(c) : c);
                nextUpper = false;
            }
        }
        return sb.toString();
    }
}
