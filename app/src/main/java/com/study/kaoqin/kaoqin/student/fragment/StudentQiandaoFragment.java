package com.study.kaoqin.kaoqin.student.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.runtime.transaction.process.ProcessModelInfo;
import com.raizlabs.android.dbflow.runtime.transaction.process.SaveModelTransaction;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.study.kaoqin.kaoqin.R;
import com.study.kaoqin.kaoqin.entity.StudentQian;
import com.study.kaoqin.kaoqin.student.StudentActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import qinshi.mylibrary.utils.LogUtils;
import qinshi.mylibrary.utils.Once;
import qinshi.mylibrary.utils.TimeUtils;
import qinshi.mylibrary.view.recyclerview.BaseAdapterHelper;
import qinshi.mylibrary.view.recyclerview.QuickAdapter;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StudentQiandaoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudentQiandaoFragment extends Fragment {


    @Bind(R.id.add_qiandao)
    FloatingActionButton mFloatingActionButton;
    @Bind(R.id.qiandao_list)
    RecyclerView mQiandaoList;

    Adapter mAdapter;
    private String tag = "isFirstLoad";



    @OnClick(R.id.add_qiandao)
    void add() {




        Intent mIntent = new Intent(getActivity(), StudentActivity.class);
        startActivityForResult(mIntent, 1);


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == AppCompatActivity.RESULT_OK) {

            StudentQian s=new StudentQian();
            s.setTime(TimeUtils.getTime());



        }

    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StudentQiandaoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StudentQiandaoFragment newInstance(String param1, String param2) {
        StudentQiandaoFragment fragment = new StudentQiandaoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        setHasOptionsMenu(true);
        //首次加载填充数据
        Once mOnce = new Once(getActivity());
        mOnce.show(tag, new Once.OnceCallback() {
            @Override
            public void onOnce() {
                List<StudentQian> list = new ArrayList<>();

                for (int i = 0; i < 4; i++) {
                    StudentQian q = new StudentQian();

                    q.setTime(qinshi.mylibrary.utils.TimeUtils.getTime());
                    q.setName("工程经济学");

                    list.add(q);

                }

                TransactionManager.getInstance().addTransaction(new SaveModelTransaction(ProcessModelInfo.withModels(list)));
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    List<StudentQian> lists = new ArrayList<>();
    Subscription mSubscription;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mQiandaoList.setLayoutManager(layoutManager);
        mAdapter = new Adapter(getActivity(), R.layout.item_student_lesson, lists);
        mQiandaoList.setAdapter(mAdapter);

        updateData();

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();

}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        mSubscription.unsubscribe();

    }

    public void updateData() {
        mSubscription = rx.Observable.create(new rx.Observable.OnSubscribe<List<StudentQian>>() {
            @Override
            public void call(Subscriber<? super List<StudentQian>> t) {


                lists.addAll(new Select().from(StudentQian.class).queryList());
                t.onNext(lists);
                t.onCompleted();

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<StudentQian>>() {
            @Override
            public void onCompleted() {
                LogUtils.i("complete");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<StudentQian> t) {
                LogUtils.d("length--->", lists.size() + "");

                mAdapter.notifyDataSetChanged();
            }
        });


    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onResume() {
        super.onResume();

//        Lesson q = new Lesson();
//        q.setCount(2);
//        q.setTime(qinshi.mylibrary.utils.TimeUtils.getTime());
//        q.setName("lalalal");
//        q.save();
        LogUtils.i("onResume");

    }

    @Override
    public void onPause() {
        super.onPause();

        LogUtils.i("onPause");
        //    observer.unregisterForContentChanges(getActivity());

    }








    public class Adapter extends QuickAdapter<StudentQian> {


        public Adapter(Context context, int layoutResId, List<StudentQian> data) {
            super(context, layoutResId, data);
        }

        @Override
        protected void convert(BaseAdapterHelper helper, StudentQian item) {
            helper.getTextView(R.id.avatar_tv).setText(item.getName().substring(0, 1));
            helper.getTextView(R.id.student_name).setText(item.getName());
            helper.getTextView(R.id.qiandao_time).setText("签到时间：" + item.getTime());
        }
    }


}
