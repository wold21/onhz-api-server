package com.onhz.server.service.search;

import com.onhz.server.dto.response.artist.ArtistSearchResponse;
import com.onhz.server.entity.artist.ArtistEntity;
import com.onhz.server.repository.ArtistRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchArtistService {
    private final ArtistRepository artistRepository;
    private final RestTemplate elasticSearchClient;

    @Data
    @AllArgsConstructor
    public static class SearchResult {
        private Long id;
        private Double score;
    }

    public List searchArtist(String keyword, Long lastId, String lastOrderValue, int limit, String orderBy) {
        List<SearchResult> searchResults = searchKeyword(keyword, lastId, lastOrderValue, limit, orderBy);
        if (searchResults.isEmpty()) {
            return List.of();
        }

        List<Long> ids = searchResults.stream()
                .map(SearchResult::getId)
                .collect(Collectors.toList());

        List<ArtistEntity> artists = artistRepository.findAllByIdIn(ids);
        if (artists.isEmpty()) {
            return List.of();
        }

        Map<Long, ArtistEntity> artistMap = artists.stream()
                .collect(Collectors.toMap(ArtistEntity::getId, artist -> artist));

        List<ArtistEntity> sortedArtists = new ArrayList<>();
        Map<Long, Double> scoreMap = searchResults.stream()
                .collect(Collectors.toMap(SearchResult::getId, SearchResult::getScore));

        for (SearchResult result : searchResults) {
            ArtistEntity artist = artistMap.get(result.getId());
            if (artist != null) {
                sortedArtists.add(artist);
            }
        }

        return sortedArtists.stream()
                .map(artist -> {
                    Double score = scoreMap.get(artist.getId());
                    ArtistSearchResponse response = ArtistSearchResponse.from(artist, score);
                    return response;
                })
                .collect(Collectors.toList());
    }

    public List<SearchResult> searchKeyword(String keyword, Long lastId, String lastOrderValue, int limit, String orderBy) {
        Map<String, Object> requestBody = createRequestBody(keyword, lastId, lastOrderValue, limit, orderBy);

        ResponseEntity<Map> response = elasticSearchClient.postForEntity(
                "/artists/_search",
                requestBody,
                Map.class);

        return extractIdsFromResponse(response.getBody());
    }

    private Map<String, Object> createRequestBody(String keyword, Long lastId, String lastOrderValue, int limit, String orderBy) {
        String sortOrder;
        String sortField;
        if (orderBy == null || orderBy.isEmpty()) {
            sortField = "score";
            sortOrder = "desc";
        } else {
            boolean isAscending = orderBy.startsWith("-");
            sortField = isAscending ? orderBy.substring(1) : orderBy;
            sortOrder = isAscending ? "asc" : "desc";

            if (!isValidSortField(sortField)) {
                throw new IllegalArgumentException("올바른 정렬 조건이 아닙니다.");
            }
        }

        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> multiMatch = new HashMap<>();

        multiMatch.put("query", keyword);
        multiMatch.put("type", "best_fields");
        multiMatch.put("operator", "or");
        multiMatch.put("fuzziness", "AUTO");
        // 이름 필드에 더 높은 가중치 부여
        multiMatch.put("fields", List.of(
                "search_vector^3",
                "search_vector.korean^2.5",
                "search_vector.english^2",
                "search_vector.ngram^1.5"
        ));

        Map<String, Object> query = new HashMap<>();
        query.put("multi_match", multiMatch);
        requestBody.put("query", query);
        requestBody.put("track_scores", true);

        List<Map<String, Object>> sortList = new ArrayList<>();
        Map<String, Object> fieldSort = new HashMap<>();

        if (sortField.equals("score")) {
            fieldSort.put("_score", sortOrder);
        } else if (sortField.equals("createdAt")) {
            fieldSort.put("created_at", sortOrder);
        } else {
            fieldSort.put(sortField, sortOrder);
        }
        sortList.add(fieldSort);

        Map<String, Object> idSort = new HashMap<>();
        idSort.put("id", "asc");
        sortList.add(idSort);

        requestBody.put("sort", sortList);
        requestBody.put("size", limit);

        if (lastId != null && lastOrderValue != null) {
            List<Object> searchAfter = new ArrayList<>();

            if (sortField.equals("score")) {
                try {
                    searchAfter.add(Double.parseDouble(lastOrderValue));
                } catch (NumberFormatException e) {
                    log.warn("올바른 형식이 아닙니다.");
                    searchAfter.add(0.0);
                }
            } else if (sortField.equals("createdAt")) {
                searchAfter.add(lastOrderValue);
            } else {
                searchAfter.add(lastOrderValue);
            }

            searchAfter.add(lastId);
            requestBody.put("search_after", searchAfter);
        }
        return requestBody;
    }

    private boolean isValidSortField(String field) {
        return field.equals("score") || field.equals("createdAt");
    }

    private List<SearchResult> extractIdsFromResponse(Map<String, Object> response) {
        List<SearchResult> results = new ArrayList<>();

        if (response != null && response.containsKey("hits")) {
            Map<String, Object> hitsData = (Map<String, Object>) response.get("hits");
            if (hitsData != null) {
                List<Map<String, Object>> hitsList = (List<Map<String, Object>>) hitsData.get("hits");
                if (hitsList != null) {
                    for (Map<String, Object> hit : hitsList) {
                        String idStr = (String) ((Map<String,Object>)hit.get("_source")).get("id");
                        Long id = Long.parseLong(idStr);
                        Double score = ((Number) hit.get("_score")).doubleValue();
                        results.add(new SearchResult(id, score));
                    }
                }
            }
        }

        return results;
    }


}
