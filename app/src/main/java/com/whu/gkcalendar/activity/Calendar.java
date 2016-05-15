package com.whu.gkcalendar.activity;

import android.app.AlarmManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.actionsheet.ActionSheet;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.whu.gkcalendar.R;
import com.whu.gkcalendar.adapter.CalendarAdapter;
import com.whu.gkcalendar.adapter.ShareCalendarAdapter;
import com.whu.gkcalendar.bean.CalendarInfo;
import com.whu.gkcalendar.bean.ShareCalendarInfo;
import com.whu.gkcalendar.dao.CalendarInfoDao;
import com.whu.gkcalendar.networking.CalendarNetworking;
import com.whu.gkcalendar.util.AlarmUtil;
import com.whu.gkcalendar.util.CaledarUpdateManager;
import com.whu.gkcalendar.util.TimeUtil;
import com.whu.gkcalendar.util.UserUtil;

import java.util.ArrayList;
import java.util.List;

public class Calendar extends AppCompatActivity implements ActionSheet.ActionSheetListener, AdapterView.OnItemClickListener {
    private Context mContext = this;
    private CalendarInfoDao dao = null;
    private List<CalendarInfo> infoList = null;
    private List<ShareCalendarInfo> sharedList = null;
    private static int position = -1;
    private SwipeMenuListView listView = null;
    private SwipeMenuListView calendarList;
    private Button addBtn;

    //选择事件类型
    private LinearLayout drawerLin;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ArrayList<String> menuList;
    private ArrayAdapter<String> arrayAdapter;
    private Button btnMenu;
    private TextView titleTextView;

    //登录按钮
    private ImageView ivLogin;
    private TextView usernameTV;

    private int activityType = 0; // 默认为0
    private String newMember = null;

    private Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            boolean success = (boolean) msg.obj;
            if (success) {
                activityType = 0;
                refreshDrawer();
                refreshData(activityType);
            }
        }
    };

    private Handler networkHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ShareCalendarAdapter adapter = (ShareCalendarAdapter) msg.obj;
            if (adapter != null)
                listView.setAdapter(adapter);
        }
    };

    private Handler addHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            boolean success = (boolean) msg.obj;
            if (success) {
                Toast.makeText(mContext, "添加成员成功", Toast.LENGTH_SHORT).show();
            }else
                Toast.makeText(mContext, "添加成员失败", Toast.LENGTH_SHORT).show();
        }
    };


    private void refreshDrawer() {
        if (UserUtil.isLogin && !menuList.contains("退出登陆")) {
            menuList.add("退出登陆");
            usernameTV.setText(UserUtil.userName);
        } else if(!UserUtil.isLogin){
            usernameTV.setText("未登录");
            if (menuList.contains("退出登陆")) {
                menuList.remove("退出登陆");
                arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuList);
                drawerList.setAdapter(arrayAdapter);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData(activityType);
        drawerLayout.closeDrawer(Gravity.LEFT);
        refreshDrawer();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent outIntent = getIntent();
        if (outIntent != null) {
            String token = outIntent.getStringExtra("token");
            if (token != null && token.equals("whu")) { // 校验是否是通过推迟一天打开
                String _id = outIntent.getStringExtra("_id");
                System.out.println("id:" + _id);
                if (_id != null) {
                    CalendarInfo info = dao.queryWithID(_id);
                    if (info != null) {
                        System.out.println(info._id + "----" + info.unix_time);
                        delayDay(info);
                    }
                }
            }
        }
        setContentView(R.layout.activity_calendar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerList = (ListView) findViewById(R.id.drawerList);
        drawerLin = (LinearLayout) findViewById(R.id.linearDrawer);
        ivLogin = (ImageView) findViewById(R.id.iv_login);
        usernameTV = (TextView) findViewById(R.id.drawer_username);
        titleTextView = (TextView) findViewById(R.id.calendar_title_textView);

        menuList = new ArrayList<String>();
        //左侧菜单栏的配置
        menuList.add("全部事项");
        menuList.add("重要紧急");
        menuList.add("重要不紧急");
        menuList.add("紧急不重要");
        menuList.add("日常琐事");
        menuList.add("公共备忘录");

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuList);
        drawerList.setAdapter(arrayAdapter);
        drawerList.setOnItemClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        addBtn = (Button) findViewById(R.id.addBtn);
        btnMenu = (Button) findViewById(R.id.menuBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go2Edit = new Intent(mContext, EditActivity.class);
                if (activityType == 0) {
                    go2Edit.putExtra("activityType", 0);
                    go2Edit.putExtra("isAddShared", false);
                }else if (activityType == 1){
                    go2Edit.putExtra("activityType", 1);
                    go2Edit.putExtra("isAddShared", true);
                }
                mContext.startActivity(go2Edit);
            }
        });
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        ivLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserUtil.isLogin) {
                    Toast.makeText(mContext, "已登陆！", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent goLogin = new Intent(mContext, LoginActivity.class);
                startActivity(goLogin);

            }
        });
        setSupportActionBar(toolbar);

        dao = new CalendarInfoDao(mContext);
