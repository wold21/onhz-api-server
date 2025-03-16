package com.onhz.server.repository.dsl;

import com.onhz.server.common.enums.ReviewType;
import com.onhz.server.dto.response.dsl.ReviewResponse;
import com.onhz.server.dto.response.dsl.UserResponse;
import com.onhz.server.entity.review.QReviewEntity;
import com.onhz.server.entity.review.QReviewLikeEntity;
import com.onhz.server.entity.user.QUserEntity;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReviewDSLRepositoryImpl implements ReviewDSLRepository {

    private final JPAQueryFactory queryFactory;
    private final QReviewEntity review = QReviewEntity.reviewEntity;
    private final QReviewLikeEntity like = QReviewLikeEntity.reviewLikeEntity;
    private final QUserEntity user = QUserEntity.userEntity;

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
    public List<ReviewResponse> findAllReviews(Pageable pageable) {
        return queryFactory
                .select(Projections.fields(ReviewResponse.class,
                        review.id,
                        userProjection(),
                        review.content,
                        review.reviewType,
                        review.entityId,
                        review.createdAt,
                        review.updatedAt,
                        review.rating
                    ))
                .from(review)
                .leftJoin(user).on(review.user.id.eq(user.id))
                .orderBy(review.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }


    @Override
    public List<ReviewResponse> findReviewsWithLikesAndUserLike(ReviewType reviewType, Long entityId, Long userId, Pageable pageable) {
        return queryFactory
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
                        queryFactory.select(like.id.isNotNull())
                                .from(like)
                                .where(like.review.id.eq(review.id)
                                        .and(like.user.id.eq(userId)))
                                .exists().as("isLiked")
                ))
                .from(review)
                .leftJoin(like).on(like.review.id.eq(review.id))
                .leftJoin(user).on(review.user.id.eq(user.id))
                .where(review.reviewType.eq(reviewType)
                        .and(review.entityId.eq(entityId)))
                .groupBy(review.id, user.id, user.email, user.userName, user.profilePath, user.role)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public Optional<ReviewResponse> findReviewDetail(Long userId, Long reviewId) {
        return Optional.ofNullable(queryFactory
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
                        queryFactory.select(like.id.isNotNull())
                                .from(like)
                                .where(like.review.id.eq(review.id)
                                        .and(like.user.id.eq(userId)))
                                .exists().as("isLiked")
                ))
                .from(review)
                .leftJoin(like).on(like.review.id.eq(review.id))
                .leftJoin(user).on(review.user.id.eq(user.id))
                .where(review.id.eq(reviewId))
                .groupBy(review.id, user.id, user.userName)
                .fetchOne());
    }
}