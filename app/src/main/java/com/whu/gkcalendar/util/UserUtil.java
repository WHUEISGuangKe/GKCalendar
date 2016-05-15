package com.whu.gkcalendar.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;


import com.whu.gkcalendar.bean.UserBean;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理用户信息
 * Created by wwhisdavid on 16/5/13.
 */
public class UserUtil {
    private static final String HOST = NetworkManager.HOST;
    private static final String LOGIN_URL = HOST + "/GKCalendarServer/user_login";
    private static final String REGISTER_URL = HOST + "/GKCalendarServer/user_register";
    private static final String LOGOUT_URL = HOST + "/GKCalendarServer/user_logout";
    private static final String IS_LOGIN_URL = HOST + "/GKCalendarServer/user_isLogin";

    public static boolean isLogin = false;
    public static String userName = "";
    public static String currentToken = "";

    public static boolean login(UserBean user, Context context) {
        if (user == null)
            return false;
        try {
            Map<String, Object> params = new HashMap();
            params.put("username", user.username);
            params.put("password", user.password);
            HttpURLConnection connection = NetworkManager.requestPost(params, LOGIN_URL);

            if (connection == null)
                return false;

            int code = connection.getResponseCode();
            Log.i("ret_code", code + "");
            if (code == 200) {
                //获取请求到的流信息
                InputStream inputStream = connection.getInputStream();
                String result = StreamUtil.streamToString(inputStream);

                JSONObject root = new JSONObject(result);
                int ret_code = root.getInt("ret_code");
                Log.i("ret_code", ret_code + "");
                if (ret_code == 1) {
                    String token = root.getString("token");
                    Log.i("token", token);
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("token", token);
                    editor.putString("username", user.username);
                    userName = user.username;
                    currentToken = token;
                    isLogin = true;
                    editor.commit();
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

    public static boolean register(UserBean user, Context context) {
        if (user == null)
            return false;

        Map<String, Object> params = new HashMap();
        params.put("username", user.username);
        params.put("password", user.password);
        HttpURLConnection connection = NetworkManager.requestPost(params, REGISTER_URL);

        if (connection == null)
            return false;

        int code = 0;
        try {
            code = connection.getResponseCode();

            Log.i("ret_code", code + "");

            if (code == 200) {
                //获取请求到的流信息
                InputStream inputStream = connection.getInputStream();
                String result = StreamUtil.streamToString(inputStream);

                JSONObject root = new JSONObject(result);
                int ret_code = root.getInt("ret_code");
                Log.i("ret_code", ret_code + "");
                if (ret_code == 1) {
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

    public static boolean logout(String username, String token){
        Map<String, Object> params = new HashMap<>();

        params.put("username", username);
        params.put("token", token);
        HttpURLConnection connection = NetworkManager.requestPost(params, LOGOUT_URL);

        if (connection == null)
            return false;

        int code = 0;
        try {
            code = connection.getResponseCode();

            Log.i("ret_code", code + "");

            if (code == 200) {
                //获取请求到的流信息
                InputStream inputStream = connection.getInputStream();
                String result = StreamUtil.streamToString(inputStream);

                JSONObject root = new JSONObject(result);
                int ret_code = root.getInt("ret_code");
                Log.i("ret_code", ret_code + "");
                if (ret_code == 1) {
                    isLogin = false;
                    userName = "";
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

    public static boolean isLogining(String username, String token){
        Map<String, Object> params = new HashMap<>();

        params.put("username", username);
        params.put("token", token);
        HttpURLConnection connection = NetworkManager.requestPost(params, IS_LOGIN_URL);

        if (connection == null)
            return false;

        int code = 0;
        try {
            code = connection.getResponseCode();

            Log.i("ret_code", code + "");

            if (code == 200) {
                //获取请求到的流信息
                InputStream inputStream = connection.getInputStream();
                String result = StreamUtil.streamToString(inputStream);

                JSONObject root = new JSONObject(result);
                int ret_code = root.getInt("ret_code");
                Log.i("ret_code", ret_code + "");
                if (ret_code == 1) {
                    isLogin = true;
                    userName = username;
                    UserUtil.currentToken = token;
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
