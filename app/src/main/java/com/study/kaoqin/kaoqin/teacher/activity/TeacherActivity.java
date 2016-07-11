package com.study.kaoqin.kaoqin.teacher.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.study.kaoqin.kaoqin.R;
import com.study.kaoqin.kaoqin.SimpleFragmentPagerAdapter;
import com.study.kaoqin.kaoqin.View.MyViewPager;

import butterknife.Bind;
import butterknife.ButterKnife;
import qinshi.mylibrary.utils.LogUtils;

public class TeacherActivity extends AppCompatActivity {


    public static final String UPDATE_DATA = "update_data";
    MyReceiver myReceiver;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.id_nv_menu)
    NavigationView mIdNvMenu;
    @Bind(R.id.drawlayout)
    DrawerLayout mDrawlayout;

    //    @Bind(R.id.tablayout)
//    TabLayout mTablayout;
    @Bind(R.id.viewpager)
    MyViewPager mViewpager;
    SimpleFragmentPagerAdapter simpleFragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitle("教室考勤");

        myReceiver = new MyReceiver();
        simpleFragmentPagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), this);
        mViewpager.setAdapter(simpleFragmentPagerAdapter);
        mViewpager.setScrollble(false);
        mViewpager.setOffscreenPageLimit(3);
        setupDrawerContent(mIdNvMenu);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(

                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        item.setChecked(true);
                        mDrawlayout.closeDrawers();

                        switch (item.getItemId()) {
                            case R.id.nav_teacher:
                                mViewpager.setCurrentItem(0);
                                LogUtils.i("11");
                                mToolbar.setTitle("教室考勤");
                                break;
                            case R.id.nav_guanli:
                                mViewpager.setCurrentItem(1);
                                mToolbar.setTitle("学生管理");
                                LogUtils.i("12");
                                break;
                            case R.id.nav_add:
                                mViewpager.setCurrentItem(2);
                                mToolbar.setTitle("所有学生");
                                LogUtils.i("3");
                                break;

                        }






                        return true;
                    }
                });
    }

    public static void launch(Context mContext) {

        Intent i = new Intent(mContext, TeacherActivity.class);
        mContext.startActivity(i);

    }


    @Override
    protected void onResume() {
        super.onResume();
        //   registerReceiver(myReceiver, new IntentFilter(UPDATE_DATA));
    }


    @Override
    protected void onPause() {
        super.onPause();

        //   unregisterReceiver(myReceiver);
    }

    public class MyReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            updateDate();
        }


    }

    public void updateDate() {
        LogUtils.i("upadate-----------");
//        MainFragment fragment = (MainFragment) simpleFragmentPagerAdapter.getItem(0);
        ;
//        fragment.updateData();

    }





    @Override
    public boolean onContextItemSelected(MenuItem item) {


        return super.onContextItemSelected(item);
    }
}
