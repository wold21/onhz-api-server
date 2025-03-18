package com.onhz.server.service.common;

import com.onhz.server.dto.response.CodeResponse;
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
public class CodeServiceTest {
    @Autowired
    private CodeService codeService;
    void resultToString(List<CodeResponse> codes){
        System.out.println("- 코드 정보\t(Id / code / name)");
        for(CodeResponse code : codes){
            System.out.println("\t\t\t" + String.format("%d / %s / %s",
                    code.getId(),
                    code.getCode(),
                    code.getName()));
        }

    }

    @Test
    @DisplayName("일반적인 코드 조회")
    void getCodeByType(){
        //given
        String type = "genre";

        //when
        List<CodeResponse> result = codeService.getCodeByType(type);

        //then
        assert(!result.isEmpty());

        resultToString(result);
    }

    @Test
    @DisplayName("잘못된 코드 조회")
    void getCodeByTypeWithError(){
        //given
        String type = "invalid";

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> codeService.getCodeByType(type));

        //then
        assertEquals("잘못된 타입입니다.", exception.getMessage());
    }

}
