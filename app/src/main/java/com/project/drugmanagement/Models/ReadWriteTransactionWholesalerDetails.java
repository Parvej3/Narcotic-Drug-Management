package com.project.drugmanagement.Models;

public class ReadWriteTransactionWholesalerDetails {

    String wholesalerName ;
    String retailerName;
    String invoiceNo;
    String invoiceDate;
    String productName;
    String batch;
    String quantity;
    String pack;
    int total;

    public ReadWriteTransactionWholesalerDetails() {
        //required empty constructor
    }

    public ReadWriteTransactionWholesalerDetails(String wholesalerName, String retailerName, String invoiceNo, String invoiceDate, String productName, String batch, String quantity, String pack, int total) {

        this.wholesalerName = wholesalerName;
        this.retailerName = retailerName;
        this.invoiceNo = invoiceNo;
        this.invoiceDate = invoiceDate;
        this.productName = productName;
        this.batch = batch;
        this.quantity = quantity;
        this.pack = pack;
        this.total = total;
    }

    public String getWholesalerName() {
        return wholesalerName;
    }

    public void setWholesalerName(String wholesalerName) {
        this.wholesalerName = wholesalerName;
    }

    public String getRetailerName() {
        return retailerName;
    }

    public void setRetailerName(String retailerName) {
        this.retailerName = retailerName;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
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
