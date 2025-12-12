package org.bahmni.module.pacsintegration.atomfeed.contract.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDTO {
    private String uuid;
    private String name;
    private String display;
}
