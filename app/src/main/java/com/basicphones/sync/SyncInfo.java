package com.basicphones.sync;

public class SyncInfo {

    private String update_checksum;
    private String update_date;

    public SyncInfo(String checksum, String date) {
        update_checksum = checksum;
        update_date = date;
    }
    public SyncInfo() {

    }

    public void setUpdate_checksum(String checksum){
        update_checksum = checksum;
    }
    public String getUpdate_checksum(){
        return update_checksum;
    }
    public void setUpdate_date(String date){
        update_date = date;
    }
    public String getUpdate_date() {
        return update_date;
    }
}
