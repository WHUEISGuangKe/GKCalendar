package com.whu.gkcalendar.util;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.WindowManager;

import com.whu.gkcalendar.R;
import com.whu.gkcalendar.activity.AlarmActiviy;
import com.whu.gkcalendar.activity.Calendar;
import com.whu.gkcalendar.bean.CalendarInfo;

import java.io.IOException;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    private NotificationManager manager;

    @Override
    public void onReceive(final Context context, Intent intent) {
        manager = (NotificationManager)context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        //例如这个id就是你传过来的
        final MediaPlayer mp = new MediaPlayer();
        Uri ringTune=null;
        if(intent.getStringExtra("ring").equals(""))
            ringTune=null;
        else {
            ringTune = Uri.parse(intent.getStringExtra("ring"));
            //final int d = 0;
        }
      //  Log.d("AlarmReciver..........",ringTune.toString());

            if(ringTune!=null) {
            try {
                mp.setDataSource(context, ringTune);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                mp.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mp.start();
        }
        final String content = intent.getStringExtra("content");
        final String _id = intent.getStringExtra("_id");
//        System.out.println("收到通知:" + content);
        //Activity是你点击通知时想要跳转的Activity

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(content)
                .setCancelable(false)
                .setPositiveButton("延后一天", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mp.stop();
                        dialog.cancel();
//                        Intent i = new Intent(context, Calendar.class);
//                        i.putExtra("token", "whu");
//                        i.putExtra("_id", _id);
//                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startActivity(i);
                    }
                })
                .setNegativeButton("知道了", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mp.stop();
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();
        CalendarInfo.ring=null;
    }
}
