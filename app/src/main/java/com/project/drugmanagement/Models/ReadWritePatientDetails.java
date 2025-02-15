package com.project.drugmanagement.Models;

import java.util.List;
import java.util.Map;

public class ReadWritePatientDetails {
    String aadharNo ;
    String patientName ;
    String address ;
    String contact ;
    List<Map<String,String>> prescriptions;

    public ReadWritePatientDetails(String aadharNo, String patientName, String address, String contact) {
        this.aadharNo = aadharNo;
        this.patientName = patientName;
        this.address = address;
        this.contact = contact;
    }

    public ReadWritePatientDetails() {
    }

    public String getAadharNo() {
        return aadharNo;
    }

    public void setAadharNo(String aadharNo) {
        this.aadharNo = aadharNo;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public List<Map<String, String>> getPrescriptions() {
        return prescriptions;
    }

    public void setPrescription(List<Map<String, String>> prescriptions) {
        this.prescriptions = prescriptions;
    }
}
