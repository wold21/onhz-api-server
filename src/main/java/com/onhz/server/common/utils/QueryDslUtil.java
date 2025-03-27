package com.onhz.server.common.utils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.*;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class QueryDslUtil {


    public static OrderSpecifier<?>[] buildOrderSpecifiers(Pageable pageable, PathBuilder<?> pathBuilder) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        pageable.getSort().forEach(order -> {
            ComparableExpressionBase<?> path = pathBuilder.getComparable(order.getProperty(), Comparable.class);
            orderSpecifiers.add(new OrderSpecifier<>(order.isAscending() ? Order.ASC : Order.DESC, path));
        });

        orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, pathBuilder.getComparable("id", Comparable.class)));
        return orderSpecifiers.toArray(new OrderSpecifier[0]);
    }

    public static BooleanBuilder buildCursorCondition(Pageable pageable, PathBuilder<?> pathBuilder, Long cursorId, String cursorValue) {
        BooleanBuilder cursorConditionBuilder = new BooleanBuilder();

        if (cursorId != null && cursorValue != null) {
            pageable.getSort().forEach(order -> {
                ComparableExpressionBase<?> path = pathBuilder.getComparable(order.getProperty(), Comparable.class);
                Field fieldType;
                try {
                    fieldType = ((Field)((ComparablePath) path).getAnnotatedElement());
                } catch (Exception e) {
                    throw new RuntimeException("Unable to retrieve field type", e);
                }
                addConditions(cursorConditionBuilder, path, cursorValue, cursorId, pathBuilder, fieldType.getType());
            });
        }
        return cursorConditionBuilder;
    }

    private static void addConditions(BooleanBuilder builder, ComparableExpressionBase<?> path,
                                      String cursorValue, Long cursorId, PathBuilder<?> pathBuilder, Class<?> fieldType) {
        if (fieldType.equals(String.class)) {
            addStringConditions(builder, (ComparableExpression<String>) path, cursorValue, cursorId, pathBuilder);
        } else if (fieldType.equals(Long.class) || fieldType.equals(Integer.class)) {
            addNumberConditions(builder, (ComparableExpression<Long>) path, cursorValue, cursorId, pathBuilder);
        } else if (fieldType.equals(double.class)) {
            addDoubleConditions(builder, (ComparableExpression<Double>) path, cursorValue, cursorId, pathBuilder);
        } else if (fieldType.equals(LocalDateTime.class)) {
            addDateTimeConditions(builder, (ComparableExpression<LocalDateTime>) path, cursorValue, cursorId, pathBuilder);
        }
    }

    private static void addStringConditions(BooleanBuilder builder, ComparableExpression<String> stringPath,
                                            String cursorValue, Long cursorId, PathBuilder<?> pathBuilder) {
        builder.and(Expressions.booleanTemplate(
                "({0} < {1} OR ({0} = {1} AND {2} > {3}))",
                stringPath, cursorValue, pathBuilder.getComparable("id", Long.class), cursorId
        ));
    }

    private static void addNumberConditions(BooleanBuilder builder, ComparableExpression<Long> numberPath,
                                            String cursorValue, Long cursorId, PathBuilder<?> pathBuilder) {
        Long numericValue = Long.parseLong(cursorValue);
        builder.and(Expressions.booleanTemplate(
                "({0} < {1} OR ({0} = {1} AND {2} > {3}))",
                numberPath, numericValue, pathBuilder.getComparable("id", Long.class), cursorId
        ));
    }

    private static void addDoubleConditions(BooleanBuilder builder, ComparableExpression<Double> doublePath,
                                            String cursorValue, Long cursorId, PathBuilder<?> pathBuilder) {
        Double doubleValue = Double.parseDouble(cursorValue);
        builder.and(Expressions.booleanTemplate(
                "({0} < {1} OR ({0} = {1} AND {2} > {3}))",
                doublePath, doubleValue, pathBuilder.getComparable("id", Long.class), cursorId
        ));
    }

    private static void addDateTimeConditions(BooleanBuilder builder, ComparableExpression<LocalDateTime> datePath,
                                              String cursorValue, Long cursorId, PathBuilder<?> pathBuilder) {
        LocalDateTime parsedDateTime = LocalDateTime.parse(cursorValue, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        builder.and(Expressions.booleanTemplate(
                "({0} < {1} OR ({0} = {1} AND {2} > {3}))",
                datePath, parsedDateTime, pathBuilder.getComparable("id", Long.class), cursorId
        ));
    }

}
