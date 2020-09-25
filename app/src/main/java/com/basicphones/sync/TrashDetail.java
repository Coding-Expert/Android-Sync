package com.basicphones.sync;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class TrashDetail extends AppCompatActivity {

    private Toolbar mTopToolbar;
    public TextView title_view;
    private ImageView back;

    public static final String BACKUP_STATE = "backupState";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash_detail);

        mTopToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(backlistener);

        ContactInfo contactInfo = (ContactInfo)getIntent().getSerializableExtra("trash_contact");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, DetailFragment.newInstance(contactInfo)).commit();
    }

    private View.OnClickListener backlistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            goPreviousScreen();
        }
    };

    public void goPreviousScreen() {
        super.onBackPressed();
    }

    public void goTrashListScreen(){
        Intent intent = new Intent();
        intent.putExtra(BACKUP_STATE, "1");
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
    public void goTrashListScreen1(){
        Intent intent = new Intent();
        intent.putExtra(BACKUP_STATE, "0");
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
