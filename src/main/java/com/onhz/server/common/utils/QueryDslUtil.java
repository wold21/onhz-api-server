package com.onhz.server.common.utils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.*;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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

    public static BooleanBuilder buildCursorCondition(Pageable pageable, PathBuilder<?> pathBuilder, Long lastId, String lastValue) {
        BooleanBuilder cursorConditionBuilder = new BooleanBuilder();

        if (lastId != null && lastValue != null) {
            pageable.getSort().forEach(order -> {
                ComparableExpressionBase<?> path = pathBuilder.getComparable(order.getProperty(), Comparable.class);
                Field fieldType;
                try {
                    fieldType = ((Field)((ComparablePath) path).getAnnotatedElement());
                } catch (Exception e) {
                    throw new RuntimeException("Unable to retrieve field type", e);
                }
                addConditions(cursorConditionBuilder, path, lastValue, lastId, pathBuilder, fieldType.getType());
            });
        }
        return cursorConditionBuilder;
    }

    private static void addConditions(BooleanBuilder builder, ComparableExpressionBase<?> path,
                                      String lastValue, Long lastId, PathBuilder<?> pathBuilder, Class<?> fieldType) {
        try{
            if (fieldType.equals(String.class)) {
                addStringConditions(builder, (ComparableExpression<String>) path, lastValue, lastId, pathBuilder);
            } else if (fieldType.equals(Long.class) || fieldType.equals(Integer.class)) {
                addNumberConditions(builder, (ComparableExpression<Long>) path, lastValue, lastId, pathBuilder);
            } else if (fieldType.equals(Double.class)) {
                addDoubleConditions(builder, (ComparableExpression<Double>) path, lastValue, lastId, pathBuilder);
            } else if (fieldType.equals(LocalDateTime.class)) {
                addDateTimeConditions(builder, (ComparableExpression<LocalDateTime>) path, lastValue, lastId, pathBuilder);
            }
        } catch (NumberFormatException | DateTimeParseException e) {
            throw new IllegalArgumentException("잘못된 데이터 형식의 커서값입니다. " + lastValue);
        }
    }

    private static void addStringConditions(BooleanBuilder builder, ComparableExpression<String> stringPath,
                                            String lastValue, Long lastId, PathBuilder<?> pathBuilder) {
        builder.and(Expressions.booleanTemplate(
                "({0} < {1} OR ({0} = {1} AND {2} < {3}))",
                stringPath, lastValue, pathBuilder.getComparable("id", Long.class), lastId
        ));
    }

    private static void addNumberConditions(BooleanBuilder builder, ComparableExpression<Long> numberPath,
                                            String lastValue, Long lastId, PathBuilder<?> pathBuilder) {
        Long numericValue = Long.parseLong(lastValue);
        builder.and(Expressions.booleanTemplate(
                "({0} < {1} OR ({0} = {1} AND {2} < {3}))",
                numberPath, numericValue, pathBuilder.getComparable("id", Long.class), lastId
        ));
    }

    private static void addDoubleConditions(BooleanBuilder builder, ComparableExpression<Double> doublePath,
                                            String lastValue, Long lastId, PathBuilder<?> pathBuilder) {
        Double doubleValue = Double.parseDouble(lastValue);
        builder.and(Expressions.booleanTemplate(
                "({0} < {1} OR ({0} = {1} AND {2} < {3}))",
                doublePath, doubleValue, pathBuilder.getComparable("id", Long.class), lastId
        ));
    }

    private static void addDateTimeConditions(BooleanBuilder builder, ComparableExpression<LocalDateTime> datePath,
                                              String lastValue, Long lastId, PathBuilder<?> pathBuilder) {
        LocalDateTime parsedDateTime = LocalDateTime.parse(lastValue, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        builder.and(Expressions.booleanTemplate(
                "({0} < {1} OR ({0} = {1} AND {2} < {3}))",
                datePath, parsedDateTime, pathBuilder.getComparable("id", Long.class), lastId
        ));
    }

}
