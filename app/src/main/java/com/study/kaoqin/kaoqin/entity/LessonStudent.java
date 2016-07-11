package com.study.kaoqin.kaoqin.entity;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.study.kaoqin.kaoqin.db.AppDatabase;

/**
 * Created by Administrator on 2016/4/18.
 */

@Table(database = AppDatabase.class)
public class LessonStudent extends BaseModel {


    @Column
    @PrimaryKey(autoincrement = true)
    public long id;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    Student mStudent;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    Lesson mLesson;

//    /**
//     * Example of setting the model for the sudent.
//     */
//    public void associateStudent(Student m) {
//        mStudentForeignKeyContainer = FlowManager.getContainerAdapter(Student.class).toForeignKeyContainer(m);
//    }
//
//    public void associateQiandao(Lesson mLesson) {
//
//        mQiandaoForeignKeyContainer = FlowManager.getContainerAdapter(Lesson.class).toForeignKeyContainer(mLesson);
//    }


    public Student getStudent() {
        return mStudent;
    }

    public void setStudent(Student mStudent) {
        this.mStudent = mStudent;
    }

    public Lesson getLesson() {
        return mLesson;
    }

    public void setLesson(Lesson mLesson) {
        this.mLesson = mLesson;
    }
}
