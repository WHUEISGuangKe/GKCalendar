package com.whu.gkcalendar.activity;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.whu.gkcalendar.R;

/**
 * Created by Administrator on 2016/5/11.
 */
public class LoginActivity extends Activity{

    private TextView tvRegistry;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        tvRegistry=(TextView)findViewById(R.id.tv_registry);
        btnLogin=(Button)findViewById(R.id.btn_login);
        tvRegistry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goRegistry = new Intent(LoginActivity.this, RegistryActivity.class);
                startActivity(goRegistry);
            }
        });
       // ColorStateList csl = new ColorStateList(new int[][]{new int[0]}, new int[]{0x4500BFFF});
       // btnLogin.setBackgroundTintList(csl);
    }
}