package com.onhz.server.service.common;

import com.onhz.server.dto.response.common.CodeResponse;
import com.onhz.server.dto.response.common.GenreFeaturedResponse;
import com.onhz.server.dto.response.common.GenreFeaturedSimpleResponse;
import com.onhz.server.entity.CommonCodeEntity;
import com.onhz.server.exception.ErrorCode;
import com.onhz.server.exception.NotFoundException;
import com.onhz.server.repository.CommonCodeRepository;
import com.onhz.server.repository.GenreFeaturedRepository;
import com.onhz.server.repository.GenreRepository;
import com.onhz.server.repository.SocialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommonService {
    private final GenreRepository genreRepository;
    private final SocialRepository socialRepository;
    private final CommonCodeRepository commonCodeRepository;
    private final GenreFeaturedRepository genreFeaturedRepository;

    public List<CodeResponse> getCodeByType(String code) {
        String lowerCode = code.toLowerCase();
        Sort sort = Sort.by(Sort.Order.asc("id"));
        List<CodeResponse> results = switch(lowerCode) {
            case "genre" -> CodeResponse.fromList(genreRepository.findAll(sort), genre -> genre);
            case "social" -> CodeResponse.fromList(socialRepository.findAll(sort), social -> social);
            default -> {
                try {
                    List<CommonCodeEntity> commonCodes = commonCodeRepository.findByCode(lowerCode);
                    if (commonCodes.isEmpty()) {
                        throw new NotFoundException(ErrorCode.NOT_FOUND_EXCEPTION, "코드를 찾을 수 없습니다.");
                    }
                    yield CodeResponse.fromList(commonCodes, commonCode -> commonCode);
                } catch (Exception e) {
                    if (e instanceof NotFoundException) {
                        throw e;
                    }
                    throw new IllegalArgumentException("잘못된 타입입니다");
                }
            }
        };
        return results;
    }

    public List<GenreFeaturedSimpleResponse> getGenreFeatures() {
        List<GenreFeaturedSimpleResponse> results = GenreFeaturedSimpleResponse.fromList(genreFeaturedRepository.findFeatureGenresWithRated());
        return results;
    }

    public GenreFeaturedResponse getGenreFeature(String genreCode) {
        String lowerCode = genreCode.toLowerCase();
        if(lowerCode.equals("r&b")){
            lowerCode = "rnb";
        } else if (lowerCode.equals("hiphop")){
            lowerCode = "hip hop";
        }
        return GenreFeaturedResponse.of(genreFeaturedRepository.findByCode(lowerCode)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_EXCEPTION, "장르를 찾을 수 없습니다.")));
    }

}
