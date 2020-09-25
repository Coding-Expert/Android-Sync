package com.basicphones.sync;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Trash extends AppCompatActivity {

    private TextView backView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);

//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        backView = findViewById(R.id.textView12);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goPreviousScreen();
            }
        });

        TrashFragment trashFragment = new TrashFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, trashFragment).commit();
    }

    public void goPreviousScreen() {
        super.onBackPressed();
    }

}
