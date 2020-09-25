package com.basicphones.sync.ContactModel;

import java.io.Serializable;

public class Event implements Serializable {

    private String eventDate;
    private int eventType;

    public Event(){

    }
    public Event(String date, int type){
        eventDate = date;
        eventType = type;
    }

    public void setEventDate(String date){
        eventDate = date;
    }
    public String getEventDate(){
        return eventDate;
    }
    public void setEventType(int type){
        eventType = type;
    }
    public int getEventType(){
        return eventType;
    }
}
