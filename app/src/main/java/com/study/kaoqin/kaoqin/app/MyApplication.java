package com.study.kaoqin.kaoqin.app;

import android.app.Application;

import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by Administrator on 2016/3/30.
 */
public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        FlowManager.destroy();
    }
}
