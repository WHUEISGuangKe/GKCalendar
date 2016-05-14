package com.whu.gkcalendar.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.whu.gkcalendar.R;
import com.whu.gkcalendar.util.UserUtil;

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

        Context context = getApplicationContext();
        SharedPreferences defaultShared = PreferenceManager.getDefaultSharedPreferences(context);
        final String username = defaultShared.getString("username", "");
        final String token = defaultShared.getString("token", "");
        Log.i("preference", username + "-" + token);
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = UserUtil.isLogining(username, token);
                if (success) {
                    UserUtil.isLogin = true;
                    UserUtil.currentToken = token;
                    Log.i("keep login", token);
                } else {
                    UserUtil.isLogin = false;
                    UserUtil.currentToken = "";
                }
            }
        }).start();
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
