package org.bahmni.module.pacsintegration.atomfeed.contract.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bahmni.module.pacsintegration.atomfeed.mappers.hl7.Constants;

import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSOrderDetails extends BaseOrderDetails{
    private Date dateCreated;
    private OrderType orderType;
    private OrderEncounter encounter;
    private List<Attribute> attributes;
    private OrderConcept concept;
    private Patient patient;
    private BaseOrderDetails previousOrder;
    private String type;

    public boolean isDiscontinuedOrder() {
        return this.getAction().equals(Constants.ORDER_ACTION_DISCONTINUE);
    }
}
