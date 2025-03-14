package com.onhz.server.dto.response;

import com.onhz.server.entity.CodeEntity;
import com.onhz.server.exception.NotFoundException;
import com.onhz.server.exception.example.ErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Builder
public class CodeResponse {
    private final Long id;
    private final String code;
    private final String name;

    public static CodeResponse from(CodeEntity entity) {
        if (entity == null) {
            throw new NotFoundException(ErrorCode.NOT_FOUND_EXCEPTION, "코드를 찾을 수 없습니다.");
        }
        return CodeResponse.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .build();
    }

    public static <T> List<CodeResponse> fromList(List<T> entities, Function<T, CodeEntity> mapper) {
        return entities.stream()
                .map(entity -> from(mapper.apply(entity)))
                .collect(Collectors.toList());
    }
}
