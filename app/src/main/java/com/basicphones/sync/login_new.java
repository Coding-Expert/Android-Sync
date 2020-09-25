package com.basicphones.sync;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jaredrummler.android.device.DeviceName;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class login_new extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int READ_SMS_REQUEST_CODE = 100;
    private static final int READ_PHONE_NUMBERS_REQUEST_CODE = 101;
    private static final int READ_PHONE_STATE_REQUEST_CODE = 102;

    private String device_phone_number = "";
    private String device_imei = "";
    private String device_model = "";
    private String device_name = "";
    private Button resend_btn;
    private String sms_code = "";
    private EditText firstName_edit;
    private EditText lastName_edit;
    private EditText password_edit;
    private EditText sms_edit;
    private Button submit_btn;
    private Button cancel_btn;
    public String response_message = "";
    private ProgressDialog sms_dialog;
    private ProgressDialog registDialog;
    private GoogleApiClient  mGoogleApiClient;
    private static final int RC_HINT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);

        resend_btn = findViewById(R.id.resend_button);
        resend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sendDeviceInfoToServer();
            }
        });

        firstName_edit = findViewById(R.id.editNewFirstName);
        lastName_edit = findViewById(R.id.editNewLastName);
        password_edit = findViewById(R.id.editNewPassword);
        sms_edit = findViewById(R.id.sms_edit);
        submit_btn = findViewById(R.id.buttonSubmitNew);
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    registDialog = ProgressDialog.show(login_new.this, "", "registering account");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    }).start();
                    sendAccountInfoToServer();
                }
            }
        });
        sms_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String sms_string = "" + s;
                if (!sms_string.isEmpty() || sms_string.length() > 0) {
                    submit_btn.setEnabled(true);
                } else {
                    submit_btn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        cancel_btn = findViewById(R.id.cancel_button);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login_new.this, Login.class);
                setResult(Activity.RESULT_CANCELED, intent);
                finish();
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    device_phone_number = tMgr.getLine1Number();
                    device_imei = tMgr.getDeviceId();
                    device_model = Build.MODEL;
                    device_name = DeviceName.getDeviceName();

//                    DeviceName.with(getApplicationContext()).request(new DeviceName.Callback(){
//                        @Override public void onFinished(DeviceName.DeviceInfo info, Exception error) {
//                            String manufacturer = info.manufacturer;  // "Samsung"
//                            String name = info.marketName;            // "Galaxy S7 Edge"
//                            String model = info.model;                // "SAMSUNG-SM-G935A"
//                            String codename = info.codename;          // "hero2lte"
//                            String deviceName = info.getName();       // "Galaxy S7 Edge"
//                            // FYI: We are on the UI thread.
//                            Toast.makeText(getApplicationContext(), deviceName, deviceName.length()).show();
//                        }
//                    });

