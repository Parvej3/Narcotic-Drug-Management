package com.project.drugmanagement.Models;

public class ReadWriteTransactionRetailerDetails {
    String invoiceNo ;
    String aadharNo ;
    String patientName ;
    String address ;
    String contact ;
    String date ;
    String doctor ;
    String productName;
    String quantity;
    String batch;
    String pack ;
    int total;


    public ReadWriteTransactionRetailerDetails() {

    }

    public ReadWriteTransactionRetailerDetails(String invoiceNo, String aadharNo, String patientName, String address, String contact, String date, String doctor, String productName, String quantity, String batch, String pack, int total) {
        this.invoiceNo = invoiceNo;
        this.aadharNo = aadharNo;
        this.patientName = patientName;
        this.address = address;
        this.contact = contact;
        this.date = date;
        this.doctor = doctor;
        this.productName = productName;
        this.quantity = quantity;
        this.batch = batch;
        this.pack = pack;
        this.total = total;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getPack() {
        return pack;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
