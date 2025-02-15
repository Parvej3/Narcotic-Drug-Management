package com.project.drugmanagement.Models;

import java.util.List;

public class ReadWriteProductsArray {
    List<String> productNames;

    public ReadWriteProductsArray() {
        //required empty constuctor
    }

    public List<String> getProductNames() {
        return productNames;
    }

    public void setProductNames(List<String> productNames) {
        this.productNames = productNames;
    }
}
