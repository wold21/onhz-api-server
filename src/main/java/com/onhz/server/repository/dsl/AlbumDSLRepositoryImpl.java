package com.onhz.server.repository.dsl;

import com.onhz.server.entity.QGenreEntity;
import com.onhz.server.entity.album.*;
import com.onhz.server.entity.artist.ArtistAlbumEntity;
import com.onhz.server.entity.artist.QArtistAlbumEntity;
import com.onhz.server.entity.artist.QArtistEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AlbumDSLRepositoryImpl implements AlbumDSLRepository{
    private final JPAQueryFactory queryFactory;
    private final QAlbumEntity albumEntity = QAlbumEntity.albumEntity;
    private final QAlbumGenreEntity albumGenre = QAlbumGenreEntity.albumGenreEntity;
    private final QGenreEntity genre = QGenreEntity.genreEntity;
    private final QArtistAlbumEntity albumArtist = QArtistAlbumEntity.artistAlbumEntity;
    private final QArtistEntity artist = QArtistEntity.artistEntity;
    @Override
    public List<AlbumEntity> findByIdInWithGenresAndArtists(List<Long> ids) {
        List<AlbumEntity> albums = queryFactory
                .selectFrom(albumEntity)
                .where(albumEntity.id.in(ids))
                .fetch();

        if (albums.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> albumIds = albums.stream()
                .map(AlbumEntity::getId)
                .collect(Collectors.toList());

        List<AlbumGenreEntity> albumGenres = queryFactory
                .selectFrom(albumGenre)
                .join(albumGenre.genre, genre).fetchJoin()
                .where(albumGenre.album.id.in(albumIds))
                .fetch();

        List<ArtistAlbumEntity> albumArtists = queryFactory
                .selectFrom(albumArtist)
                .join(albumArtist.artist, artist).fetchJoin()
                .where(albumArtist.album.id.in(albumIds))
                .fetch();

        Map<Long, List<AlbumGenreEntity>> albumGenresByAlbumId = albumGenres.stream()
                .collect(Collectors.groupingBy(ag -> ag.getAlbum().getId()));

        Map<Long, List<ArtistAlbumEntity>> albumArtistsByAlbumId = albumArtists.stream()
                .collect(Collectors.groupingBy(aa -> aa.getAlbum().getId()));

        for (AlbumEntity album : albums) {
            Long albumId = album.getId();
            album.setAlbumGenres(albumGenresByAlbumId.getOrDefault(albumId, Collections.emptyList()));
            album.setAlbumArtists(albumArtistsByAlbumId.getOrDefault(albumId, Collections.emptyList()));
        }

        Map<Long, AlbumEntity> albumMap = albums.stream()
                .collect(Collectors.toMap(AlbumEntity::getId, Function.identity()));

        return ids.stream()
                .map(albumMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Data
    @AllArgsConstructor
    private static class AlbumGenreMapping {
        private Long albumId;
        private Long genreId;
    }

    @Data
    @AllArgsConstructor
    private static class AlbumArtistMapping {
        private Long albumId;
        private Long artistId;
    }
}


