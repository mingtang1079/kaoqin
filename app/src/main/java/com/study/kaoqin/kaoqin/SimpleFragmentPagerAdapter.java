package com.study.kaoqin.kaoqin;


import android.support.v4.app.Fragment;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.study.kaoqin.kaoqin.teacher.activity.fragment.AddStudentFragment;
import com.study.kaoqin.kaoqin.teacher.activity.fragment.CountFragment;
import com.study.kaoqin.kaoqin.teacher.activity.fragment.MainFragment;

/**
 * Created by Administrator on 2016/3/23.
 */
public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {


    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[]{"主页", "统计"};
    private Context context;

    public SimpleFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new MainFragment();
        } else if (position == 1)
            return new CountFragment();
        else {
            return new AddStudentFragment();
        }

    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
