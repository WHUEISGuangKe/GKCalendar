package com.whu.gkcalendar.activity;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.whu.gkcalendar.R;
import com.whu.gkcalendar.bean.UserBean;
import com.whu.gkcalendar.util.UserUtil;

/**
 * Created by Administrator on 2016/5/11.
 */
public class RegistryActivity extends Activity implements View.OnClickListener {

    private Context context = this;

    private Button btnRegister;
    private Button btnBack;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etPassword2;

    private Handler handle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            boolean success = (boolean) msg.obj;
            if (success) {
                btnBack.callOnClick();
                Toast.makeText(context, "注册成功!", Toast.LENGTH_SHORT).show();
            }else
                Toast.makeText(context, "注册失败，重试!", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registry_page);

        btnRegister = (Button)findViewById(R.id.btn_register);
        btnBack = (Button)findViewById(R.id.btn_back);
        etUsername = (EditText)findViewById(R.id.et_register_username);
        etPassword = (EditText)findViewById(R.id.et_register_password);
        etPassword2 = (EditText)findViewById(R.id.et_register_password2);

        etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etPassword2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        btnRegister.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_register:
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String password2 = etPassword2.getText().toString();
                if (username.length() == 0 || password.length() == 0 || password2.length() == 0)
                    Toast.makeText(context, "输入不能为空!", Toast.LENGTH_SHORT).show();
                else if (!password.equals(password2)){
                    Toast.makeText(context, "两次密码需一致!", Toast.LENGTH_SHORT).show();
                }else{
                    final UserBean user = new UserBean();
                    user.username = username;
                    user.password = password;

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            boolean success = UserUtil.register(user, context);
                            Message msg = Message.obtain();
                            msg.obj = success;
                            handle.sendMessage(msg);
                        }
                    }).start();

                }

                break;

            case R.id.btn_back:
                finish();
                break;
        }
    }
}
