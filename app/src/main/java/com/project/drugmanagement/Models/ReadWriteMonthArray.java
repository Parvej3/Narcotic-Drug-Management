package com.project.drugmanagement.Models;

import java.util.List;

public class ReadWriteMonthArray {
    List<String> months;

    public ReadWriteMonthArray() {
        //required empty constructor
    }

    public List<String> getMonths() {
        return months;
    }

    public void setMonths(List<String> months) {
        this.months = months;
    }
}
