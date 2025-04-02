package com.onhz.server.repository.dsl;

import com.onhz.server.common.utils.QueryDslUtil;
import com.onhz.server.entity.album.AlbumEntity;
import com.onhz.server.entity.artist.ArtistEntity;
import com.onhz.server.entity.artist.QArtistEntity;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ArtistDSLRepositoryImpl implements ArtistDSLRepository{
    private final JPAQueryFactory queryFactory;
    private final QArtistEntity artistEntity = QArtistEntity.artistEntity;
    PathBuilder<ArtistEntity> entityPath = new PathBuilder<>(ArtistEntity.class, "artistEntity");

    @Override
    public List<Long> findAllIds(Long lastId, String lastOrderValue, Pageable pageable) {
        JPAQuery<Long> query = queryFactory
                .select(artistEntity.id)
                .from(artistEntity);
        if (lastId != null) {
            query.where(QueryDslUtil.buildCursorCondition(pageable, entityPath, lastId, lastOrderValue));
        }

        return query
                .orderBy(artistEntity.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public List<ArtistEntity> findArtistsByKeyword(String keyword, Long lastId, String lastOrderValue, Pageable pageable) {
        JPAQuery<ArtistEntity> query = queryFactory
                .selectFrom(artistEntity)
                .where(artistEntity.name.containsIgnoreCase(keyword));
        if (lastId != null) {
            query.where(QueryDslUtil.buildCursorCondition(pageable, entityPath, lastId, lastOrderValue));
        }
        return query
                .orderBy(QueryDslUtil.buildOrderSpecifiers(pageable, entityPath))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }
}
