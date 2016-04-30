package com.whu.gkcalendar.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.whu.gkcalendar.R;

public class WelcomeActivity extends AppCompatActivity {
    private static final int TIME = 1500;
    private static final int GO_HOME = 1000;
    private static final int GO_GUIDE = 1001;
    private boolean isFirst = false;

    private Handler mhandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case GO_HOME:
                        goHome();
                        break;
                    case GO_GUIDE:
                        goGuide();
                        break;
                }
            }
        };

    private void init() {
        SharedPreferences preferences = getSharedPreferences("jike", MODE_PRIVATE);
        isFirst = preferences.getBoolean("isFirst", true);
        SharedPreferences.Editor ed = preferences.edit();
        ed.putBoolean("isFirst", false);
        ed.commit();
        if (isFirst)
            mhandler.sendEmptyMessageDelayed(GO_GUIDE, TIME);
        else {
            mhandler.sendEmptyMessageDelayed(GO_HOME, TIME);

        }
    }


    public void goHome() {
        Intent i1 = new Intent(WelcomeActivity.this, Calendar.class);
        startActivity(i1);
        finish();
    }

    public void goGuide() {
        Intent i2 = new Intent(WelcomeActivity.this, GuideActivity.class);
        startActivity(i2);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_welcome);
        init();
    }
}
