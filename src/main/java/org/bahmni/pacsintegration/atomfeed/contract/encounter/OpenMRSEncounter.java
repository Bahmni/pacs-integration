package org.bahmni.pacsintegration.atomfeed.contract.encounter;

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

    public void setTestOrders(List<OpenMRSOrder> orders) {
        this.testOrders = orders;
    }

    public boolean hasLabOrder() {
        for (OpenMRSOrder openMRSOrder : testOrders) {
            if (openMRSOrder.isLabOrder())
                return true;
        }
        return false;
    }

    public List<OpenMRSOrder> getLabOrders() {
        List<OpenMRSOrder> labOrders = new ArrayList<OpenMRSOrder>();
        for (OpenMRSOrder openMRSOrder : testOrders) {
            if (openMRSOrder.isLabOrder() && !openMRSOrder.isVoided())
                labOrders.add(openMRSOrder);
        }
        return labOrders;
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
}
