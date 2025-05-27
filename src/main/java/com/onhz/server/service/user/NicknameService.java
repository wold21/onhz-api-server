package com.onhz.server.service.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onhz.server.repository.NicknameWordRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class NicknameService {
    private final NicknameWordRepository nicknameWordRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final Random random = new Random();

    private static final String REDIS_KEY_PREFIX = "nickname:words:";

    @PostConstruct
    public void loadNicknameWordsToRedis() {
        try {
            log.info("Starting to load nickname words to Redis...");

            // 각 카테고리별로 데이터 조회 및 Redis 저장
            loadCategoryToRedis("genre");
            loadCategoryToRedis("adjective");
            loadCategoryToRedis("animal");

            log.info("Successfully loaded all nickname words to Redis");

        } catch (Exception e) {
            log.error("Failed to load nickname words to Redis", e);
        }
    }

    private void loadCategoryToRedis(String category) throws JsonProcessingException {
        List<String> words = nicknameWordRepository.findWordsByCategory(category);
        String jsonWords = objectMapper.writeValueAsString(words);

        redisTemplate.opsForValue().set(REDIS_KEY_PREFIX + category, jsonWords);

        log.info("Loaded {} {} words to Redis", words.size(), category);
    }

    public String getRandomNickname() {
        try {
            return generateRandomNickname();
        } catch (JsonProcessingException e) {
            log.error("닉네임 생성에 실패했습니다.", e);
            throw new RuntimeException("닉네임 생성에 실패했습니다.", e);
        }
    }

    public String generateRandomNickname() throws JsonProcessingException {
        List<String> genres = getWordsFromRedis("genre");
        List<String> adjectives = getWordsFromRedis("adjective");
        List<String> animals = getWordsFromRedis("animal");

        String genre = genres.get(random.nextInt(genres.size()));
        String adjective = adjectives.get(random.nextInt(adjectives.size()));
        String animal = animals.get(random.nextInt(animals.size()));

        return genre + " " + adjective + " " + animal;
    }

    private List<String> getWordsFromRedis(String category) throws JsonProcessingException {
        String jsonData = redisTemplate.opsForValue().get(REDIS_KEY_PREFIX + category);
        return objectMapper.readValue(jsonData, new TypeReference<List<String>>() {});
    }
}
