package com.study.kaoqin.kaoqin.teacher.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.raizlabs.android.dbflow.sql.language.Select;
import com.study.kaoqin.kaoqin.R;
import com.study.kaoqin.kaoqin.adapter.StudentCountAdapter;
import com.study.kaoqin.kaoqin.entity.LessonStudent;
import com.study.kaoqin.kaoqin.entity.LessonStudent_Table;
import com.study.kaoqin.kaoqin.entity.Lesson_Student_Table;
import com.study.kaoqin.kaoqin.entity.Student;
import com.study.kaoqin.kaoqin.entity.Student_Table;
import com.study.kaoqin.kaoqin.teacher.activity.fragment.MainFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import qinshi.mylibrary.utils.LogUtils;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CourseDetailActivity extends AppCompatActivity {

    @Bind(R.id.course_detail_list)
    RecyclerView mList;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    public ActionBar mActionBar;

    long id;
    //签到课的id
    StudentCountAdapter mCountAdapter;
    private List<Student> mStudentList = new ArrayList<>();


    private List<Student> mAllStudents = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        ButterKnife.bind(this);
        mToolbar.setTitle("详细信息");

        setSupportActionBar(mToolbar);
        mActionBar = this.getSupportActionBar();
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);

        id = getIntent().getLongExtra(MainFragment.STUDENT_TAG, 0);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mList.setLayoutManager(layoutManager);
        mCountAdapter = new StudentCountAdapter(this, R.layout.item_student_qiandao, mStudentList);
        mList.setAdapter(mCountAdapter);
        getData();
        getAllStudents(0);
    }

    private void getData() {


        mStudentList.clear();

        List<LessonStudent> list = new Select().from(LessonStudent.class).queryList();

        List<LessonStudent> lists = new Select().from(LessonStudent.class).where(LessonStudent_Table.mLesson_id.eq(id)).queryList();

        LogUtils.i("list1---->" + lists.size());

        for (LessonStudent s : lists) {

            mStudentList.add(s.getStudent());
        }

        mCountAdapter.notifyDataSetChanged();

    }

    public static void launch(Context mContext) {

        Intent i = new Intent(mContext, CourseDetailActivity.class);
        mContext.startActivity(i);

    }

    public static void launch(Context mContext, Bundle mBundle) {

        Intent i = new Intent(mContext, CourseDetailActivity.class);
        i.putExtras(mBundle);
        mContext.startActivity(i);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_detail, menu);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.sort_nodao:

                getNodaoStudent();
                break;


            case R.id.sort_all_stu:

                getData();

                break;
            default:
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    private void getNodaoStudent() {

        List<Student> nodaoList = getDiffrent4(mAllStudents, mStudentList);
        mStudentList.clear();
        mStudentList.addAll(nodaoList);
        mCountAdapter.notifyDataSetChanged();

    }

    Subscription mSubscription;

    public void getAllStudents(final int i) {


        mSubscription = rx.Observable.create(new rx.Observable.OnSubscribe<List<Student>>() {
            @Override
            public void call(Subscriber<? super List<Student>> t) {

                LogUtils.i("call");
                List<Student> list = null;
                if (i == 0) {
                    list = new Select().from(Student.class).queryList();
                } else if (i == 1) {
                    list = new Select().from(Student.class).orderBy(Student_Table.count, true).queryList();
                } else
                    list = new Select().from(Student.class).orderBy(Student_Table.count, false).queryList();
                LogUtils.i("listSize", list.size() + "");
                t.onNext(list);
                t.onCompleted();

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Student>>() {
            @Override
            public void onCompleted() {
                LogUtils.i("complete");
            }

            @Override
            public void onError(Throwable e) {
                LogUtils.i("error--->", e.getMessage().toString());
            }

            @Override
            public void onNext(List<Student> t) {

                mAllStudents.clear();
                mAllStudents.addAll(t);

            }
        });
    }


    public List<Student> getDiffrent4(List<Student> all, List<Student> now) {

        List<Student> nodaoList = new ArrayList<>();
        nodaoList.addAll(mAllStudents);
//
//        LogUtils.i("all--->" + all.size());
//        LogUtils.i("now---->" + now.size());

        for (Student s : now) {

            for (int i=0;i< nodaoList.size();i++)
            {

                if (s.name.equals(all.get(i).name)) {
                    nodaoList.remove(i);
                          i--;
                }

            }
        }

        LogUtils.i("nodao-->" + nodaoList.size());

        return nodaoList;

    }


}
