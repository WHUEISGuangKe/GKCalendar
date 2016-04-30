package com.whu.gkcalendar.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.whu.gkcalendar.R;
import com.whu.gkcalendar.bean.CalendarInfo;
import com.whu.gkcalendar.util.TimeUtil;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by wwhisdavid on 16/4/12.
 */
public class CalendarAdapter extends BaseAdapter {
    private Context context;
    private List<CalendarInfo> list;
    private String nowDate;

    public CalendarAdapter(Context context, List<CalendarInfo> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        nowDate = TimeUtil.getTodayString();
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        int type = -1; // type: 0:同一天 1：不同一天,s数字表示与现在的日期差 + 1
        CalendarInfo lastBean = null;
        String lastBeanDate = null;
        if (position > 0) {
            // 获取上一条记录
            lastBean = list.get(position - 1);
        }
        if (lastBean != null){
            lastBeanDate = lastBean.year + "-" + lastBean.date;
        }


        CalendarInfo bean = list.get(position);

        String calendarDate = bean.year + "-" + bean.date; // 获取当前记录的时间（天）



        if (lastBean == null || !lastBeanDate.equals(calendarDate)) {
            view = View.inflate(context, R.layout.calendar_list_item, null);
            String[] dateStr = bean.date.split("-");
            int month = Integer.valueOf(dateStr[0]);
            int day = Integer.valueOf(dateStr[1]);
            Date date = new GregorianCalendar(bean.year, month - 1, day, 12, 0, 0).getTime();
            type = TimeUtil.daysBetweenNow(date);

        } else { // 同一天
            type = -1;
            view = View.inflate(context, R.layout.calendar_list_item_without_day, null);
        }


        TextView time = (TextView) view.findViewById(R.id.item_time);
        TextView content = (TextView) view.findViewById(R.id.calendar_content);
        TextView importent = (TextView) view.findViewById(R.id.importent);

        time.setText(bean.time);
        content.setText(bean.calendar);
        if (bean.isImportent == 1) {
            importent.setText("*");
        }
        if (type >= 0) {

            TextView weekDay = (TextView) view.findViewById(R.id.week_day_text);
            TextView todayDay = (TextView) view.findViewById(R.id.today_text);
            TextView dateDay = (TextView) view.findViewById(R.id.date_text);

            weekDay.setText(bean.week_day);
            if (type <= 2) {
                dateDay.setText(bean.date);
                if (type == 0)
                    todayDay.setText("今天");
                else if (type == 1)
                    todayDay.setText("明天");
                else if (type == 2)
                    todayDay.setText("后天");
            } else {
                todayDay.setText(bean.date);
                dateDay.setText("还有" + type + "天");
            }
        }
        return view;
    }
}
