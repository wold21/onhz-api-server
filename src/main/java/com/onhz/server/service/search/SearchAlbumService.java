package com.onhz.server.service.search;

import com.onhz.server.common.utils.PageUtils;
import com.onhz.server.dto.response.album.AlbumResponse;
import com.onhz.server.entity.album.AlbumEntity;
import com.onhz.server.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchAlbumService {
    private final AlbumRepository albumRepository;

    public List searchAlbum(String keyword, Long lastId, String lastOrderValue, int limit, String orderBy) {
        Pageable pageable = PageUtils.createPageable(0, limit, orderBy, AlbumEntity.class);
        List<AlbumEntity> albums = albumRepository.findAlbumsByKeyword(keyword, lastId, lastOrderValue, pageable);
        return albums.stream().map(AlbumResponse::from).toList();
    }
}
