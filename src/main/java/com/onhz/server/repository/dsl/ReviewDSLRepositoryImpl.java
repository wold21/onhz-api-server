package com.onhz.server.repository.dsl;

import com.onhz.server.common.enums.ReviewType;
import com.onhz.server.dto.response.review.ReviewLatestResponse;
import com.onhz.server.dto.response.review.ReviewResponse;
import com.onhz.server.dto.response.UserResponse;
import com.onhz.server.entity.album.QAlbumEntity;
import com.onhz.server.entity.artist.QArtistEntity;
import com.onhz.server.entity.review.QReviewEntity;
import com.onhz.server.entity.review.QReviewLikeEntity;
import com.onhz.server.entity.review.ReviewEntity;
import com.onhz.server.entity.track.QTrackEntity;
import com.onhz.server.entity.user.QUserEntity;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReviewDSLRepositoryImpl implements ReviewDSLRepository {

    private final JPAQueryFactory queryFactory;
    private static final QReviewEntity review = new QReviewEntity("review"); // ✅ 전역변수 선언
    private final QReviewLikeEntity like = QReviewLikeEntity.reviewLikeEntity;
    private final QUserEntity user = QUserEntity.userEntity;

    private final QAlbumEntity album = QAlbumEntity.albumEntity;
    private final QArtistEntity artist = QArtistEntity.artistEntity;
    private final QTrackEntity track = QTrackEntity.trackEntity;

    private Expression<UserResponse> userProjection() {
        return ExpressionUtils.as(
                Projections.fields(UserResponse.class,
                        user.id,
                        user.email,
                        user.userName,
                        user.profilePath,
                        user.role
                ),
                "user"
        );
    }

    @Override
    public List<ReviewLatestResponse> findAllReviews(Pageable pageable) {
        QAlbumEntity albumForTrack = new QAlbumEntity("albumForTrack");
        return queryFactory
                .select(Projections.fields(ReviewLatestResponse.class,
                        review.id,
                        userProjection(),
                        review.content,
                        review.reviewType,
                        review.entityId,
                        review.createdAt,
                        review.updatedAt,
                        review.rating,
                        new CaseBuilder()
                                .when(review.reviewType.eq(ReviewType.ALBUM)).then(album.title.max())
                                .when(review.reviewType.eq(ReviewType.ARTIST)).then(artist.name.max())
                                .otherwise(track.trackName.max()).as("entityName"),
                        new CaseBuilder()
                                .when(review.reviewType.eq(ReviewType.ALBUM)).then(album.coverPath.max())
                                .when(review.reviewType.eq(ReviewType.ARTIST)).then(artist.profilePath.max())
                                .when(review.reviewType.eq(ReviewType.TRACK)).then(albumForTrack.coverPath.max())
                                .otherwise("").as("entityFilePath"),
                        like.id.countDistinct().intValue().as("likeCount")
                ))
                .from(review)
                .leftJoin(user).on(review.user.id.eq(user.id))
                .leftJoin(like).on(like.review.id.eq(review.id))
                .leftJoin(album).on(review.reviewType.eq(ReviewType.ALBUM).and(review.entityId.eq(album.id)))
                .leftJoin(artist).on(review.reviewType.eq(ReviewType.ARTIST).and(review.entityId.eq(artist.id)))
                .leftJoin(track).on(review.reviewType.eq(ReviewType.TRACK).and(review.entityId.eq(track.id)))
                .leftJoin(albumForTrack).on(review.reviewType.eq(ReviewType.TRACK).and(track.album.id.eq(albumForTrack.id)))
                .groupBy(review.id, user.id)
                .orderBy(review.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private BooleanExpression getIsLikedExpression(Long userId) {
        return userId != null
                ? queryFactory.select(like.id.isNotNull())
                .from(like)
                .where(like.review.id.eq(review.id)
                        .and(like.user.id.eq(userId)))
                .exists()
                : Expressions.FALSE;
    }

    private List<OrderSpecifier<?>> getOrderSpecifiers(Pageable pageable) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        pageable.getSort().forEach(order -> {
            PathBuilder<ReviewEntity> entityPath = new PathBuilder<>(ReviewEntity.class, "review");
                ComparableExpressionBase<?> path = entityPath.getComparable(order.getProperty(), Comparable.class);
                orderSpecifiers.add(new OrderSpecifier<>(order.isAscending() ? Order.ASC : Order.DESC, path));
        });

        if (orderSpecifiers.isEmpty()) {
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, review.createdAt));
        }

        return orderSpecifiers;
    }


    private JPAQuery<ReviewResponse> getReviewResponseQuery(Long userId, BooleanExpression condition) {
        BooleanExpression isLikedExpression = getIsLikedExpression(userId);

        JPAQuery<ReviewResponse> query = queryFactory
                .select(Projections.fields(ReviewResponse.class,
                        review.id,
                        userProjection(),
                        review.content,
                        review.reviewType,
                        review.entityId,
                        review.createdAt,
                        review.updatedAt,
                        review.rating,
                        like.id.countDistinct().intValue().as("likeCount"),
                        isLikedExpression.as("isLiked")
                ))
                .from(review)
                .leftJoin(like).on(like.review.id.eq(review.id))
                .leftJoin(user).on(review.user.id.eq(user.id))
                .where(condition)
                .groupBy(review.id, user.id);
        return query;

    }

    @Override
    public List<ReviewResponse> findReviewsWithLikesAndUserLike(ReviewType reviewType, Long entityId, Long userId, Pageable pageable) {
        JPAQuery<ReviewResponse> query = getReviewResponseQuery(userId, review.reviewType.eq(reviewType).and(review.entityId.eq(entityId)));
        List<OrderSpecifier<?>> orderSpecifiers = getOrderSpecifiers(pageable);
        if (!orderSpecifiers.isEmpty()) {
            query.orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]));
        }
        return query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public Optional<ReviewResponse> findReviewDetail(Long userId, Long reviewId) {
        return Optional.ofNullable(
                getReviewResponseQuery(userId, review.id.eq(reviewId))
                        .fetchOne()
        );
    }

    @Override
    public List<ReviewResponse> findUserReviews(ReviewType reviewType, Long userId, Pageable pageable) {
        JPAQuery<ReviewResponse> query = getReviewResponseQuery(userId, review.reviewType.eq(reviewType).and(review.user.id.eq(userId)));
        List<OrderSpecifier<?>> orderSpecifiers = getOrderSpecifiers(pageable);
        if (!orderSpecifiers.isEmpty()) {
            query.orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]));
        }
        return query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

}