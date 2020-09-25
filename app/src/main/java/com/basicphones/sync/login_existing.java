package com.basicphones.sync;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.basicphones.sync.ContactModel.Address;
import com.basicphones.sync.ContactModel.Company;
import com.basicphones.sync.ContactModel.Event;
import com.basicphones.sync.ContactModel.IMAddress;
import com.hbb20.CountryCodePicker;
import com.jaredrummler.android.device.DeviceName;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import jagerfield.mobilecontactslibrary.Contact.Contact;
import jagerfield.mobilecontactslibrary.ContactFields.AddressField;
import jagerfield.mobilecontactslibrary.ElementContainers.AddressContainer;
import jagerfield.mobilecontactslibrary.ElementContainers.EmailContainer;
import jagerfield.mobilecontactslibrary.ElementContainers.IMContainer;
import jagerfield.mobilecontactslibrary.ElementContainers.NumberContainer;
import jagerfield.mobilecontactslibrary.ElementContainers.EventContainer;
import jagerfield.mobilecontactslibrary.ImportContactsAsync;
import com.basicphones.sync.ContactModel.Phone;
import com.basicphones.sync.ContactModel.Email;
import com.loopj.android.http.TextHttpResponseHandler;

public class login_existing extends AppCompatActivity {

    private EditText phone_edit;
    private EditText password_edit;
    private Button login_btn;
    private String device_phone_number;
    private String device_imei;
    private String device_model;
    private String device_name;
    private String sms_code;
    private TextView ok_btn;
    private TextView cancel_btn;
    private EditText sms_text;
    private ProgressDialog loginDialog;
    private ProgressDialog sms_dialog;
    private CountryCodePicker ccp;
    private String country_code = "";

