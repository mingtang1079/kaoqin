package com.study.kaoqin.kaoqin.entity;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.ManyToMany;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.sql.language.ITypeConditional;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Where;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.container.ForeignKeyContainer;
import com.study.kaoqin.kaoqin.db.AppDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/23.
 */
@Table(database = AppDatabase.class)
@ManyToMany(referencedTable = Student.class)
public class Lesson extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    public long id;

    @Column
    public String name;
    @Column
    public int count;
    @Column
    public String time;

//    @Column
//    @ForeignKey(saveForeignKeyModel = true)
//    ForeignKeyContainer<LessonStudent> mQianBridgeStudentForeignKeyContainer;
//
//
//    public void associateBridge(LessonStudent mQianBridgeStudent) {
//        mQianBridgeStudentForeignKeyContainer = FlowManager.getContainerAdapter(LessonStudent.class).toForeignKeyContainer(mQianBridgeStudent);
//
//
//    }

//
//    public List<LessonStudent> mBridgeStudents;
//
//    @OneToMany(methods = {OneToMany.Method.SAVE, OneToMany.Method.DELETE}, variableName = "mBridgeStudents")
//    public List<LessonStudent> getBride() {
//        if (mBridgeStudents == null) {
//            mBridgeStudents = new Select()
//                    .from(LessonStudent.class)
//                    .where(QianBridgeStudent_Table.id.eq(id)).queryList();
//
//        }
//        return mBridgeStudents;
//    }
//
//
    public List<Student> lists;


    public Lesson() {

    }


    public Lesson(String mName, String mTime, int mCount, ArrayList<Student> list) {
        this.lists = list;
        name = mName;
        time = mTime;
        count = mCount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int mCount) {
        count = mCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String mName) {
        name = mName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String mTime) {
        time = mTime;
    }


    public List<Student> getStudents() {
//        if (lists == null) {
//            lists = new Select()
//                    .from(Student.class).innerJoin(Lesson.class).on(QianBridgeStudent_Table.mQiandaoForeignKeyContainer_id.eq(id)).innerJoin(Student.class)
//                    .on(QianBridgeStudent_Table.mStudentForeignKeyContainer_xuehao.eq(Student_Table.xuehao)).queryList();
//        }
        return lists;
    }


}
