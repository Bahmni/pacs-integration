package org.bahmni.module.pacsintegration.atomfeed.contract.encounter;

import org.bahmni.module.pacsintegration.model.OrderType;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSEncounter {
    private String encounterUuid;
    private String patientUuid;
    private List<OpenMRSOrder> testOrders = new ArrayList<OpenMRSOrder>();
    private List<OpenMRSProvider> providers = new ArrayList<OpenMRSProvider>();

    public OpenMRSEncounter() {
    }

    public OpenMRSEncounter(String encounterUuid, String patientUuid, List<OpenMRSOrder> testOrders, List<OpenMRSProvider> providers) {

        this.encounterUuid = encounterUuid;
        this.testOrders = testOrders;
        this.patientUuid = patientUuid;
        this.providers = providers;
    }

    public void setEncounterUuid(String encounterUuid) {
        this.encounterUuid = encounterUuid;
    }

    public String getEncounterUuid() {
        return encounterUuid;
    }

    public List<OpenMRSOrder> getTestOrders() {
        return testOrders;
    }

    public List<OpenMRSOrder> getAcceptableTestOrders(List<OrderType> acceptableOrderTypes) {
        List<OpenMRSOrder> acceptableNewOrders = new ArrayList<OpenMRSOrder>();
        for(OpenMRSOrder openMRSOrder : testOrders) {
            OrderType acceptableOrderType = findOrderType(acceptableOrderTypes, openMRSOrder.getOrderType());
            if (acceptableOrderType != null) {
                acceptableNewOrders.add(openMRSOrder);
            }
        }
        return acceptableNewOrders;
    }

    public void setTestOrders(List<OpenMRSOrder> orders) {
        this.testOrders = orders;
    }

    public String getPatientUuid() {
        return patientUuid;
    }

    public void setPatientUuid(String patientUuid) {
        this.patientUuid = patientUuid;
    }

    public List<OpenMRSProvider> getProviders() {
        return providers;
    }

    public void setProviders(List<OpenMRSProvider> providers) {
        this.providers = providers;
    }

    public void addTestOrder(OpenMRSOrder order) {
        testOrders.add(order);
    }

    private OrderType findOrderType(List<OrderType> acceptableOrderTypes, String orderType) {
        for (OrderType acceptableOrderType : acceptableOrderTypes) {
            if (acceptableOrderType.getName().equals(orderType)) {
                return acceptableOrderType;
            }
        }
        return null;
    }

}
