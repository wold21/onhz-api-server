package com.onhz.server.service.common;

import com.onhz.server.dto.response.common.CodeResponse;
import com.onhz.server.dto.response.common.GenreCatalogResponse;
import com.onhz.server.dto.response.common.GenreCatalogSimpleResponse;
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
    @DisplayName("일반적인 코드 조회")
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
    void getGenreCatalogs(){
        //given
        //nothing
        //when
        List<GenreCatalogSimpleResponse> result = commonService.getGenreCatalogs();

        //then
        assert(!result.isEmpty());
    }

    @Test
    @DisplayName("주요 장르 데이터 조회")
    void getGenreCatalogDetail(){
        //given
        String type = "rock";

        //when
        GenreCatalogResponse result = commonService.getGenreCatalog(type);

        //then
        assert(result.getId() > 0);
    }

    @Test
    @DisplayName("잘못된 코드 조회")
    void getCodeByTypeWithError(){
        //given
        String type = "invalid";

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> commonService.getCodeByType(type));

        //then
        assertEquals("잘못된 타입입니다.", exception.getMessage());
    }

}
