package org.bahmni.module.pacsintegration.atomfeed.builders;

import org.bahmni.module.pacsintegration.model.OrderType;

public class OrderTypeBuilder {
    private OrderType orderType;

    public OrderTypeBuilder() {
        orderType = new OrderType();
    }

    public OrderTypeBuilder withName(String orderTypeName) {
        orderType.setName(orderTypeName);
        return this;
    }

    public OrderType build() {
        return orderType;
    }
}
