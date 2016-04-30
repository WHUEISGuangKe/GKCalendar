package com.whu.gkcalendar.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.whu.gkcalendar.R;

import java.util.ArrayList;

/**
 * Created by wwhisdavid on 16/4/12.
 */
public class DayAdapter extends BaseAdapter{
    private Context context;


    public DayAdapter(Context context,ArrayList list){
        this.context = context;
    }

    @Override
    public int getCount() {
        return 50;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if(convertView == null){
            view = View.inflate(context, R.layout.day_list, null);
        }else
            view = convertView;

        TextView weekDay = (TextView)view.findViewById(R.id.week_day_text);
        TextView todayDay = (TextView)view.findViewById(R.id.today_text);
        TextView dateDay = (TextView)view.findViewById(R.id.date_text);

        weekDay.setText("周一");
        todayDay.setText("今天");
        dateDay.setText("4-11");

        return view;
    }
}
