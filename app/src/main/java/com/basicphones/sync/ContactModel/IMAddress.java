package com.basicphones.sync.ContactModel;

import java.io.Serializable;

public class IMAddress implements Serializable {

    private String imaddressName;
    private int imaddressType;


    public IMAddress(){

    }

    public IMAddress(String m_imaddressName, int m_imaddressType){
        imaddressName = m_imaddressName;
        imaddressType = m_imaddressType;
    }

    public void setImaddressName(String name) {
        imaddressName = name;

    }
    public String getImaddressName(){
        return imaddressName;
    }
    public void setImaddressType(int type) {
        imaddressType = type;
    }
    public int getImaddressType() {
        return imaddressType;
    }
}
