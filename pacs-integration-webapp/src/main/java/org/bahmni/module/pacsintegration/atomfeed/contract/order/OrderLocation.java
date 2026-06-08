package org.bahmni.module.pacsintegration.atomfeed.contract.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderLocation {
    private String uuid;
    private String display;
    private String name;
    private List<LocationTag> tags;
    private List<Attribute> attributes;
}
