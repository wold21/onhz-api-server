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
        String keywordLower = keyword.toLowerCase();

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

        // 검색방법 설정(중요!)
        Map<String, Object> functionScore = getSearchParameter(keywordLower);

        requestBody.put("query", Map.of("function_score", functionScore));
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
                    throw new IllegalArgumentException("올바른 형식이 아닙니다.");
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

    private static Map<String, Object> getSearchParameter(String keywordLower) {
        /**
         * 검색 파라미터가 많아서 각 줄별로 주석을 달겠음.
         * 요약
         * 검색어와 완전하게 일치하는 문서가 1순위
         * 여러 단어일 시 각 단어를 모두 포함한 문서가 2순위
         * 검색어와 정확한 단어 일치하는 문서가 3순위
         * 정해놓은 옵션에 따라 만족하는 문서에 대해 확실한 점수를 부여하기 위해
         * 추가 점수 부여 함수가 존재함.(결과값 곱셈처리 등)
         * */

        /*
        * 사용자가 검색한 키워드와 정확히 동일한 순서로 검색 대상이 일치하는지에 대해서 점수를 부여하며
        * 최고 점수가 50점임. 예를들어 bruno mars면 사용자가 bruno mars라고 검색했을 때 50점이 되는 것임.
        * 검색한 내용 그대로 전부 일치하는 것에 점수를 높게 주는 것이 낫다고 생각함.
        */
        Map<String, Object> phraseMatch = new HashMap<>();
        phraseMatch.put("search_vector", Map.of("query", keywordLower, "boost", 50));

        /*
        * 검색어와 정확이 일치하는 단어를 찾음.
        * 정확히 일치하면 최고 20점.
        * 위의 조건은 구문 검색인것이고 해당 조건은 단어 검색임.
        */
        Map<String, Object> exactMatch = new HashMap<>();
        exactMatch.put("search_vector", Map.of("value", keywordLower, "boost", 20));

        /*
        * 검색어로 시작하는 단어를 찾음.
        * 최고 점수는 2점 여기까지 오면 앞에서부터 비슷한 데이터가 검색됨
        * bruno인 경우 bru가 검색되는 것임.
        */
        Map<String, Object> prefixMatch = new HashMap<>();
        prefixMatch.put("search_vector", Map.of("value", keywordLower, "boost", 2));

        /*
        * 이 부분은 위의 셋팅을 바탕으로 어떤 데이터를 우선적으로 올리거나 검색할지를 설정함 
        */
        Map<String, Object> multiMatch = new HashMap<>();
        multiMatch.put("query", keywordLower);
        multiMatch.put("type", "best_fields"); // 가장 잘 일치하는 필드의 점수를 사용함.
        multiMatch.put("operator", "and"); // 검색어의 모든 단어가 포함된 문서만 찾음
        multiMatch.put("fuzziness", "AUTO"); // 오타를 허용함.(엘라스틱 서치에 설정된 값이 있는것 같음. 아마도 키보드사이의 거리나 알파벳 순서 이런거?)
        multiMatch.put("fields", List.of(
                "search_vector^3", // search_vector에 3배 가중치
                "search_vector.korean^2", // search_vector.korean에 2배 가중치
                "search_vector.english^1.5", // search_vector.english에 1.5배 가중치
                "search_vector.ngram^0.5" // search_vector.ngram에 0.5배 가중치
        ));

        String[] words = keywordLower.split("\\s+");
        List<Map<String, Object>> shouldClauses = new ArrayList<>();

        /*
        * 여기선 위에서 설정한 검색 방식 중 하나라도 일치하면 결과에 포함될 수 있도록 설정하는 곳.
        * 여러개가 일치하면 점수를 많이 받음.
        */
        shouldClauses.add(Map.of("match_phrase", phraseMatch));
        shouldClauses.add(Map.of("term", exactMatch));
        shouldClauses.add(Map.of("prefix", prefixMatch));
        shouldClauses.add(Map.of("multi_match", multiMatch));

        /*
        * 검색어가 여러 단어로 구성된 경우 검색어가 모두 포함된 문서를 더 중요하다고 여김.
        */
        if (words.length > 1) {
            Map<String, Object> allWordsMatch = new HashMap<>();
            List<Map<String, Object>> mustClauses = new ArrayList<>();

            for (String word : words) {
                Map<String, Object> termQuery = new HashMap<>();
                termQuery.put("search_vector", word);
                mustClauses.add(Map.of("term", termQuery));
            }

            allWordsMatch.put("must", mustClauses);
            shouldClauses.add(Map.of("bool", allWordsMatch));
        }

        Map<String, Object> boolQuery = new HashMap<>();
        boolQuery.put("should", shouldClauses);

        Map<String, Object> functionScore = new HashMap<>();
        functionScore.put("query", Map.of("bool", boolQuery));
        functionScore.put("boost_mode", "multiply"); // 기본 점수와 함수 점수를 곱, 결과 극적
        functionScore.put("score_mode", "sum"); // 여러 함수 점수를 합산

        List<Map<String, Object>> functions = new ArrayList<>();

        /*
        * 가중치 함수로 구문이 정확하는 문서에 대해 아예 높은 점수를 줘버림.(최상단 유도)
        */
        functions.add(Map.of(
                "filter", Map.of("match_phrase", Map.of("search_vector", keywordLower)),
                "weight", 300
        ));

        /*
        * 여러 단어로 검색 시 해당 단어를 모두 포함한 문서에 대해 150점을 부여함.(상단 유도)
        */
        if (words.length > 1) {
            Map<String, Object> allWordsFilter = new HashMap<>();
            List<Map<String, Object>> filterMustClauses = new ArrayList<>();

            for (String word : words) {
                Map<String, Object> termQuery = new HashMap<>();
                termQuery.put("search_vector", word);
                filterMustClauses.add(Map.of("term", termQuery));
            }

            allWordsFilter.put("must", filterMustClauses);

            functions.add(Map.of(
                    "filter", Map.of("bool", allWordsFilter),
                    "weight", 150
            ));
        }

        /*
        * 검색어와 정확히 일치하는 `단일` 문서가 있는 경우 100점의 점수를 부여함.
        */
        functions.add(Map.of(
                "filter", Map.of("term", Map.of("search_vector", keywordLower)),
                "weight", 100
        ));

        functionScore.put("functions", functions);
        return functionScore;
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
