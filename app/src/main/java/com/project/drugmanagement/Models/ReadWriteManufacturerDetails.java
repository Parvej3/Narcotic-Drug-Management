package com.project.drugmanagement.Models;

import java.util.List;

public class ReadWriteManufacturerDetails {

    String dlno;
    String email;
    String name;
    String address;
    String contact;

    List<String> products;


    public ReadWriteManufacturerDetails() {
        // required empty constructor
    }

    public ReadWriteManufacturerDetails(String email, String name, String contact, String address, String dlno) {
        this.email = email;
        this.name = name;
        this.dlno = dlno;
        this.address = address;
        this.contact = contact;
    }

    public List<String> getProducts() {
        return products;
    }

    public void setProducts(List<String> products) {
        this.products = products;
    }

    public String getDlno() {
        return dlno;
    }

    public void setDlno(String dlno) {
        this.dlno = dlno;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
