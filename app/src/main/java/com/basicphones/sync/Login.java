package com.basicphones.sync;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    private Button login_btn;
    private Button signup_btn;

    private static final int READ_SMS_REQUEST_CODE = 100;
    private static final int READ_PHONE_NUMBERS_REQUEST_CODE = 101;
    private static final int READ_PHONE_STATE_REQUEST_CODE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_btn = findViewById(R.id.login_button);
        signup_btn = findViewById(R.id.signup_button);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login_intent = new Intent(Login.this, login_existing.class);
                startActivity(login_intent);
            }
        });

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup_intent = new Intent(Login.this, login_new.class);
                startActivityForResult(signup_intent, 1);
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    Token token_info = SyncLab.get(getApplicationContext()).getTokenInfo();
                    if(token_info.getAuthentication() != null && !token_info.getAuthentication().isEmpty() && !token_info.getAuthentication().equals("")){
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "please login again", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        else {
            requestPermissions(new String[]{Manifest.permission.READ_SMS}, READ_SMS_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_SMS_REQUEST_CODE) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_NUMBERS}, READ_PHONE_NUMBERS_REQUEST_CODE);
        }
        if (requestCode == READ_PHONE_NUMBERS_REQUEST_CODE) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE_REQUEST_CODE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Toast.makeText(getApplicationContext(), "Register Success!", Toast.LENGTH_SHORT).show();
        }
        if(resultCode == Activity.RESULT_CANCELED){
            Toast.makeText(getApplicationContext(), "cancel", Toast.LENGTH_SHORT).show();
        }
    }
}
