package com.study.kaoqin.kaoqin.student;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.study.kaoqin.kaoqin.R;
import com.study.kaoqin.kaoqin.base.BaseActivity;
import com.study.kaoqin.kaoqin.constant.Constants;

import qinshi.mylibrary.utils.PreferenceUtils;
import qinshi.mylibrary.utils.ToastUtils;

/**
 * A login screen that offers login via email/password.
 */
public class StudentLoginActivity extends BaseActivity {


    // UI references.
    private EditText mTextView_xuehao;
    private EditText mTextView_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);
        // Set up the login form.
        mTextView_xuehao = (EditText) findViewById(R.id.email);


        mTextView_name = (EditText) findViewById(R.id.password);


        Button mButton_sure = (Button) findViewById(R.id.email_sign_in_button);


        mButton_sure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!TextUtils.isEmpty(mTextView_name.getText().toString()) && !TextUtils.isEmpty(mTextView_xuehao.getText().toString()))
                    saveInfo();
                else
                    ToastUtils.showShort(StudentLoginActivity.this, "请把信息填写完整");
            }


        });


    }


    private void saveInfo() {


        Dialog alertDialog = new AlertDialog.Builder(this).setTitle("提示").setMessage("账号和手机唯一标示绑定，且注册后不可更改，您确定学号和姓名正确吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PreferenceUtils.setPrefBoolean(StudentLoginActivity.this, Constants.ISLOAD, true);
                        PreferenceUtils.setPrefString(StudentLoginActivity.this, Constants.XUEHAO, mTextView_xuehao.getText().toString());
                        PreferenceUtils.setPrefString(StudentLoginActivity.this, Constants.NAME, mTextView_name.getText().toString());

                        StudentActivity.launch(StudentLoginActivity.this);

                        StudentLoginActivity.this.finish();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                }).create();

        alertDialog.show();


    }

    public static void launch(Context mContext) {

        Intent i = new Intent(mContext, StudentLoginActivity.class);
        mContext.startActivity(i);

    }

}

