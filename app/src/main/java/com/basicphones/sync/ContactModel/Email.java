package com.basicphones.sync.ContactModel;

import java.io.Serializable;

public class Email implements Serializable {

    private String email;
    private int type;

    public Email() {

    }

    public Email(String e_email, int e_type) {
        email = e_email;
        type = e_type;
    }

    public void setEmail(String e_email) {
        email = e_email;
    }

    public String getEmail() {
        return email;
    }

    public void setType(int e_type) {
        type = e_type;
    }

    public int getType() {
        return  type;
    }
}
