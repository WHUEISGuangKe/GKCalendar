package com.whu.gkcalendar.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.whu.gkcalendar.R;
import com.whu.gkcalendar.bean.CalendarInfo;
import com.whu.gkcalendar.bean.ShareCalendarInfo;
import com.whu.gkcalendar.dao.CalendarInfoDao;
import com.whu.gkcalendar.networking.CalendarNetworking;
import com.whu.gkcalendar.util.AlarmUtil;
import com.whu.gkcalendar.util.CaledarUpdateManager;
import com.whu.gkcalendar.util.TimeUtil;
import com.whu.gkcalendar.util.UserUtil;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditActivity extends Activity implements View.OnClickListener {

    private Button btnBack, btnSave, btnClock;
    private TextView tvTime, tvDate, editText;
    private static final int REQUEST_CODE_PICK_RINGTONE = 1;
    private CalendarInfoDao dao;
    //保存铃声的Uri的字符串形式
    private String mRingtoneUri = null;
    private Context mContext = this;
    private CheckBox cbImpo, cbUrge;
    private int activityType = 0;
    private boolean isAddShared = false;

    private Context context = this;
    private ShareCalendarInfo sharedInfo = new ShareCalendarInfo();
    private Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            boolean success = (boolean) msg.obj;
            if (success) {
                Toast.makeText(context, "修改成功！", Toast.LENGTH_SHORT).show();
                CaledarUpdateManager.calendarQueue.add(sharedInfo);
                setResult(RESULT_OK, new Intent());
                finish();
            } else {
                Toast.makeText(context, "修改失败，日程可能已被修改，请刷新后重试！", Toast.LENGTH_SHORT).show();
            }

        }
    };

    private Handler createHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            boolean success = (boolean) msg.obj;
            if (success) {
                Toast.makeText(context, "添加日程成功！", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK, new Intent());
                finish();
            } else {
                Toast.makeText(context, "添加失败！", Toast.LENGTH_SHORT).show();
            }

        }
    };

    private int handleDate() {
        String dateString = tvDate.getText() + "#" + tvTime.getText();
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd#HH:mm").parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long unixTimestamp = date.getTime() / 1000;
        return Integer.valueOf(String.valueOf(unixTimestamp));
    }

    private CalendarInfo capsulation(CalendarInfo info, int timestamp) {
        info.unix_time = timestamp;
        String dateStr = TimeUtil.getDateStringFromUnix(timestamp);
//        System.out.println(dateStr);

        String[] dates = dateStr.split(":");
        info.year = Integer.valueOf(dates[0]);
        String date = dates[1];
        if (date.substring(0, 1).equals("0")) {
            date = date.substring(1);
        }
        info.date = date;
        info.week_day = dates[2];
        info.time = tvTime.getText().toString();
        info.calendar = editText.getText().toString().trim();
        info._id = timestamp + info.calendar.hashCode() + info.isImportent;
        return info;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit);
        tvDate = (TextView) findViewById(R.id.tvdate);
        tvTime = (TextView) findViewById(R.id.tvtime);
        btnClock = (Button) findViewById(R.id.btnclock);
        btnBack = (Button) findViewById(R.id.btnback);
        btnSave = (Button) findViewById(R.id.btnsave);
        editText = (TextView) findViewById(R.id.editText);
        cbImpo = (CheckBox) findViewById(R.id.checkboxImpor);
        cbUrge = (CheckBox) findViewById(R.id.checkboxUrge);

        //btnClock.setVisibility(View.INVISIBLE);
        btnSave.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        tvDate.setOnClickListener(this);
        tvTime.setOnClickListener(this);
        btnClock.setOnClickListener(this);

        tvDate.setText(TimeUtil.getTodayString());
        Intent intent = getIntent();

        activityType = intent.getIntExtra("activityType", 0);
        isAddShared = intent.getBooleanExtra("isAddShared", false);

        if (intent.getBundleExtra("editData") != null) {
            activityType = intent.getBundleExtra("editData").getInt("activityType");
            if (activityType == 0) {
                Log.d("EditActivity..", "intentBundle!=null");
                editText.setText(intent.getBundleExtra("editData").getString("calendar"));
                tvDate.setText(intent.getBundleExtra("editData").getString("date"));
                tvTime.setText(intent.getBundleExtra("editData").getString("time"));
                String ringT = intent.getBundleExtra("editData").getString("ring");
                if (ringT.equals(""))
                    btnClock.setText("NONE");
                else {
                    btnClock.setText(RingtoneManager.getRingtone(this, Uri.parse(ringT)).getTitle(this));
                    CalendarInfo.ring = Uri.parse(ringT);
                }
                int i = intent.getBundleExtra("editData").getInt("isImpor");
                // Log.d("EditActivity........",String.format("%d",i));
                if (i == 1)
                    cbUrge.setChecked(true);
                if (i == 2)
                    cbImpo.setChecked(true);
                if (i == 3) {
                    cbImpo.setChecked(true);
                    cbUrge.setChecked(true);
                }
            } else if (activityType == 1) {
                sharedInfo.content = intent.getBundleExtra("editData").getString("calendar");
                sharedInfo.unixstamp = intent.getBundleExtra("editData").getInt("unixstamp");
                sharedInfo.creator = intent.getBundleExtra("editData").getString("creator");
                sharedInfo.id = intent.getBundleExtra("editData").getString("id");
                sharedInfo.version = intent.getBundleExtra("editData").getInt("version");

                editText.setText(sharedInfo.content);
                String dateStr = TimeUtil.getDateString(sharedInfo.unixstamp);
                String[] dates = dateStr.split("#");

                tvDate.setText(dates[0]);
                tvTime.setText(dates[1]);
            }
        }
        if (activityType == 1){
            cbImpo.setVisibility(View.INVISIBLE);
            cbUrge.setVisibility(View.INVISIBLE);
            btnClock.setEnabled(false);
        }
        dao = new CalendarInfoDao(mContext);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvdate:
                new DatePickerDialog(EditActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String monthStr = monthOfYear + 1 < 10 ? "0" + (monthOfYear + 1) : 1 + monthOfYear + "";
                        String dayStr = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
//                        String string = String.format("%d-%d-%d", year, monthOfYear + 1, dayOfMonth);
                        tvDate.setText(year + "-" + monthStr + "-" + dayStr);
                    }
                }, TimeUtil.getYear(), TimeUtil.getMonth() - 1, TimeUtil.getDay_month()).show();
                break;
            case R.id.tvtime:
                new TimePickerDialog(EditActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String minStr = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
                        String secStr = minute < 10 ? "0" + minute : "" + minute;
//                        String string = String.format("%2d:%2d", hourOfDay, minute);
                        tvTime.setText(minStr + ":" + secStr);
                    }
                }, 00, 00, true).show();
                break;
            case R.id.btnclock:
                doPickRingtone();
                break;

            case R.id.btnback:
                setResult(RESULT_CANCELED, new Intent());
                this.finish();
                break;

            case R.id.btnsave:
                if (activityType == 0) {
                    CalendarInfo info = new CalendarInfo();
                    CalendarInfo queryInfo = null;
                    int timestamp = handleDate();
                    if (cbImpo.isChecked() && cbUrge.isChecked())
                        info.isImportent = 3;
                    else if (!cbImpo.isChecked() && cbUrge.isChecked())
                        info.isImportent = 1;
                    else if (cbImpo.isChecked() && !cbUrge.isChecked())
                        info.isImportent = 2;
                    else
                        info.isImportent = 0;

                    info = capsulation(info, timestamp);
                    int currentTimestamp = (int) (System.currentTimeMillis() / 1000);
//                System.out.println(currentTimestamp +"~~~~~~~~~~~~~~"+info.unix_time);
                    if (info.unix_time <= currentTimestamp) {
                        Toast.makeText(mContext, "日程已过时", Toast.LENGTH_LONG).show();
                        break;
                    }
                    //
                    String token = info.unix_time + info.calendar + info.isImportent;
                    Log.d("EditActivity......token", token);
                    String nowId = info.unix_time + info.calendar.hashCode() + info.isImportent + "";
                    Log.d("EditActivity....nowId", nowId);
                    queryInfo = dao.queryWithID(nowId);
//                System.out.println(token + "---" + (info.unix_time + info.calendar.hashCode()));
                    if (queryInfo != null) {
                        String str = queryInfo.unix_time + queryInfo.calendar + queryInfo.isImportent;
                        Log.d("EditActivity.....str", str);
                        if (str.equals(token)) {
//                        System.out.println("idTest2:" + str);
                            Toast.makeText(mContext, "已有该日程", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                    if (CalendarInfo.ring != null)
                        info.ring2 = CalendarInfo.ring.toString();
                    else
                        info.ring2 = "";
                    dao.add(info);
                    //添加注册闹钟
                    info.ring1 = CalendarInfo.ring;
                    AlarmUtil.registerAlarm(mContext, info);
                    CalendarInfo.ring = null;
                    setResult(RESULT_OK, new Intent());
                    finish();
                } else if (activityType == 1) {
                    sharedInfo.content = editText.getText().toString().trim();
                    sharedInfo.unixstamp = handleDate();
                    if (isAddShared) {
                        showInputDialog(sharedInfo);
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                boolean success = CalendarNetworking.alterCalendar(
                                        sharedInfo.version,
                                        sharedInfo.id,
                                        UserUtil.userName,
                                        sharedInfo.content,
                                        sharedInfo.unixstamp,
                                        UserUtil.currentToken);
                                Message msg = Message.obtain();
                                msg.obj = success;
                                handle.sendMessage(msg);
                            }
                        }).start();
                    }
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE_PICK_RINGTONE: {
                Uri pickedUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                CalendarInfo.ring = pickedUri;
                handleRingtonePicked(pickedUri);
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED, new Intent());
        super.onBackPressed();

    }

    @Override
    protected void onStop() {
        CalendarInfo.ring = null;
        super.onStop();
    }

    private void doPickRingtone() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        // Allow user to pick 'Default'
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        // Show only ringtones
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
        // Don't show 'Silent'
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);

        Uri ringtoneUri;
        if (mRingtoneUri != null) {
            ringtoneUri = Uri.parse(mRingtoneUri);
        } else {
            // Otherwise pick default ringtone Uri so that something is
            // selected.
            ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        }

        // Put checkmark next to the current ringtone for this contact
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringtoneUri);

        // Launch!
        // startActivityForResult(intent, REQUEST_CODE_PICK_RINGTONE);
        startActivityForResult(intent, REQUEST_CODE_PICK_RINGTONE);
    }

    private void handleRingtonePicked(Uri pickedUri) {
        if (pickedUri == null) {
            mRingtoneUri = null;
        } else {
            mRingtoneUri = pickedUri.toString();
        }
        // get ringtone name and you can save mRingtoneUri for database.
        if (mRingtoneUri != null) {
            btnClock.setText(RingtoneManager.getRingtone(this, pickedUri).getTitle(this));
        } else {
            btnClock.setText("NONE");
        }
    }

    private void showInputDialog(final ShareCalendarInfo info) {

        final EditText inputServer = new EditText(this);
        inputServer.setFocusable(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.createCal)).setView(inputServer).setNegativeButton(
                "取消", null);
        builder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        String inputName = inputServer.getText().toString();
                        if (inputName.length() == 0)
                            Toast.makeText(mContext, "标题不能为空", Toast.LENGTH_SHORT).show();
                        else {
                            info.title = inputName;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    boolean success = false;
                                    try {
                                        success = CalendarNetworking.createCalendar(UserUtil.userName, info.title, info.unixstamp, info.content, UserUtil.currentToken);
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                    Message msg = Message.obtain();
                                    msg.obj = success;
                                    createHandle.sendMessage(msg);
                                }
                            }).start();
                        }
                    }
                });
        builder.show();
    }
}
