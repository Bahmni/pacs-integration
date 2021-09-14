package org.bahmni.module.pacsintegration.atomfeed.contract.patient;

import java.util.Date;
import java.util.List;

public class OpenMRSPatient {
    private String patientId;
    private String givenName;
    private String middleName;
    private String familyName;
    private String gender;
    private String healthId;
    private String patientReferenceNumber;
    private List<CareContext> careContexts;
    private String phoneNumber;
    private Date birthDate;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getCareContextInfo(){
        return careContexts.get(0).getDisplay() + " record";
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getHealthId() {
        return healthId;
    }

    public void setHealthId(String healthId) {
        this.healthId = healthId;
    }

    public String getPatientReferenceNumber() {
        return patientReferenceNumber;
    }

    public void setPatientReferenceNumber(String patientReferenceNumber) {
        this.patientReferenceNumber = patientReferenceNumber;
    }

    public List<CareContext> getCareContexts() {
        return careContexts;
    }

    public void setCareContexts(List<CareContext> careContexts) {
        this.careContexts = careContexts;
    }
}
