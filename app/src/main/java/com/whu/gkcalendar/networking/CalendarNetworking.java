package com.whu.gkcalendar.networking;

import android.util.Log;

import com.whu.gkcalendar.bean.ShareCalendarInfo;
import com.whu.gkcalendar.util.CaledarUpdateManager;
import com.whu.gkcalendar.util.NetworkManager;
import com.whu.gkcalendar.util.StreamUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wwhisdavid on 16/5/14.
 */
public class CalendarNetworking {
    private static final String HOST = NetworkManager.HOST;
    private static final String FETCH_LIST_URL = HOST + "/GKCalendarServer/calendar_query";
    private static final String ALTER_URL = HOST + "/GKCalendarServer/calendar_alter";
    private static final String ADD_URL = HOST + "/GKCalendarServer/calendar_addMember";
    private static final String CREATE_URL = HOST + "/GKCalendarServer/calendar_create";

    public static List<ShareCalendarInfo> fetchList(String username, String token){
        Map<String, Object> params = new HashMap();
        params.put("username", username);
        params.put("token", token);
        HttpURLConnection connection = NetworkManager.requestPost(params, FETCH_LIST_URL);

        if (connection == null)
            return null;

        int code = 0;

        try {
            code = connection.getResponseCode();

            Log.i("code", code + "");

            if (code == 200) {
                //获取请求到的流信息
                InputStream inputStream = connection.getInputStream();
                String result = StreamUtil.streamToString(inputStream);

                JSONObject root = new JSONObject(result);
                int ret_code = root.getInt("ret_code");
                Log.i("ret_code", ret_code + "");
                if (ret_code == 1) {
                    List<ShareCalendarInfo> list = new ArrayList<>();

                    JSONArray allCalendar = root.getJSONArray("message");
                    for (int i = 0; i < allCalendar.length() ; i++) {
                        JSONObject calendar = allCalendar.getJSONObject(i);
                        ShareCalendarInfo info = new ShareCalendarInfo();

                        info.version = calendar.getInt("version");
                        info.id = calendar.getString("calendar_id");
                        info.title = calendar.getString("calendar_title");
                        info.unixstamp = calendar.getInt("unixstamp");
                        info.content = calendar.getString("content");
                        info.creator = calendar.getString("creator");

                        list.add(info);
                    }

                    return list;
                } else
                    return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    //http://localhost:8080/GKCalendarServer/calendar_alter?
    // version=8&calendar_id=1&username=root&content=123&date=99&token=3506402
    public static boolean alterCalendar(int version, String id, String username, String content, int unixstamp, String token){
        Map<String, Object> params = new HashMap();
        params.put("username", username);
        params.put("version", version + "");
        params.put("calendar_id", id);
        params.put("token", token);
        params.put("content", content);
        params.put("date", unixstamp);

        HttpURLConnection connection = NetworkManager.requestPost(params, ALTER_URL);

        if (connection == null)
            return false;

        int code = 0;

        try {
            code = connection.getResponseCode();

            Log.i("code", code + "");

            if (code == 200) {
                //获取请求到的流信息
                InputStream inputStream = connection.getInputStream();
                String result = StreamUtil.streamToString(inputStream);

                JSONObject root = new JSONObject(result);
                int ret_code = root.getInt("ret_code");
                Log.i("ret_code", ret_code + "");
                /*
                * {
		            "calendar_id":1,
		            "message":"success", // 成功更新
		            "ret_code":1,
		            "version":9
		            }
                * */
                if (ret_code == 1) {
                    ShareCalendarInfo info = new ShareCalendarInfo();
                    info.id = root.getString("calendar_id");
                    info.version = root.getInt("version");
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
    // http://localhost:8080/GKCalendarServer/calendar_addMember?
    // username=root&newMemberName=root1&token=3506402&calendar_id=11
    public static boolean addMember(String username, String newMemberName, String token, String id){
        Map<String, Object> params = new HashMap();
        params.put("username", username);
        params.put("newMemberName", newMemberName);
        params.put("calendar_id", id);
        params.put("token", token);

        HttpURLConnection connection = NetworkManager.requestPost(params, ADD_URL);

        if (connection == null)
            return false;

        int code = 0;
        try {
            code = connection.getResponseCode();

            Log.i("code", code + "");

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

    // http://localhost:8080/GKCalendarServer/calendar_create?
    // username=root&calendar_name=helloworld&date=198198198&content=hellowworld!&token=3506402
    public static boolean createCalendar(String username, String title, int unixstamp, String content, String token){
        Map<String, Object> params = new HashMap();

        params.put("username", username);
        params.put("date", unixstamp);
        params.put("calendar_name", title);
        params.put("token", token);
        params.put("content", content);

        HttpURLConnection connection = NetworkManager.requestPost(params, CREATE_URL);

        if (connection == null)
            return false;

        int code = 0;
        try {
            code = connection.getResponseCode();

            Log.i("code", code + "");

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
}
