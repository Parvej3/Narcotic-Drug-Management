package com.project.drugmanagement.Models;

public class ReadWriteTransactionDetails {
    String depoName ;
    String invoiceNo;
    String invoiceDate;
    String productName;
    String mfgDate;
    String batch;
    String quantity;
    String pack;
    int total;
    String mfgName;


    public ReadWriteTransactionDetails() {
        //required empty constructor
    }



    public ReadWriteTransactionDetails(String depoName, String invoiceNo, String invoiceDate, String productName, String mfgName, String mfgDate, String batch, String quantity, String pack, int total) {
        this.depoName = depoName;
        this.invoiceNo = invoiceNo;
        this.invoiceDate = invoiceDate;
        this.productName = productName;
        this.mfgDate = mfgDate;
        this.batch = batch;
        this.quantity = quantity;
        this.pack = pack;
        this.total = total;
        this.mfgName = mfgName;
    }

    public String getDepoName() {
        return depoName;
    }

    public void setDepoName(String depoName) {
        this.depoName = depoName;
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

    public String getMfgDate() {
        return mfgDate;
    }

    public void setMfgDate(String mfgDate) {
        this.mfgDate = mfgDate;
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
    public String getMfgName() {
        return mfgName;
    }

    public void setMfgName(String mfgName) {
        this.mfgName = mfgName;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
