package com.project.drugmanagement.Models;

public class ReadWriteUserDetails {
    
    String dlno;
    String email;
    String name;
    String address;
    String contact ;


    public ReadWriteUserDetails() {
        // required empty constructor
    }

    public ReadWriteUserDetails(String email, String name , String dlno, String address, String contact) {
        this.email = email;
        this.name = name;
        this.dlno = dlno;
        this.address = address;
        this.contact  = contact;
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