//                    mGoogleApiClient = new GoogleApiClient.Builder(this)
//                            .addConnectionCallbacks(this)
//                            .enableAutoManage(this, this)
//                            .addApi(Auth.CREDENTIALS_API)
//                            .build();
                    sms_dialog = ProgressDialog.show(login_new.this, "", "device authentication");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    }).start();
                    sendDeviceInfoToServer();
                }
            }
        }

    }

    private void showHint() {
        HintRequest hintRequest = new HintRequest.Builder()
                .setHintPickerConfig(new CredentialPickerConfig.Builder()
                        .setShowCancelButton(true)
                        .build())
                .setPhoneNumberIdentifierSupported(true)
                .build();

        PendingIntent intent =
                Auth.CredentialsApi.getHintPickerIntent(mGoogleApiClient, hintRequest);
        try {
            startIntentSenderForResult(intent.getIntentSender(), RC_HINT, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_HINT) {
            if (resultCode == RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                Toast.makeText(getApplicationContext(), credential.getId(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    Handler sms_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            sms_dialog.dismiss();
            Toast.makeText(getApplicationContext(), "SMS message reached on your phone.", Toast.LENGTH_SHORT).show();
        }
    };
    Handler account_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            registDialog.dismiss();
        }
    };

    /** send device info to server */
    public void sendDeviceInfoToServer() {

        RequestParams rp = new RequestParams();
        rp.add("number", device_phone_number);
        rp.add("device_number", device_phone_number);
//        rp.add("number", "+8616696516925");
//        rp.add("device_number", "+8616696516925");

        rp.add("device_imei", device_imei);
//        rp.add("account_number", "");
//        rp.add("device_model", device_model);
//        rp.add("device_name", device_name);
        String url = "/get-code";
        HttpUtils.post(url, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.d("asd", "---------------- this is response : " + response);
                try {
                    response_message = response.getString("message");
                    if(response_message.equals("success")){
                        sms_handler.sendEmptyMessage(0);
                    }
                    Log.d("apr_error: ", "statuscode=   " + statusCode + " : " + response.toString());
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                Log.d("apr_error: ", "statuscode=   " + statusCode);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                AsyncHttpClient.log.w(LOG_TAG, "onSuccess(int, Header[], String) was not overriden, but callback was received");
//                Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_SHORT).show();
                Log.d("apr_error: ", "statuscode=   " + statusCode + " : " + responseString);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("apr_error: ", "statuscode=   " + statusCode + " : " + errorResponse.toString());
                if(sms_dialog.isShowing()){
                    sms_dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "authentication failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("apr_error: ", "statuscode=   " + statusCode + " : " + errorResponse.toString());
                if(sms_dialog.isShowing()){
                    sms_dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "authentication failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("apr_error: ", "statuscode=   " + statusCode + " : " + responseString);
                if(sms_dialog.isShowing()){
                    sms_dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "authentication failed", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    public boolean validate(){
        boolean check_flag = false;
        String firstname = firstName_edit.getText().toString();
        if(firstname.isEmpty() || firstname.length() < 0){
            Toast.makeText(getApplicationContext(), "input firstname", Toast.LENGTH_SHORT).show();
            check_flag = false;
            return check_flag;
        }
        else{

            if(firstname.matches(".*[A-Z].*") || firstname.matches(".*[a-z].*")){
                check_flag = true;
            }
            else{
                Toast.makeText(getApplicationContext(), "input firstname as characters", Toast.LENGTH_SHORT).show();
                check_flag = false;
                return check_flag;
            }
        }
        String lastname = lastName_edit.getText().toString();
        if(lastname.isEmpty() || lastname.length() < 0){
            Toast.makeText(getApplicationContext(), "input lastname", Toast.LENGTH_SHORT).show();
            check_flag = false;
            return check_flag;
        }
        else{
            if(lastname.matches(".*[A-Z].*") || lastname.matches(".*[a-z].*")){
                check_flag = true;
            }
            else{
                Toast.makeText(getApplicationContext(), "input lastname as characters", Toast.LENGTH_SHORT).show();
                check_flag = false;
                return check_flag;
            }
        }
        String password = password_edit.getText().toString();
        if(password.isEmpty() || password.length() < 0){
            Toast.makeText(getApplicationContext(), "input password", Toast.LENGTH_SHORT).show();
            check_flag = false;
            return check_flag;
        }
        String sms_code = sms_edit.getText().toString();
        if(sms_code.isEmpty() || sms_code.length() < 0){
            Toast.makeText(getApplicationContext(), "input sms code", Toast.LENGTH_SHORT).show();
            check_flag = false;
            return check_flag;
        }
        else{
            String regexStr = "^[0-9]*$";
            if(sms_code.trim().matches(regexStr)){
                check_flag = true;
            }
        }
        return check_flag;
    }

    /** send Account information to Server*/
    public void sendAccountInfoToServer(){
        RequestParams rp = new RequestParams();
        rp.add("account_number", device_phone_number);
        rp.add("device_number", device_phone_number);
//        rp.add("account_number", "+8616696516925");
//        rp.add("device_number", "+8616696516925");
        rp.add("first_name", firstName_edit.getText().toString());
        rp.add("last_name", lastName_edit.getText().toString());
        rp.add("password", password_edit.getText().toString());
        rp.add("code", sms_edit.getText().toString());
        rp.add("device_imei", device_imei);
        rp.add("device_model", device_model);
        rp.add("device_name", device_name);

        String url = "/device-auth";
        HttpUtils.post(url, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.d("apr_error: ", "statuscode=   " + statusCode + " : " + response.toString());
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    String token = serverResp.getString("token");
                    Token m_token = new Token(device_phone_number, token);
                    SyncLab.get(getApplicationContext()).addTokenInfo(m_token);
                    account_handler.sendEmptyMessage(0);
                    Intent intent = new Intent(login_new.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                Log.d("apr_error: ", "statuscode=   " + statusCode);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                AsyncHttpClient.log.w(LOG_TAG, "onSuccess(int, Header[], String) was not overriden, but callback was received");
                Log.d("apr_error: ", "statuscode=   " + statusCode + " : " + responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("apr_error: ", "statuscode=   " + statusCode + " : " + errorResponse.toString());
                if(registDialog.isShowing()){
                    registDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "authentication failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("apr_error: ", "statuscode=   " + statusCode + " : " + errorResponse.toString());
                if(registDialog.isShowing()){
                    registDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "authentication failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("apr_error: ", "statuscode=   " + statusCode + " : " + responseString);
                if(registDialog.isShowing()){
                    registDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "authentication failed", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
//        showHint();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
