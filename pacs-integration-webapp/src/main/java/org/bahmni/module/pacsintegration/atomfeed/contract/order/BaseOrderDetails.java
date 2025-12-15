package org.bahmni.module.pacsintegration.atomfeed.contract.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseOrderDetails {
    private String uuid;
    private String action;
    private String orderNumber;
    private String urgency;
    private String accessionNumber;
    private String commentToFulfiller;
    private Date dateCreated;
}
