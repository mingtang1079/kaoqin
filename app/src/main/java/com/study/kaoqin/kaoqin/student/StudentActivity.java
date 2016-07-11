package com.study.kaoqin.kaoqin.student;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.study.kaoqin.kaoqin.R;
import com.study.kaoqin.kaoqin.constant.Constants;
import com.study.kaoqin.kaoqin.qrcode.encde.EncodingHandler;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import qinshi.mylibrary.utils.DateUtils;
import qinshi.mylibrary.utils.LogUtils;
import qinshi.mylibrary.utils.PreferenceUtils;

public class StudentActivity extends AppCompatActivity {

    @Bind(R.id.erweima)
    TextView mTextView_er;
    @Bind(R.id.image_erc)
    ImageView mImageView_erc;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    private String content = null;

    @OnClick(R.id.erweima)
    void encode() {

        //生成二维码图片，第一个参数是二维码的内容，第二个参数是正方形图片的边长，单位是像素
        try {
            Bitmap qrcodeBitmap = EncodingHandler.createQRCode(content, 400);

            mImageView_erc.setImageBitmap(qrcodeBitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setTitle("签到");
        content = PreferenceUtils.getPrefString(this, Constants.NAME, "") + "&" + PreferenceUtils.getPrefString(this, Constants.XUEHAO, "") + "&" + DateUtils.getCurrentTime();
        content = recode(content);
        LogUtils.i("info--->", content);
    }

    public static void launch(Context mContext) {

        Intent i = new Intent(mContext, StudentActivity.class);
        mContext.startActivity(i);

    }


    private String recode(String str) {
        String formart = "";

        try {
            boolean ISO = Charset.forName("ISO-8859-1").newEncoder()
                    .canEncode(str);
            if (!ISO) {
                formart = new String(content.getBytes("UTF-8"), "ISO-8859-1");
                Log.i("change -->", formart);

            } else {
                formart = str;
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return formart;
    }
}
