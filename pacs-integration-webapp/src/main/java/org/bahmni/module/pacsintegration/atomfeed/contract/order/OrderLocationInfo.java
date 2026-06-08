package org.bahmni.module.pacsintegration.atomfeed.contract.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderLocationInfo {
    private LocationDTO fulfillingLocation;
    private LocationDTO sourceLocation;
}
