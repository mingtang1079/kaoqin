package com.study.kaoqin.kaoqin.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.study.kaoqin.kaoqin.R;
import com.study.kaoqin.kaoqin.entity.Student;

import java.util.List;

import qinshi.mylibrary.view.recyclerview.BaseAdapterHelper;
import qinshi.mylibrary.view.recyclerview.QuickAdapter;

/**
 * Created by Administrator on 2016/4/19.
 */
public class StudentCountAdapter extends QuickAdapter<Student> {


    public StudentCountAdapter(Context context, int layoutResId, List<Student> data) {
        super(context, layoutResId, data);
    }

    @Override
    protected void convert(BaseAdapterHelper helper, Student item) {
        helper.getTextView(R.id.avatar_tv).setText(item.getName().substring(0, 1));
        helper.getTextView(R.id.student_name).setText(item.getName());
        helper.getTextView(R.id.student_count).setText("签到次数：" + item.getCount());
        helper.getTextView(R.id.student_xuehao).setText("学号："+item.getXuehao());
    }
}



