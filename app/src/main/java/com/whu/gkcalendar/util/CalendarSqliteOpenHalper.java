package com.whu.gkcalendar.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by wwhisdavid on 16/4/12.
 */
public class CalendarSqliteOpenHalper extends SQLiteOpenHelper{
    private final static String DBName = "calendar.db";
    private final static int VERSION = 1;


    public CalendarSqliteOpenHalper(Context context) {
        super(context, DBName, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("创建数据库~~~~~~~~~");
        db.execSQL("CREATE TABLE calendar_info(_id integer primary key,year integer,date varchar(20),weekday varchar(20),time varchar(20),isImportent integer,calendar varchar(200),unix_time integer)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
