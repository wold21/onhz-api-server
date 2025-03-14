package com.onhz.server.service.common;

import com.onhz.server.dto.response.CodeResponse;
import com.onhz.server.repository.GenreRepository;
import com.onhz.server.repository.SocialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CodeService {
    private final GenreRepository genreRepository;
    private final SocialRepository socialRepository;

    public List<CodeResponse> getCodeByType(String type) {
        Sort sort = Sort.by(Sort.Order.asc("id"));

        return switch(type.toLowerCase()) {
            case "genre" -> CodeResponse.fromList(genreRepository.findAll(sort), genre -> genre);
            case "social" -> CodeResponse.fromList(socialRepository.findAll(sort), social -> social);
            default -> throw new IllegalArgumentException("잘못된 타입입니다.");
        };
    }

}
