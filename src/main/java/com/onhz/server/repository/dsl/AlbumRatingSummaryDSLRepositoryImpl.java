package com.onhz.server.repository.dsl;

import com.onhz.server.entity.QGenreEntity;
import com.onhz.server.entity.album.QAlbumEntity;
import com.onhz.server.entity.album.QAlbumGenreEntity;
import com.onhz.server.entity.album.QAlbumRatingSummaryEntity;
import com.onhz.server.entity.artist.QArtistAlbumEntity;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AlbumRatingSummaryDSLRepositoryImpl implements AlbumRatingSummaryDSLRepository{
    private final JPAQueryFactory queryFactory;
    private final QAlbumRatingSummaryEntity albumRatingSummaryEntity = QAlbumRatingSummaryEntity.albumRatingSummaryEntity;
    private final QArtistAlbumEntity artistAlbumEntity = QArtistAlbumEntity.artistAlbumEntity;
    private final QAlbumEntity albumEntity = QAlbumEntity.albumEntity;
    private final QAlbumGenreEntity albumGenreEntity = QAlbumGenreEntity.albumGenreEntity;
    private final QGenreEntity genreEntity = QGenreEntity.genreEntity;

    @Override
    public Page<Long> findAllIdsWithRatingAndGenre(String genreCode, Pageable pageable) {
        BooleanExpression genreCodeCondition = genreCodeCondition(genreCode.toLowerCase());
        List<Tuple> dataList = queryFactory
                .select(
                        albumRatingSummaryEntity.album.id,
                        albumRatingSummaryEntity.ratingCount,
                        albumRatingSummaryEntity.averageRating
                )
                .distinct()
                .from(albumRatingSummaryEntity)
                .leftJoin(albumRatingSummaryEntity.album, albumEntity)
                .leftJoin(albumEntity.albumGenres, albumGenreEntity)
                .leftJoin(albumGenreEntity.genre, genreEntity)
                .where(genreCodeCondition)
                .orderBy(
                        albumRatingSummaryEntity.ratingCount.desc(),
                        albumRatingSummaryEntity.averageRating.desc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<Long> content = dataList.stream()
                .map(data -> data.get(albumRatingSummaryEntity.album.id))
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, 0L);
    }


    @Override
    public List<Long> findAlbumIdsByArtistIdWithRating(Long artistId, Pageable pageable) {
        List<Tuple> dataList = queryFactory
                .select(
                        albumRatingSummaryEntity.album.id,
                        albumRatingSummaryEntity.ratingCount,
                        albumRatingSummaryEntity.averageRating
                )
                .distinct()
                .from(albumRatingSummaryEntity)
                .leftJoin(artistAlbumEntity)
                .on(artistAlbumEntity.album.eq(albumRatingSummaryEntity.album))
                .where(artistAlbumEntity.artist.id.eq(artistId))
                .orderBy(
                        albumRatingSummaryEntity.ratingCount.desc(),
                        albumRatingSummaryEntity.averageRating.desc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        List<Long> result = dataList.stream()
                .map(data -> data.get(albumRatingSummaryEntity.album.id))
                .collect(Collectors.toList());

        return result;
    }

    private BooleanExpression genreCodeCondition(String genreCode) {
        /**
         * 장르코드에 따른 조건분기
         * 조건
         * 1. 앨범의 장르가 k-pop만을 가지고 있을 경우 pop에 검색되면 안됨.
         * 2. kpop은 k-pop에 검색 되어야 함.
         * 3. j-pop은 pop에 검색 되어야 함.
         * 4. hiphop은 hip hop에 검색 되어야 함.
         * 5. 이외에 복합 장르일 경우 띄어쓰기를 기준으로 뒤에 코드와 파라미터 값이 like문으로 비교되어야 함.
         */
        if ("pop".equalsIgnoreCase(genreCode)) {
            return genreEntity.code.lower().eq("pop")
                    .or(genreEntity.code.lower().like("% pop"))
                    .or(genreEntity.code.lower().like("%-pop"))
                    .or(genreEntity.code.lower().like("j-pop"))
                    .and(genreEntity.code.lower().notLike("%k-pop%"));
        }
        else if ("hip hop".equalsIgnoreCase(genreCode) || "hiphop".equalsIgnoreCase(genreCode)) {
            return genreEntity.code.lower().eq("hip hop")
                    .or(genreEntity.code.lower().eq("hiphop"))
                    .or(genreEntity.code.lower().like("% hip hop"))
                    .or(genreEntity.code.lower().like("% hiphop"));
        }
        else if ("kpop".equalsIgnoreCase(genreCode) || "k-pop".equalsIgnoreCase(genreCode)) {
            return genreEntity.code.lower().eq("kpop")
                    .or(genreEntity.code.lower().eq("k-pop"));
        }
        else {
            return genreEntity.code.lower().eq(genreCode)
                    .or(genreEntity.code.lower().like("% " + genreCode))
                    .or(genreEntity.code.lower().like("%-" + genreCode));
        }
    }
}
