package com.whu.gkcalendar.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.whu.gkcalendar.bean.CalendarInfo;
import com.whu.gkcalendar.util.CalendarSqliteOpenHalper;

import java.util.ArrayList;

/**
 * Created by wwhisdavid on 16/4/12.
 */
public class CalendarInfoDao {
    private CalendarSqliteOpenHalper calendarSqliteOpenHalper;

    public CalendarInfoDao(Context context) {
        calendarSqliteOpenHalper = new CalendarSqliteOpenHalper(context);
    }

    public boolean add(CalendarInfo info) {
//        System.out.println("添加数据~~~~~");
        SQLiteDatabase db = calendarSqliteOpenHalper.getReadableDatabase();

        ContentValues value = new ContentValues();
        value.put("_id", info._id);
        value.put("year", info.year);
        value.put("date", info.date);
        value.put("weekday", info.week_day);
        value.put("time", info.time);
        value.put("isImportent", info.isImportent);
        value.put("calendar", info.calendar);
        value.put("unix_time", info.unix_time);
        //value.put("ring",info.ring1.toString());

        long result = db.insert("calendar_info", null, value);

        db.close();

        if (result == -1)
            return false;
        else
            return true;
    }

    public boolean delete(CalendarInfo info) {
        SQLiteDatabase db = calendarSqliteOpenHalper.getReadableDatabase();
        int result = db.delete("calendar_info", "_id = ?", new String[]{info._id + ""});
        db.close();
        if (result == -1)
            return false;
        else
            return true;
    }

    public boolean update(String columnName, CalendarInfo info) {
        SQLiteDatabase db = calendarSqliteOpenHalper.getReadableDatabase();
        ContentValues values = new ContentValues();

        if (columnName.equals("isImportent")) {
            values.put(columnName, info.isImportent);
        }

        if (columnName.equals("year")){
            values.put(columnName, info.year);
        }

        if (columnName.equals("date")){
            values.put(columnName, info.date);
        }

        if (columnName.equals("unix_time")){
            values.put(columnName, info.unix_time);
        }

        if (columnName.equals("weekday")){
            values.put(columnName, info.week_day);
        }

        int result = db.update("calendar_info", values, "_id = ?", new String[]{info._id + ""});
        db.close();
        if (result == -1)
            return false;
        else
            return true;
    }

    public CalendarInfo queryWithID(String _id){
        SQLiteDatabase db = calendarSqliteOpenHalper.getReadableDatabase();
        String[] fields = {"_id", "year", "date", "weekday", "time", "isImportent", "calendar", "unix_time"};
        Cursor cursor = db.query("calendar_info", fields, "_id = ?", new String[]{_id}, null, null, "unix_time asc");

        CalendarInfo bean = null;
        if (cursor != null && cursor.getCount() > 0) {//判断cursor中是否存在数据
            bean = new CalendarInfo();
            //循环遍历结果集，获取每一行的内容
            while (cursor.moveToNext()) {//条件，游标能否定位到下一行

                //获取数据
                bean._id = cursor.getInt(0);
                bean.year = cursor.getInt(1);
                bean.date = cursor.getString(2);
                bean.week_day = cursor.getString(3);
                bean.time = cursor.getString(4);
                bean.isImportent = cursor.getInt(5);
                bean.calendar = cursor.getString(6);
                bean.unix_time = cursor.getInt(7);
            }
            cursor.close();//关闭结果集

        }
        //关闭数据库对象
        db.close();
        return  bean;
    }

    public ArrayList<CalendarInfo> query(String now_unix) {

        ArrayList<CalendarInfo> list = new ArrayList<CalendarInfo>();

        //执行sql语句需要sqliteDatabase对象
        //调用getReadableDatabase方法,来初始化数据库的创建
        SQLiteDatabase db = calendarSqliteOpenHalper.getReadableDatabase();

        //table:表名, columns：查询的列名,如果null代表查询所有列； selection:查询条件, selectionArgs：条件占位符的参数值,
        //groupBy:按什么字段分组, having:分组的条件, orderBy:按什么字段排序
        String[] fields = {"_id", "year", "date", "weekday", "time", "isImportent", "calendar", "unix_time"};
        Cursor cursor = db.query("calendar_info", fields, "unix_time > ?", new String[]{now_unix}, null, null, "unix_time asc");
        //解析Cursor中的数据
        if (cursor != null && cursor.getCount() > 0) {//判断cursor中是否存在数据

            //循环遍历结果集，获取每一行的内容
            while (cursor.moveToNext()) {//条件，游标能否定位到下一行
                CalendarInfo bean = new CalendarInfo();
                //获取数据
                bean._id = cursor.getInt(0);
                bean.year = cursor.getInt(1);
                bean.date = cursor.getString(2);
                bean.week_day = cursor.getString(3);
                bean.time = cursor.getString(4);
                bean.isImportent = cursor.getInt(5);
                bean.calendar = cursor.getString(6);
                bean.unix_time = cursor.getInt(7);
                list.add(bean);
            }
            cursor.close();//关闭结果集

        }
        //关闭数据库对象
        db.close();

        return list;
    }

}
