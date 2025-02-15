package com.project.drugmanagement.Models;

import java.util.List;

public class ReadWriteProductionDetails {
    String batchNo;
    String name;
    String mfgDate;
    String expDate;
    String Quantity;
    String pack;
    int total;



    public ReadWriteProductionDetails() {
        //required empty constructor
    }

    public ReadWriteProductionDetails(String batchNo, String name, String mfgDate, String expDate, String quantity, String pack, int total) {
        this.batchNo = batchNo;
        this.name = name;
        this.mfgDate = mfgDate;
        this.expDate = expDate;
        Quantity = quantity;
        this.pack = pack;
        this.total = total;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMfgDate() {
        return mfgDate;
    }

    public void setMfgDate(String mfgDate) {
        this.mfgDate = mfgDate;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
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
