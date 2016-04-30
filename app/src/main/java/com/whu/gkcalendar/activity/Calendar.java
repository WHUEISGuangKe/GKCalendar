package com.whu.gkcalendar.activity;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baoyz.actionsheet.ActionSheet;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.whu.gkcalendar.R;
import com.whu.gkcalendar.adapter.CalendarAdapter;
import com.whu.gkcalendar.bean.CalendarInfo;
import com.whu.gkcalendar.dao.CalendarInfoDao;
import com.whu.gkcalendar.util.AlarmUtil;
import com.whu.gkcalendar.util.TimeUtil;

import java.util.List;

public class Calendar extends AppCompatActivity implements ActionSheet.ActionSheetListener, AdapterView.OnItemClickListener {
    private Context mContext = this;
    private CalendarInfoDao dao = null;
    private List<CalendarInfo> infoList = null;
    private static int position = -1;
    private SwipeMenuListView listView = null;

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent outIntent = getIntent();
        if(outIntent != null){
            String token = outIntent.getStringExtra("token");
            if (token != null && token.equals("whu")){ // 校验是否是通过推迟一天打开
                String _id = outIntent.getStringExtra("_id");
                System.out.println("id:"+_id);
                if (_id != null) {
                    CalendarInfo info = dao.queryWithID(_id);
                    if(info != null) {
                        System.out.println(info._id + "----" + info.unix_time);
                        delayDay(info);
                    }
                }
            }
        }
        setContentView(R.layout.activity_calendar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        Button addBtn = (Button) toolbar.findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go2Edit = new Intent(mContext, EditActivity.class);
                mContext.startActivity(go2Edit);
            }
        });
        setSupportActionBar(toolbar);

        dao = new CalendarInfoDao(mContext);
//        testDB(dao);
        int timestamp = (int) (System.currentTimeMillis() / 1000) - 5;
//        System.out.println("timestamp：~~~"+timestamp);
        List list = dao.query(timestamp + ""); // test
        infoList = list;

//        testQuery(dao);
        SwipeMenuListView calendarList = (SwipeMenuListView) findViewById(R.id.calendar_list);
        CalendarAdapter calendarAdapter = new CalendarAdapter(mContext, list);
        calendarList.setAdapter(calendarAdapter);

        calendarList.setOnItemClickListener(this);

        listView = calendarList;

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(R.color.red);
                // set item width
                deleteItem.setWidth(180);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        listView.setMenuCreator(creator);
        listView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {


            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                CalendarInfo info = infoList.get(position);
                dao.delete(info);
                AlarmUtil.cancelAlarm(mContext, info._id);
                refreshData();
                return false;
            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_calendar, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }



    private void delayDay(CalendarInfo info){
        int tomorrow = info.unix_time + 3600 * 24;
        info.unix_time = tomorrow;
        String dateStr = TimeUtil.getDateStringFromUnix(tomorrow);
        String[] dates = dateStr.split(":");
        info.year = Integer.valueOf(dates[0]);
        String date = dates[1];
        if (date.substring(0,1).equals("0")){
            date = date.substring(1);
        }
        info.date = date;
        info.week_day = dates[2];
        dao.update("year", info);
        dao.update("date", info);
        dao.update("unix_time", info);
        dao.update("weekday", info);
        AlarmUtil.cancelAlarm(mContext, info._id);
        AlarmUtil.registerAlarm(mContext, info);
        refreshData();
    }

    private void testDB(CalendarInfoDao dao) {

        CalendarInfo bean2 = new CalendarInfo();
        bean2.year = 2016;
        bean2.date = "4-13";
        bean2.week_day = "Wed";
        bean2.time = 18 + ":23";
        bean2.isImportent = 1;
        bean2.calendar = "20~coding!coding!coding!coding!coding!coding!coding!coding!coding!coding!coding!coding!";
        bean2.unix_time = 1460542980;
        bean2._id = 1019;

        dao.add(bean2);
        refreshData();

        AlarmUtil.registerAlarm(mContext, bean2);
    }

    private void testQuery(CalendarInfoDao dao) {
        List<CalendarInfo> list = dao.query("1460440800");

        for (CalendarInfo info : list) {
            System.out.println(info.calendar);
        }
    }

    private void refreshData(){
        int timestamp = (int) (System.currentTimeMillis() / 1000) - 5;
        infoList = dao.query("" + timestamp);
        CalendarAdapter adapter = new CalendarAdapter(mContext, infoList);
        listView.setAdapter(adapter);
    }
    private void editData(CalendarInfo info){
        Intent intent=new Intent(mContext,EditActivity.class);
        String strCalendar=info.calendar,strDate=info.date,strTime=info.time,strYear=String.valueOf(info.year);
        Bundle bundle=new Bundle();
        bundle.putString("calendar",strCalendar);
       // bundle.putString("year",strYear);
        bundle.putString("date",strYear+"-"+strDate);
        bundle.putString("time",strTime);
        intent.putExtra("editData", bundle);
        startActivityForResult(intent, 1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       // super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 1:
                if(resultCode==RESULT_OK) {
                    CalendarInfo info = infoList.get(position);

                    dao.delete(info);
                    AlarmUtil.cancelAlarm(mContext, info._id);
                }
                break;
        }
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
//        Toast.makeText(getApplicationContext(), "dismissed isCancle = " + isCancel, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        CalendarInfo info = infoList.get(position);
        switch (index){
            case 0: // 编辑
                editData(info);
                break;
            case 1: // 标为重要
                info.isImportent = 1;
                dao.update("isImportent", info);
                refreshData();
                break;
            case 2: // 推后一天
                delayDay(info);
                break;
            case 3: // 设为已结束(暂定删除)
                dao.delete(info);
                AlarmUtil.cancelAlarm(mContext, info._id);
                refreshData();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.position = position;
        ActionSheet.createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle("取消")
                .setOtherButtonTitles("编辑","标为重要","推后一天","设为已结束")
                .setCancelableOnTouchOutside(true)
                .setListener(this).show();
    }

}
