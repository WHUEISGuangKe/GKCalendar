package com.whu.gkcalendar.bean;

/**
 * Created by wwhisdavid on 16/4/12.
 */
public class CalendarInfo {
    public int year;
    public String date; // 月-日
    public String week_day; // 周几
    public String time; // 时-分
    public String calendar;
    public int isImportent; // 1:重要 0：不重要
    public int unix_time;
    public int _id;

    @Override
    public String toString() {
        return year + "#" + date + "#" + time + "#" + week_day + "#" + calendar + unix_time + "#" + _id;
    }
}
