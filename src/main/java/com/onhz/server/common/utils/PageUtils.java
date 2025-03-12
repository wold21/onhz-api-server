package com.onhz.server.common.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.*;

public class PageUtils {
    private record FieldMapping(String entityField, String entityType, String alias) {}
    private static final Map<String, FieldMapping> FIELD_MAPPINGS = new HashMap<>();

    private static final String SORT_DELIMITER = ",";

    static {
        registerField("id", "id", "AlbumEntity", "a");
        registerField("title", "title", "AlbumEntity", "a");
        registerField("release_date", "releaseDate", "AlbumEntity", "a");
        registerField("created_at", "createdAt", "AlbumEntity", "a");

        registerField("average_rating", "averageRating", "AlbumRatingSummaryEntity", "ars");
        registerField("rating_count", "ratingCount", "AlbumRatingSummaryEntity", "ars");
    }

    private static void registerField(String clientField, String entityField, String entityType, String alias) {
        FIELD_MAPPINGS.put(clientField, new FieldMapping(entityField, entityType, alias));
    }

    public static List<String> splitOrderBy(String orderBy) {
        if(orderBy == null || orderBy.isBlank() || orderBy.isEmpty()) {
            return Collections.singletonList("created_at");
        }
        return Arrays.asList(orderBy.split(SORT_DELIMITER));
    }

    public static boolean isValidSortField(String orderBy) {
        List<String> orderByItems = splitOrderBy(orderBy);
        return orderByItems.stream()
                .map(item -> extractFieldName(item))
                .allMatch(FIELD_MAPPINGS::containsKey);
    }

    public static String extractFieldName(String orderByItem) {
        return orderByItem.startsWith("-") ? orderByItem.substring(1) : orderByItem;
    }

    public static Pageable createPageable(int offset, int limit, String orderBy) {
        Sort sort = createSort(orderBy);
        int pageNumber = limit > 0 ? offset / limit : 0;
        return PageRequest.of(pageNumber, limit, sort);
    }

    public static Sort createSort(String orderBy) {
        List<String> orderByItems = splitOrderBy(orderBy);

        List<Sort.Order> orders = new ArrayList<>();
        for (String item : orderByItems) {
            String mappedField = getMappedFieldName(item);
            Sort.Direction direction = extractDirection(item);
            orders.add(new Sort.Order(direction, mappedField));
        }

        return Sort.by(orders);
    }

    public static String getMappedFieldName(String orderByItem) {
        String fieldName = extractFieldName(orderByItem);
        FieldMapping mapping = FIELD_MAPPINGS.get(fieldName);
        return mapping.alias() + "." + mapping.entityField();
    }

    public static Sort.Direction extractDirection(String orderByItem) {
        return orderByItem.startsWith("-") ? Sort.Direction.ASC : Sort.Direction.DESC;
    }



}