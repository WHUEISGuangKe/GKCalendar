package com.whu.gkcalendar.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by wwhisdavid on 16/4/12.
 */
public class TimeUtil {
    public static Calendar now = Calendar.getInstance();

    public static String getTodayString(){
        return getYear()+"-"+getMonth()+"-"+getDay_month();
    }

    public static int getYear(){
        return now.get(Calendar.YEAR);
    }

    public static int getMonth(){
        return (now.get(Calendar.MONTH) + 1);
    }

    public static int getDay_month(){
        return now.get(Calendar.DAY_OF_MONTH);
    }

    public static int getDay_week(){
        return now.get(Calendar.DAY_OF_WEEK);
    }

    public static int getHour(){
        return now.get(Calendar.HOUR_OF_DAY);
    }

    public static int getMinute(){
        return now.get(Calendar.MINUTE);
    }

    public static int getSecond(){
        return now.get(Calendar.SECOND);
    }

    public static int getDay_year(){
        return now.get(Calendar.DAY_OF_YEAR);
    }

    public static int daysBetweenNow(Date date){
        int day1 = getDay_year();
        Calendar aCalendar = Calendar.getInstance();
        aCalendar.setTime(date);
        int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
        return day2 - day1;
    }

    public static String getDateStringFromUnix(int timestamp){
        Date date = new Date(((long)timestamp)* 1000);
        SimpleDateFormat format = new SimpleDateFormat("yyyy:MM-dd:E");
        String str = format.format(date);
        System.out.println(str);
        return str;
    }

    public static int getUnixTimestamp(){

        return 0;
    }
}

