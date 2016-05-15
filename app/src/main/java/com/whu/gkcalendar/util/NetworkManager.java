package com.whu.gkcalendar.util;

import android.util.Log;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

/**
 * Created by wwhisdavid on 16/5/14.
 */
public class NetworkManager {
    public static final String HOST = "http://192.168.16.222:8080";
    public static HttpURLConnection requestPost(Map<String, Object> params, String urlString) {

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setConnectTimeout(10 * 1000);
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setDoOutput(true);// 是否输入参数
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setUseCaches(false);
            // http://localhost:8080/GKCalendarServer/user_login?username=root&password=root

            String content = "";
            boolean flag = false;

            Set<Map.Entry<String, Object>> entries = params.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                String paramName = entry.getKey();
                String paramValue = (String) (entry.getValue() + "");
                if (!flag) {
                    content += paramName + "=" + URLEncoder.encode(paramValue, "UTF-8");
                    flag = true;
                } else
                    content += "&" + paramName + "=" + URLEncoder.encode(paramValue, "UTF-8");
            }

            Log.i("params", content.toString());

            DataOutputStream out = new DataOutputStream(connection
                    .getOutputStream());
            out.writeBytes(content);

            out.flush();
            out.close();
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
