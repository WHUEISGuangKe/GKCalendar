package com.whu.gkcalendar.bean;

import android.media.RingtoneManager;
import android.net.Uri;

import java.net.URI;

/**
 * Created by wwhisdavid on 16/4/12.
 */
public class CalendarInfo {
    public int year;
    public String date; // 月-日
    public String week_day; // 周几
    public String time; // 时-分
    public String calendar;
    //修改重要性
    public int isImportent; // 0：琐事 1：紧急不重要 2：重要不紧急 3：紧急重要
    public int unix_time;
    public int _id;
    public static Uri ring= null;//铃声中间变量
    public Uri ring1=null;
    public String ring2;


    @Override
    public String toString() {
        return year + "#" + date + "#" + time + "#" + week_day + "#" + calendar + unix_time + "#" + _id;
    }
}
