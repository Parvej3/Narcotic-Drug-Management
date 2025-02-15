package com.project.drugmanagement.Models;

public class ReadWriteDoctorDetails {
    String name;
    String contact;
    String address;
    String qualification;


    String email;

    public ReadWriteDoctorDetails() {
        // required empty constructor
    }

    public ReadWriteDoctorDetails(String email , String name, String contact, String address, String qualification) {
        this.email = email;
        this.name = name;
        this.contact = contact;
        this.address = address;
        this.qualification = qualification;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
