package com.whu.gkcalendar.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.whu.gkcalendar.R;
import com.whu.gkcalendar.bean.ShareCalendarInfo;
import com.whu.gkcalendar.util.TimeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by wwhisdavid on 16/5/14.
 */
public class ShareCalendarAdapter extends BaseAdapter{
    private Context context ;
    private List<ShareCalendarInfo> list;

    public ShareCalendarAdapter(Context context, List<ShareCalendarInfo> list){
        if (list != null)
            this.list = list;
        else
            this.list = new ArrayList<>();
        this.context = context;
    }

    @Override
    public int getCount() {
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
        ShareCalendarInfo lastBean = null;
        String lastBeanDate = null;
        if (position > 0) {
            // 获取上一条记录
            lastBean = list.get(position - 1);
        }

        if (lastBean != null) {
            int stamp = lastBean.unixstamp;
            String dateString = TimeUtil.getDateString(stamp);
            lastBeanDate = dateString.split("#")[0];
        }

        ShareCalendarInfo bean = list.get(position);

        int stamp = bean.unixstamp;
        String dateString = TimeUtil.getDateString(stamp);
        String calendarDate = dateString.split("#")[0]; // 获取当前记录的时间（天）
        String calendarTime = dateString.split("#")[1];

        if (lastBean == null || !lastBeanDate.equals(calendarDate)) {
            view = View.inflate(context, R.layout.calendar_list_item, null);
            String[] dateStr = calendarDate.split("-");
            int month = Integer.valueOf(dateStr[1]);
            int day = Integer.valueOf(dateStr[2]);
            int year = Integer.valueOf(dateStr[0]);
            Date date = new GregorianCalendar(year, month - 1, day, 12, 0, 0).getTime();
            type = TimeUtil.daysBetweenNow(date);

        } else { // 同一天
            type = -1;
            view = View.inflate(context, R.layout.calendar_list_item_without_day, null);
        }


        TextView time = (TextView) view.findViewById(R.id.item_time);
        TextView content = (TextView) view.findViewById(R.id.calendar_content);
        TextView importent = (TextView) view.findViewById(R.id.importent);

        time.setText(calendarTime);
        content.setText(bean.title);

        importent.setText("（紧急）");
        importent.setTextColor(context.getResources().getColor(R.color.green));


//        else if (bean.isImportent == 2) {
//            importent.setText("（重要）");
//            importent.setTextColor(context.getResources().getColor(R.color.yellow));
//        }
//        else if (bean.isImportent == 3)
//            importent.setText("(重要紧急）");
//        else {
//            // time.setVisibility(View.INVISIBLE);
//            // content.setVisibility(View.INVISIBLE);
//        }


        if (type >= 0) {

            TextView weekDay = (TextView) view.findViewById(R.id.week_day_text);
            TextView todayDay = (TextView) view.findViewById(R.id.today_text);
            TextView dateDay = (TextView) view.findViewById(R.id.date_text);

            weekDay.setText(TimeUtil.getWeekDayFromUnix(bean.unixstamp));
            if (type <= 2) {
                dateDay.setText(calendarDate);
                if (type == 0)
                    todayDay.setText("今天");
                else if (type == 1)
                    todayDay.setText("明天");
                else if (type == 2)
                    todayDay.setText("后天");
            } else {
                todayDay.setText(calendarDate);
                dateDay.setText("还有" + type + "天");
            }
        }
        return view;
    }
}
