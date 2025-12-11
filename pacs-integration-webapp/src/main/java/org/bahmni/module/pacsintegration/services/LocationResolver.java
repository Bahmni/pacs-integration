package org.bahmni.module.pacsintegration.services;

import org.bahmni.module.pacsintegration.atomfeed.contract.order.OrderLocationInfo;
import org.bahmni.module.pacsintegration.atomfeed.contract.order.OpenMRSOrderDetails;
import org.springframework.stereotype.Service;

@Service
public interface LocationResolver {
    OrderLocationInfo resolveLocations(OpenMRSOrderDetails orderDetails);
}
