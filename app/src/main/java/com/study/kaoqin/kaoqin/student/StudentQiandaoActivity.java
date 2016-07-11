package com.study.kaoqin.kaoqin.student;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.study.kaoqin.kaoqin.R;
import com.study.kaoqin.kaoqin.student.fragment.StudentQiandaoFragment;

public class StudentQiandaoActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_qiandao);

        getSupportFragmentManager().beginTransaction().replace(R.id.content, new StudentQiandaoFragment()).commit();


    }
}
