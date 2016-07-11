package com.study.kaoqin.kaoqin.entity;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.study.kaoqin.kaoqin.db.AppDatabase;

/**
 * Created by Administrator on 2016/4/1.
 */
@Table(name = "Student", database = AppDatabase.class)
@ModelContainer
public class Student extends BaseModel {

    @Column
    public String name;

    //签到次数
    @Column
    public int count;

    //学号
    @Column
    public long xuehao;

    @Column
    @PrimaryKey(autoincrement = true)
    public long id;

    public int getCount() {
        return count;
    }

    public void setCount(int mCount) {
        count = mCount;
    }

    public Student() {

    }

    public Student(String mName, int mXuehao) {
        name = mName;
        xuehao = mXuehao;
    }

    public String getName() {
        return name;
    }

    public void setName(String mName) {
        name = mName;
    }

    public long getXuehao() {
        return xuehao;
    }

    public void setXuehao(long mXuehao) {
        xuehao = mXuehao;
    }
}
