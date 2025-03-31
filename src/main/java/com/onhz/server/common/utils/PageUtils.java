package com.onhz.server.common.utils;

import com.onhz.server.entity.album.AlbumEntity;
import com.onhz.server.entity.album.AlbumRatingSummaryEntity;
import com.onhz.server.entity.artist.ArtistEntity;
import com.onhz.server.entity.artist.ArtistRatingSummaryEntity;
import com.onhz.server.entity.review.ReviewEntity;
import com.onhz.server.entity.track.TrackEntity;
import com.onhz.server.entity.track.TrackRatingSummaryEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.*;

public class PageUtils {
    private record FieldMapping(String entityField, String alias) {}
    private static final Map<Class<?>, Map<String, FieldMapping>> FIELD_MAPPINGS = new HashMap<>();

    private static final String SORT_DELIMITER = ",";

    static {
        registerField(AlbumEntity.class, "id", null);
        registerField(AlbumEntity.class, "title", null);
        registerField(AlbumEntity.class, "releaseDate", null);
        registerField(AlbumEntity.class, "createdAt", null);

        registerField(ArtistEntity.class, "id", null);
        registerField(ArtistEntity.class, "name", null);
        registerField(ArtistEntity.class, "createdAt", null);

        registerField(TrackEntity.class, "id", "t");
        registerField(TrackEntity.class, "trackName", "t");
        registerField(TrackEntity.class, "createdAt", "t");

        registerField(AlbumRatingSummaryEntity.class, "averageRating", "ars");
        registerField(AlbumRatingSummaryEntity.class, "ratingCount", "ars");

        registerField(ArtistRatingSummaryEntity.class, "averageRating", "ars");
        registerField(ArtistRatingSummaryEntity.class, "ratingCount", "ars");

        registerField(TrackRatingSummaryEntity.class, "averageRating", "trs");
        registerField(TrackRatingSummaryEntity.class, "ratingCount", "trs");

        registerField(ReviewEntity.class, "createdAt", null);
        registerField(ReviewEntity.class, "rating", null);
    }

    private static void registerField(Class<?> entityClass, String entityField, String alias) {
        FIELD_MAPPINGS
                .computeIfAbsent(entityClass, k -> new HashMap<>())
                .put(entityField, new FieldMapping(entityField, alias));
    }

    public static List<String> splitOrderBy(String orderBy) {
        if (orderBy == null || orderBy.isBlank()) {
            return Collections.singletonList("createdAt");
        }
        return Arrays.asList(orderBy.split(SORT_DELIMITER));
    }

    public static boolean isValidSortField(String orderBy, Class<?> entityClass) {
        List<String> orderByItems = splitOrderBy(orderBy);
        Map<String, FieldMapping> entityFields = FIELD_MAPPINGS.getOrDefault(entityClass, Collections.emptyMap());
        return orderByItems.stream()
                .map(PageUtils::extractFieldName)
                .allMatch(entityFields::containsKey);
    }

    public static String extractFieldName(String orderByItem) {
        return orderByItem.startsWith("-") ? orderByItem.substring(1) : orderByItem;
    }

    public static Pageable createPageable(int offset, int limit, String orderBy, Class<?> entityClass) {
        if (!isValidSortField(orderBy, entityClass)) {
            throw new IllegalArgumentException("정렬조건이 올바르지 않습니다.");
        }
        Sort sort = createSort(orderBy, entityClass);
        int pageNumber = limit > 0 ? offset / limit : 0;
        return PageRequest.of(pageNumber, limit, sort);
    }

    public static Pageable createPageable(int offset, int limit) {
        Sort sort = Sort.unsorted();
        int pageNumber = limit > 0 ? offset / limit : 0;
        return PageRequest.of(pageNumber, limit, sort);
    }

    public static Sort createSort(String orderBy, Class<?> entityClass) {
        List<String> orderByItems = splitOrderBy(orderBy);
        Map<String, FieldMapping> entityFields = FIELD_MAPPINGS.getOrDefault(entityClass, Collections.emptyMap());

        List<Sort.Order> orders = new ArrayList<>();
        for (String item : orderByItems) {
            String fieldName = extractFieldName(item);
            FieldMapping mapping = entityFields.get(fieldName);
            if (mapping != null) {
                if (mapping.alias != null) {
                    orders.add(new Sort.Order(extractDirection(item), mapping.alias() + "." + mapping.entityField()));
                } else {
                    orders.add(new Sort.Order(extractDirection(item), mapping.entityField()));
                }
            }
        }

        return orders.isEmpty() ? Sort.unsorted() : Sort.by(orders);
    }

    public static Sort.Direction extractDirection(String orderByItem) {
        return orderByItem.startsWith("-") ? Sort.Direction.ASC : Sort.Direction.DESC;
    }
}