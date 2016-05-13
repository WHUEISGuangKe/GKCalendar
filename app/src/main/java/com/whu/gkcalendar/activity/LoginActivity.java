package com.whu.gkcalendar.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.whu.gkcalendar.R;
import com.whu.gkcalendar.bean.UserBean;
import com.whu.gkcalendar.util.UserUtil;

/**
 * Created by Administrator on 2016/5/11.
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    private TextView tvRegistry;
    private Button btnLogin;
    private EditText usernameEt;
    private EditText passwordEt;

    private Context context = this;

    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            boolean flag = (boolean)msg.obj;
            if(flag) {
                Toast.makeText(context, "登陆成功", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(context,"登陆失败", Toast.LENGTH_SHORT).show();
        };
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        tvRegistry = (TextView) findViewById(R.id.tv_registry);
        btnLogin = (Button) findViewById(R.id.btn_login);
        usernameEt = (EditText) findViewById(R.id.et_username);
        passwordEt = (EditText) findViewById(R.id.et_password);

        btnLogin.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login: // 登陆操作：
                final UserBean user = new UserBean();
                String name = usernameEt.getText().toString();
                String password = passwordEt.getText().toString();
                Log.i("name,pass",name+password);
                if (name.length() == 0)
                    Toast.makeText(context,"账号不能为空", Toast.LENGTH_SHORT).show();
                else if(password.length() == 0)
                    Toast.makeText(context,"密码不能为空", Toast.LENGTH_SHORT).show();
                else {
                    user.username = name;
                    user.password = password;
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            //请求网络数据
                            UserUtil.isLogin = UserUtil.login(user, context);
                            //通过handler将msg发送到主线程去更新Ui
                            Message msg = Message.obtain();
                            msg.obj = UserUtil.isLogin;
                            handler.sendMessage(msg);
                        }
                    }).start();
                }
                break;
        }
    }
}