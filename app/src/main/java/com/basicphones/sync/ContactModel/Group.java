package com.basicphones.sync.ContactModel;

import java.io.Serializable;

public class Group implements Serializable {

    private String groupId;
    private String groupName;

    public Group(){

    }
    public Group(String id, String name){
        groupId = id;
        groupName = name;
    }

    public void setGroupId(String id){
        groupId = id;
    }

    public String getGroupId() {
        return groupId;
    }
    public void setGroupName(String name){
        groupName = name;
    }
    public String getGroupName(){
        return groupName;
    }
}
