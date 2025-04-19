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

    public static BooleanBuilder buildCursorCondition(Pageable pageable, PathBuilder<?> pathBuilder, Long lastId, String lastOrderValue) {
        BooleanBuilder cursorConditionBuilder = new BooleanBuilder();

        if (lastId != null && lastOrderValue != null) {
            pageable.getSort().forEach(order -> {
                ComparableExpressionBase<?> path = pathBuilder.getComparable(order.getProperty(), Comparable.class);
                Field fieldType;
                boolean isAscending = order.isAscending();
                try {
                    fieldType = ((Field)((ComparablePath) path).getAnnotatedElement());
                } catch (Exception e) {
                    throw new RuntimeException("Unable to retrieve field type", e);
                }
                addConditions(cursorConditionBuilder, path, lastOrderValue, lastId, pathBuilder, fieldType.getType(), isAscending);
            });
        }
        return cursorConditionBuilder;
    }

    private static void addConditions(BooleanBuilder builder, ComparableExpressionBase<?> path,
                                      String lastOrderValue, Long lastId, PathBuilder<?> pathBuilder, Class<?> fieldType, boolean isAscending) {
        try{
            if (fieldType.equals(String.class)) {
                addStringConditions(builder, (ComparableExpression<String>) path, lastOrderValue, lastId, pathBuilder, isAscending);
            } else if (fieldType.equals(Long.class) || fieldType.equals(Integer.class)) {
                addNumberConditions(builder, (ComparableExpression<Long>) path, lastOrderValue, lastId, pathBuilder, isAscending);
            } else if (fieldType.equals(Double.class)) {
                addDoubleConditions(builder, (ComparableExpression<Double>) path, lastOrderValue, lastId, pathBuilder, isAscending);
            } else if (fieldType.equals(LocalDateTime.class)) {
                addDateTimeConditions(builder, (ComparableExpression<LocalDateTime>) path, lastOrderValue, lastId, pathBuilder, isAscending);
            }
        } catch (NumberFormatException | DateTimeParseException e) {
            throw new IllegalArgumentException("잘못된 데이터 형식의 커서값입니다. " + lastOrderValue);
        }
    }

    private static void addStringConditions(BooleanBuilder builder, ComparableExpression<String> stringPath,
                                            String lastOrderValue, Long lastId, PathBuilder<?> pathBuilder, boolean isAscending) {
        String template = isAscending
                ? "({0} > {1} OR ({0} = {1} AND {2} < {3}))"  // ASC
                : "({0} < {1} OR ({0} = {1} AND {2} < {3}))"; // DESC
        builder.and(Expressions.booleanTemplate(
                template,
                stringPath, lastOrderValue, pathBuilder.getComparable("id", Long.class), lastId
        ));
    }

    private static void addNumberConditions(BooleanBuilder builder, ComparableExpression<Long> numberPath,
                                            String lastOrderValue, Long lastId, PathBuilder<?> pathBuilder, boolean isAscending) {
        Long numericValue = Long.parseLong(lastOrderValue);
        String template = isAscending
                ? "({0} > {1} OR ({0} = {1} AND {2} < {3}))"  // ASC
                : "({0} < {1} OR ({0} = {1} AND {2} < {3}))"; // DESC
        builder.and(Expressions.booleanTemplate(
                template,
                numberPath, numericValue, pathBuilder.getComparable("id", Long.class), lastId
        ));
    }

    private static void addDoubleConditions(BooleanBuilder builder, ComparableExpression<Double> doublePath,
                                            String lastOrderValue, Long lastId, PathBuilder<?> pathBuilder, boolean isAscending) {
        Double doubleValue = Double.parseDouble(lastOrderValue);
        String template = isAscending
                ? "({0} > {1} OR ({0} = {1} AND {2} < {3}))"  // ASC
                : "({0} < {1} OR ({0} = {1} AND {2} < {3}))"; // DESC
        builder.and(Expressions.booleanTemplate(
                template,
                doublePath, doubleValue, pathBuilder.getComparable("id", Long.class), lastId
        ));
    }

    private static void addDateTimeConditions(BooleanBuilder builder, ComparableExpression<LocalDateTime> datePath,
                                              String lastOrderValue, Long lastId, PathBuilder<?> pathBuilder, boolean isAscending) {
        LocalDateTime parsedDateTime = LocalDateTime.parse(lastOrderValue, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String template = isAscending
                ? "({0} > {1} OR ({0} = {1} AND {2} < {3}))"  // ASC
                : "({0} < {1} OR ({0} = {1} AND {2} < {3}))"; // DESC
        builder.and(Expressions.booleanTemplate(
                template,
                datePath, parsedDateTime, pathBuilder.getComparable("id", Long.class), lastId
        ));
    }

}
