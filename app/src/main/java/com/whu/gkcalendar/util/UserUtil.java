package com.whu.gkcalendar.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;


import com.whu.gkcalendar.bean.UserBean;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * 管理用户信息
 * Created by wwhisdavid on 16/5/13.
 */
public class UserUtil {
    private static final String LOGIN_URL = "http://10.133.182.57:8080/GKCalendarServer/user_login";
    //http://192.168.13.83:8080/itheima74/servlet/GetNewsServlet
    private static final String REGISTER_URL = "http://localhost:8080/GKCalendarServer/user_register";
    private static final String LOGOUT_URL = "http://localhost:8080/GKCalendarServer/user_logout";
    private static final String USER_FILE = "user_info";
    public static boolean isLogin = false;

    public static boolean login(UserBean user, Context context) {
        if (user == null)
            return false;
        try {
            URL url = new URL(LOGIN_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setConnectTimeout(10 * 1000);
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setDoOutput(true);// 是否输入参数
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setUseCaches(false);
            // http://localhost:8080/GKCalendarServer/user_login?username=root&password=root


            String content = "username=" + URLEncoder.encode(user.username, "UTF-8");
            content +="&password="+URLEncoder.encode(user.password, "UTF-8");;

            DataOutputStream out = new DataOutputStream(connection
                    .getOutputStream());
            out.writeBytes(content);

            out.flush();
            out.close();

            Log.i("params", content.toString());

            int code = connection.getResponseCode();
            Log.i("ret_code", code + "");
            if (code == 200) {
                //获取请求到的流信息
                InputStream inputStream = connection.getInputStream();
                String result = StreamUtil.streamToString(inputStream);

                JSONObject root = new JSONObject(result);
                int message = root.getInt("message");
                Log.i("message", message + "");
                if (message == 1) {
                    String token = root.getString("token");
                    Log.i("token", token);
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("token", token);
                    editor.commit();
                    isLogin = true;
                    return true;

                } else
                    return false;

            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }

}