    private static final int READ_CONTACT_REQUEST_CODE = 100;
    private static final int WRITE_CONTACT_REQUEST_CODE = 101;
    private static final int WRITE_CALENDAR_REQUEST_CODE = 102;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_existing);

        phone_edit = findViewById(R.id.editAccountNumber);
        password_edit = findViewById(R.id.editExistingPassword);
        login_btn = findViewById(R.id.buttonSubmitExisting);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    device_phone_number = tMgr.getLine1Number();
                    device_imei = tMgr.getDeviceId();
                    device_model = Build.MODEL;
                    device_name = DeviceName.getDeviceName();
                }
            }
        }
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    loginDialog = ProgressDialog.show(login_existing.this, "", "logging");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    }).start();
                    sendAccountInfoToServer();
                }

            }
        });
        ccp = findViewById(R.id.ccp);
        ccp.hideNameCode(true);
        ccp.setCountryPreference("US,CA");
        country_code = ccp.getDefaultCountryCodeWithPlus();
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                country_code = ccp.getSelectedCountryCodeWithPlus();
            }
        });
        checkPermission();
    }
    Handler login_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            loginDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Login Success!", Toast.LENGTH_SHORT).show();
        }
    };

    /** send account info to server for login */
    public void sendAccountInfoToServer() {
        RequestParams rp = new RequestParams();
        rp.add("number", country_code + phone_edit.getText().toString());
        rp.add("device_number", device_phone_number);
//        rp.add("device_number", "+8616696516925");
        rp.add("device_imei", device_imei);
//        rp.add("device_imei", "3578900456");
        rp.add("password", password_edit.getText().toString());
        String url = "/get-code";
        HttpUtils.post(url, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.d("api_result", "" + statusCode + " : " + response.toString());
                try {
                    String message = response.getString("message");
                    if(message.equals("success")){
                        if(loginDialog.isShowing()){
                            loginDialog.dismiss();
                        }
                        showSMSInputDialog();
                    }
                    if(message.equals("authentication")){
                        String token_info = response.getString("Token");
                        if(!token_info.isEmpty() && !token_info.equals("")) {
                            Token token = SyncLab.get(getApplicationContext()).getTokenInfo();
                            if (token.getAuthentication() == null || token.getAuthentication().isEmpty() || token.getAuthentication().equals("")) {
                                Token login_token = new Token(country_code + phone_edit.getText().toString(), token_info);
                                SyncLab.get(getApplicationContext()).addTokenInfo(login_token);
                                Log.d("Token_info:  ", token_info);
                            }
                            login_handler.sendEmptyMessage(0);
                            Intent intent = new Intent(login_existing.this, MainActivity.class);
                            startActivity(intent);
                        }
                        else{
                            if(loginDialog.isShowing()){
                                loginDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Token info does not exist", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                AsyncHttpClient.log.w(LOG_TAG, "onSuccess(int, Header[], String) was not overriden, but callback was received");
                Log.d("api_result", "" + statusCode + " : " + responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("api_result", "" + statusCode + " : " + errorResponse.toString());
                if(loginDialog.isShowing()){
                    loginDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("api_result", "" + statusCode + " : " + responseString);
                if(loginDialog.isShowing()){
                    loginDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
    public boolean validate(){
        String phone_number = phone_edit.getText().toString();
        if(phone_number.isEmpty() || phone_number.length() < 0){
            Toast.makeText(getApplicationContext(), "input phone number.", Toast.LENGTH_SHORT).show();
            return false;
        }
        String password = password_edit.getText().toString();
        if(password.isEmpty() || password.length() < 0){
            Toast.makeText(getApplicationContext(), "input password.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    /** if account use other device(phone) by same phone , proceed sms verification */
    public void showSMSInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alertdialog_custom_view, null);
        builder.setView(dialogView);

        ok_btn = (TextView) dialogView.findViewById(R.id.ok);
        cancel_btn = (TextView) dialogView.findViewById(R.id.cancel);
        sms_text = (EditText) dialogView.findViewById(R.id.sms_text);
        sms_text.requestFocus();

        final AlertDialog dialog = builder.create();
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!sms_text.getText().toString().isEmpty() && sms_text.getText().toString().length() > 0){
                    sms_dialog = ProgressDialog.show(login_existing.this, "", "logging");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    }).start();
                    sendSMSInfoToServer(sms_text.getText().toString());
                    dialog.cancel();
                }
                else {
                    String msg = "input sms code";
                    Toast.makeText(getApplicationContext(), msg, msg.length()).show();
                }

            }
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }
    Handler sms_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            sms_dialog.dismiss();
            Toast.makeText(getApplicationContext(), "Login Success!", Toast.LENGTH_SHORT).show();
        }
    };
    public void sendSMSInfoToServer(String sms_code){
        RequestParams rp = new RequestParams();
        rp.add("account_number", country_code + phone_edit.getText().toString());
        rp.add("device_number", device_phone_number);
//        rp.add("device_number", "+8616696516925");
        rp.add("code", sms_code);
        rp.add("device_imei", device_imei);
//        rp.add("device_imei", "3578900456");
        rp.add("device_model", device_model);
        rp.add("device_name", device_name);
        rp.add("password", password_edit.getText().toString());
        String url = "/device-auth";
        HttpUtils.post(url, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.d("apr_error: ", "statuscode=   " + statusCode + " : " + response.toString());
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    String token = serverResp.getString("token");
                    Token m_token = new Token(country_code + phone_edit.getText().toString(), token);
                    SyncLab.get(getApplicationContext()).addTokenInfo(m_token);
                    sms_handler.sendEmptyMessage(0);
                    Intent intent = new Intent(login_existing.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("apr_error: ", "statuscode=   " + statusCode + " : " + errorResponse.toString());
                if(sms_dialog.isShowing()){
                    sms_dialog.dismiss();
                    showSMSInputDialog();
                }
            }

        });
    }

    public void checkPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED){
//                addContactToPhone();
            }
            else{
                requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS}, WRITE_CONTACT_REQUEST_CODE);
            }
        }
        else{
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACT_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == READ_CONTACT_REQUEST_CODE){
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS}, WRITE_CONTACT_REQUEST_CODE);
        }

        if(requestCode == WRITE_CONTACT_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                addContactToPhone();
            }
        }

    }

    public void addContactToPhone(){
        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setId("12");
        contactInfo.setUid("");
        contactInfo.setNamePrefix("abc");
        contactInfo.setFirstName("abc");
        contactInfo.setMiddleName("abc");
        contactInfo.setLastName("abc");
        contactInfo.setNameSuffix("abc");
        contactInfo.setFullName("abc");
        contactInfo.addPhone("1233242", 1);
        contactInfo.addEmail("admin@gmail.com", 2);
        contactInfo.setmTrash("untrash");
        contactInfo.setmSync("unsync");
        contactInfo.setmRemove("unremove");
        contactInfo.setCompany(new Company("kumkang company", "kkg"));
        contactInfo.addAddress("asdfsdfsdf", 2);
        contactInfo.addIMAddress("axcvvxvc", 0);
        contactInfo.addEvent("2015-05-26", 3);
        ContactUtils.addContactInPhone(contactInfo, login_existing.this);

    }

}