//        testDB(dao);
        int timestamp = (int) (System.currentTimeMillis() / 1000) - 5;
//        System.out.println("timestamp：~~~"+timestamp);
        List list = dao.query(timestamp + "", -1); // test
        infoList = list;

//        testQuery(dao);
        calendarList = (SwipeMenuListView) findViewById(R.id.calendar_list);
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
                if(activityType == 0) {
                    CalendarInfo info = infoList.get(position);
                    dao.delete(info);
                    AlarmUtil.cancelAlarm(mContext, info._id, info.ring2);
                    refreshData(1);
                    return false;
                }else if (activityType == 1){
                    Toast.makeText(mContext, "暂不支持！", Toast.LENGTH_SHORT).show();
                    return false;
                }
                return false;

            }
        });
    }

    private void delayDay(CalendarInfo info) {
        int tomorrow = info.unix_time + 3600 * 24;
        info.unix_time = tomorrow;
        String dateStr = TimeUtil.getDateStringFromUnix(tomorrow);
        String[] dates = dateStr.split(":");
        info.year = Integer.valueOf(dates[0]);
        String date = dates[1];
        if (date.substring(0, 1).equals("0")) {
            date = date.substring(1);
        }
        info.date = date;
        info.week_day = dates[2];
        dao.update("year", info);
        dao.update("date", info);
        dao.update("unix_time", info);
        dao.update("weekday", info);
        AlarmUtil.cancelAlarm(mContext, info._id, info.ring2);
        AlarmUtil.registerAlarm(mContext, info);
        refreshData(activityType);
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
        refreshData(activityType);

        AlarmUtil.registerAlarm(mContext, bean2);
    }

    private void testQuery(CalendarInfoDao dao) {
        List<CalendarInfo> list = dao.query("1460440800", -1);

        for (CalendarInfo info : list) {
            System.out.println(info.calendar);
        }
    }

    private void refreshData(int flag) { // flag:0 本地逻辑  1：网络逻辑
        if (flag == 0) {
            titleTextView.setText("我的日程");
            int timestamp = (int) (System.currentTimeMillis() / 1000) - 5;
            infoList = dao.query("" + timestamp, -1);
            CalendarAdapter adapter = new CalendarAdapter(mContext, infoList);
            listView.setAdapter(adapter);
        } else {
            titleTextView.setText("共享日程");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<ShareCalendarInfo> list = CalendarNetworking.fetchList(UserUtil.userName, UserUtil.currentToken);
                    sharedList = list;
                    ShareCalendarAdapter adapter = new ShareCalendarAdapter(mContext, list);
                    Message msg = Message.obtain();
                    msg.obj = adapter;
                    networkHandler.sendMessage(msg);
                }
            }).start();
        }
    }

    private void divide(int imporDegree) {
        int timestamp = (int) (System.currentTimeMillis() / 1000) - 5;
        infoList = dao.query("" + timestamp, imporDegree);
        CalendarAdapter adapter = new CalendarAdapter(mContext, infoList);
        listView.setAdapter(adapter);
    }

    private void editData(CalendarInfo info) {
        Intent intent = new Intent(mContext, EditActivity.class);

        String strCalendar = info.calendar, strDate = info.date, strTime = info.time, strYear = String.valueOf(info.year);
        int isImpor = info.isImportent;
        Bundle bundle = new Bundle();
        bundle.putString("calendar", strCalendar);
        // bundle.putString("year",strYear);
        bundle.putString("date", strYear + "-" + strDate);
        bundle.putString("time", strTime);
        bundle.putString("ring", info.ring2);
        bundle.putInt("isImpor", isImpor);
        bundle.putInt("activityType", activityType);
        intent.putExtra("editData", bundle);
        startActivityForResult(intent, 1);


    }

    private void editNetDate(ShareCalendarInfo info) {
        Intent intent = new Intent(mContext, EditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("calendar", info.content);
        bundle.putInt("unixstamp", info.unixstamp);
        bundle.putInt("version", info.version);
        bundle.putInt("activityType", activityType);
        bundle.putString("creator", info.creator);
        bundle.putString("id", info.id);
        intent.putExtra("editData", bundle);
        startActivityForResult(intent, 2);

    }

    private void showInputDialog(final ShareCalendarInfo info) {

        final EditText inputServer = new EditText(this);
        inputServer.setFocusable(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.addMem)).setView(inputServer).setNegativeButton(
                "取消", null);
        builder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        String inputName = inputServer.getText().toString();
                        if(inputName.length() == 0)
                            Toast.makeText(mContext, "用户名不能为空", Toast.LENGTH_SHORT).show();
                        else {
                            newMember = inputName;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    boolean success = CalendarNetworking.addMember(UserUtil.userName, newMember, UserUtil.currentToken, info.id);
                                    Message msg = Message.obtain();
                                    msg.obj = success;
                                    addHandler.sendMessage(msg);
                                }
                            }).start();
                        }
                    }
                });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    CalendarInfo info = infoList.get(position);
                    dao.delete(info);
                    AlarmUtil.cancelAlarm(mContext, info._id, info.ring2);
                }
                break;

            case 2:
                if (resultCode == RESULT_OK) {
                    refreshData(1);
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
        CalendarInfo info = null;
        if (actionSheet.getTag().equals("0")) {
            info = infoList.get(position);
        }
        switch (index) {
            case 0: // 编辑
                if (actionSheet.getTag().equals("0"))
                    editData(info);
                else {
                    ShareCalendarInfo sharedInfo = sharedList.get(position);
                    editNetDate(sharedInfo);
                }
                break;
            case 1: //  标为紧急重要 / 添加组员
                if (actionSheet.getTag().equals("0")) {
                    info.isImportent = 3;
                    dao.update("isImportent", info);
                    refreshData(activityType);
                }else { // 添加组员逻辑

                    final ShareCalendarInfo sharedInfo = sharedList.get(position);
                    showInputDialog(sharedInfo);

                }
                break;
            case 2: // 推后一天
                delayDay(info);
                break;
            case 3: // 设为已结束(暂定删除)
                dao.delete(info);
                AlarmUtil.cancelAlarm(mContext, info._id, info.ring2);
                refreshData(activityType);
                break;
        }
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        int importDegree = 0;
        if (activityType == 0) {
            if (parent == calendarList) {
                this.position = position;
                ActionSheet
                        .createBuilder(this, getSupportFragmentManager())
                        .setTag(""+activityType)
                        .setCancelButtonTitle("取消")
                        .setOtherButtonTitles("编辑", "标为紧急重要", "推后一天", "设为已结束")
                        .setCancelableOnTouchOutside(true)
                        .setListener(this).show();
            } else if (parent == drawerList) {
                switch (position) {
                    case 0://全部
                        importDegree = 0;
                        divide(-1);
                        break;
                    case 1://重要紧急
                        divide(3);
                        break;
                    case 2://重要
                        divide(2);
                        break;
                    case 3://紧急
                        divide(1);
                        break;
                    case 4://琐事
                        divide(0);
                        break;

                    case 5: // 公共备忘
                        if (!UserUtil.isLogin) {
                            Toast.makeText(mContext, "请先登陆", Toast.LENGTH_SHORT);
                            Intent goLogin = new Intent(mContext, LoginActivity.class);
                            startActivity(goLogin);
                        } else {
                            activityType = 1;
                            refreshData(activityType);
                            drawerLayout.closeDrawer(Gravity.LEFT);
                        }


                        break;
                    case 6: // 退出登陆
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

                                String username = sharedPreferences.getString("username", "");
                                String token = sharedPreferences.getString("token", "");
                                UserUtil.isLogin = false;
                                UserUtil.currentToken = "";
                                boolean success = UserUtil.logout(username, token);

                                Message msg = Message.obtain();
                                msg.obj = success;
                                handle.sendMessage(msg);
                            }
                        }).start();
                        break;
                    default:
                        break;
                }

                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        }else if(activityType == 1){
            if (parent == calendarList) {
                this.position = position;
                ActionSheet.createBuilder(this, getSupportFragmentManager())
                        .setTag("" + activityType)
                        .setCancelButtonTitle("取消")
                        .setOtherButtonTitles("编辑", "添加组员")
                        .setCancelableOnTouchOutside(true)
                        .setListener(this).show();
            }else if (parent == drawerList){
                switch(position){
                    case 5: // 公共备忘
                        if (!UserUtil.isLogin) {
                            Toast.makeText(mContext, "请先登陆", Toast.LENGTH_SHORT);
                            Intent goLogin = new Intent(mContext, LoginActivity.class);
                            startActivity(goLogin);
                        } else {
                            activityType = 1;
                            refreshData(activityType);
                            drawerLayout.closeDrawer(Gravity.LEFT);
                        }


                        break;
                    case 6: // 退出登陆
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

                                String username = sharedPreferences.getString("username", "");
                                String token = sharedPreferences.getString("token", "");
                                UserUtil.isLogin = false;
                                UserUtil.currentToken = "";
                                boolean success = UserUtil.logout(username, token);

                                Message msg = Message.obtain();
                                msg.obj = success;
                                handle.sendMessage(msg);

                            }
                        }).start();
                        break;
                    default:
                        Toast.makeText(mContext, "该模式下无法使用该功能", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }

    }

}
