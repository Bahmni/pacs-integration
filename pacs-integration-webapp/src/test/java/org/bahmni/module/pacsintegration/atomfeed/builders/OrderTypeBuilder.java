/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

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
