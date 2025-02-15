package com.project.drugmanagement.Models;

import java.util.List;

public class ReadWriteDepoArray {
    List<String> depoNames;

    public ReadWriteDepoArray() {
        //required empty constructor
    }

    public List<String> getDepoNames() {
        return depoNames;
    }

    public void setDepoNames(List<String> depoNames) {
        this.depoNames = depoNames;
    }
}
