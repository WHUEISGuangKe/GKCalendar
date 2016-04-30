package com.whu.gkcalendar.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.whu.gkcalendar.R;

public class AlarmActiviy extends AppCompatActivity {
    private Context mContext = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("AlarmActiviy:收到");
        setContentView(R.layout.activity_alarm_activiy);
        final MediaPlayer mediaPlayer = MediaPlayer.create(mContext, 1);
        //mediaPlayer.setLooping(true);
        mediaPlayer.start();

        //创建一个闹钟提醒的对话框,点击确定关闭铃声与页面
        new AlertDialog.Builder(AlarmActiviy.this).setTitle("闹钟").setMessage("您有日程!")
                                    .setPositiveButton("关闭闹铃", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            mediaPlayer.stop();
                                            AlarmActiviy.this.finish();
                                        }
                                    }).show();
    }

}
