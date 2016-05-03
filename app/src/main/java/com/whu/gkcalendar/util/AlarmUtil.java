package com.whu.gkcalendar.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.whu.gkcalendar.bean.CalendarInfo;

import java.util.Calendar;

/**
 * Created by wwhisdavid on 16/4/13.
 */
public class AlarmUtil { // 闹钟管理工具类
    public static void registerAlarm(Context context, CalendarInfo info) {
        Intent intent = new Intent("android.alarm.action");
        intent.putExtra("content", info.calendar);
        intent.putExtra("_id", info._id + "");
        if(info.ring1!=null)
            intent.putExtra("ring",info.ring1.toString());
        else
            intent.putExtra("ring","");
//        Intent intent = new Intent(context, com.whu.gkcalendar.activity.Calendar.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, info._id, intent, 0);

        Calendar calendar = Calendar.getInstance();
        long timestamp = ((long)(info.unix_time)) * 1000;
        System.out.println("timestamp:"+timestamp + "id:" + info._id);
        calendar.setTimeInMillis(timestamp);

        AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);
        System.out.println(calendar.getTimeInMillis());
        Toast.makeText(context, "闹钟设置成功！:", Toast.LENGTH_SHORT).show();
    }

    public static void cancelAlarm(Context context, int _id){
        AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent("android.alarm.action");
//        intent.setClass(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, _id, intent, 0);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(context, "闹钟取消成功！", Toast.LENGTH_SHORT).show();
    }
}
