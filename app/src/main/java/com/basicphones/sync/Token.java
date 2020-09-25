package com.basicphones.sync;

public class Token {

    private String Account_Number;
    private String Authentication;

    public Token(String imei, String authentication){
        Account_Number = imei;
        Authentication = authentication;
    }
    public Token(){

    }

    public void setAccount_Number(String imei){
        Account_Number = imei;
    }
    public String getAccount_Number(){
        return Account_Number;
    }
    public void setAuthentication(String authentication){
        Authentication = authentication;
    }
    public String getAuthentication(){
        return Authentication;
    }
}
