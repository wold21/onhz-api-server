package com.onhz.server.repository;

import com.onhz.server.entity.user.NicknameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NicknameWordRepository extends JpaRepository<NicknameEntity, Long> {

    @Query(value = "SELECT word FROM nickname_word_tb WHERE category = :category", nativeQuery = true)
    List<String> findWordsByCategory(String category);
}
