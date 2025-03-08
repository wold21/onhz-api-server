package com.onhz.server.service.album;


import com.onhz.server.common.utils.SortUtils;
import com.onhz.server.dto.response.AlbumGenreResponse;
import com.onhz.server.entity.album.AlbumEntity;
import com.onhz.server.repository.album.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumRepository albumRepository;

    public List<AlbumGenreResponse> getAlbums(int offset, int limit, String orderBy) {
        Pageable pageable = SortUtils.createPageable(offset, limit, orderBy);
        boolean isRating = orderBy.contains("rating");

        Page<AlbumEntity> albumPage = isRating ? albumRepository.findAllWithRatings(pageable) : albumRepository.findAllWithGenres(pageable);
        List<AlbumGenreResponse> response = albumPage.getContent()
                .stream()
                .map(AlbumGenreResponse::from)
                .collect(Collectors.toList());
        return response;
    }

}
