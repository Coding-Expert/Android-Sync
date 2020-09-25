package com.basicphones.sync.ContactModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Phone implements Serializable {

    private String number;
    private int type;

    public Phone() {

    }

    public Phone(String p_number, int p_type) {
        number = p_number;
        type = p_type;
    }

    public void setNumber(String m_number){
        number = m_number;
    }

    public String getNumber() {
        return number;
    }
    public void setType(int m_type){
        type = m_type;
    }

    public int getType() {
        return type;
    }


}
