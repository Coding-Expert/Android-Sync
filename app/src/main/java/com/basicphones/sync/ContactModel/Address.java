package com.basicphones.sync.ContactModel;

import java.io.Serializable;

public class Address implements Serializable {

    private String addressName;
    private int addressType;

    public Address() {

    }

    public Address(String m_addressName, int m_addressType){
        addressName = m_addressName;
        addressType = m_addressType;
    }

    public void setAddressName(String m_addressName){
        addressName = m_addressName;
    }
    public String getAddressName(){
        return addressName;
    }
    public void setAddressType(int m_addressType) {
        addressType = m_addressType;
    }
    public int getAddressType() {
        return addressType;
    }
}
