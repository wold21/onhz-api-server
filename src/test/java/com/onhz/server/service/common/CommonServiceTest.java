package com.onhz.server.service.common;

import com.onhz.server.dto.response.common.CodeResponse;
import com.onhz.server.dto.response.common.GenreFeaturedResponse;
import com.onhz.server.dto.response.common.GenreFeaturedSimpleResponse;
import com.onhz.server.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional(readOnly = true)
public class CommonServiceTest {
    @Autowired
    private CommonService commonService;

    @Test
    @DisplayName("특정 코드 조회")
    void getCodeByType(){
        //given
        String type = "genre";

        //when
        List<CodeResponse> result = commonService.getCodeByType(type);

        //then
        assert(!result.isEmpty());
    }

    @Test
    @DisplayName("주요 장르 코드 조회")
    void getGenreFeatures(){
        //given
        //nothing
        //when
        List<GenreFeaturedSimpleResponse> result = commonService.getGenreFeatures();

        //then
        assert(!result.isEmpty());
    }

    @Test
    @DisplayName("주요 장르 데이터 조회")
    void getGenreFeature(){
        //given
        String type = "rock";

        //when
        GenreFeaturedResponse result = commonService.getGenreFeature(type);

        //then
        assert(result.getId() > 0);
    }

    @Test
    @DisplayName("잘못된 코드 조회")
    void getCodeByTypeWithError(){
        //given
        String type = "invalid";

        //when
        NotFoundException exception = assertThrows(NotFoundException.class, () -> commonService.getCodeByType(type));

        //then
        assertEquals("코드를 찾을 수 없습니다.", exception.getMessage());
    }

}
