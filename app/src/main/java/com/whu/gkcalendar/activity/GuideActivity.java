package com.whu.gkcalendar.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.whu.gkcalendar.R;
import com.whu.gkcalendar.adapter.GuideAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/13.
 */
public class GuideActivity extends Activity implements ViewPager.OnPageChangeListener {

    private ViewPager vp;
    private GuideAdapter vpa;
    private List<View> views;
    private ImageView[] dots;
    private int[] ids = {R.id.ivpag, R.id.ivpag2, R.id.ivpag3};
    private Button btnStart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide_activity);
        initView();
        initDocs();
        vp.setOnPageChangeListener(this);
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(this);
        views = new ArrayList<View>();
        views.add(inflater.inflate(R.layout.view1, null));
        views.add(inflater.inflate(R.layout.view2, null));
        views.add(inflater.inflate(R.layout.view3, null));

        vpa = new GuideAdapter(views, this);


        vp = (ViewPager) findViewById(R.id.viewPager);


        btnStart = (Button) views.get(2).findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GuideActivity.this, Calendar.class);
                startActivity(i);
            }
        });
        vp.setAdapter(vpa);
    }

    private void initDocs() {
        dots = new ImageView[views.size()];
        for (int i = 0; i < views.size(); i++) {
            dots[i] = (ImageView) findViewById(ids[i]);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < ids.length; i++) {
            if (position == i)
                dots[i].setImageResource(R.drawable.login_point_selected);
            else
                dots[i].setImageResource(R.drawable.login_point);
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}


