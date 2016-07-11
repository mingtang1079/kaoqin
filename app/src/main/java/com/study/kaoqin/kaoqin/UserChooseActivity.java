package com.study.kaoqin.kaoqin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.study.kaoqin.kaoqin.constant.Constants;
import com.study.kaoqin.kaoqin.student.StudentActivity;
import com.study.kaoqin.kaoqin.student.StudentLoginActivity;
import com.study.kaoqin.kaoqin.teacher.activity.TeacherActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import qinshi.mylibrary.utils.PreferenceUtils;

public class UserChooseActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.teacher)
    Button mTextView_teacher;
    @Bind(R.id.student)
    Button mTextView_student;


    @OnClick(R.id.student)
    void s() {
        if (PreferenceUtils.getPrefBoolean(this, Constants.ISLOAD, false)) {
            StudentActivity.launch(this);
        } else
            StudentLoginActivity.launch(this);
        finish();

    }


    @OnClick(R.id.teacher)
    void t() {


        TeacherActivity.launch(this);

        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_choose);
        ButterKnife.bind(this);


        setSupportActionBar(toolbar);

        toolbar.setTitle(getResources().getString(R.string.app_name));


    }


}
