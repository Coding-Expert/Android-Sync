package com.basicphones.sync.ContactModel;

import java.io.Serializable;

public class Company implements Serializable {

    private String organization;
    private String title;

    public Company() {

    }

    public Company(String c_organization, String c_title){
        organization = c_organization;
        title = c_title;
    }

    public void setOrganization(String c_organization){
        organization = c_organization;
    }

    public String getOrganization() {
        return organization;
    }

    public void setTitle(String c_title) {
        title = c_title;
    }

    public String getTitle(){
        return title;
    }
}
