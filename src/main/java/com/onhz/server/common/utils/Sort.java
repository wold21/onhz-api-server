package com.onhz.server.common.utils;

import com.onhz.server.common.enums.OrderDirection;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Sort {
    private final List<OrderCondition> orders = new ArrayList<>();

    public Sort(String orderBy) {
        if(!StringUtils.hasText(orderBy)) {
            orders.add(new OrderCondition("created_at", OrderDirection.DESC));
            return;
        }

        String[] orderConditions = orderBy.split(",");
        for(String order : orderConditions) {
            if(order.startsWith("-")) {
                orders.add(new OrderCondition(order.substring(1), OrderDirection.DESC));
            } else {
                orders.add(new OrderCondition(order, OrderDirection.ASC));
            }
        }


    }


    public record OrderCondition(String field, OrderDirection direction) {} /*레코드 클래스*/
}
