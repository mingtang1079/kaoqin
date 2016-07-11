package com.study.kaoqin.kaoqin.entity;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.study.kaoqin.kaoqin.db.AppDatabase;

/**
 * Created by Administrator on 2016/5/24.
 */
@Deprecated
@Table(database = AppDatabase.class)
public class StudentQian extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    public long id;

    //学生一切签到的课程名
    @Column
    public String name;


    @Column
    public String time;

    public StudentQian() {
    }

    public StudentQian(long mId, String mName, String mTime) {
        id = mId;
        name = mName;
        time = mTime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String mTime) {
        time = mTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String mName) {
        name = mName;
    }
}
