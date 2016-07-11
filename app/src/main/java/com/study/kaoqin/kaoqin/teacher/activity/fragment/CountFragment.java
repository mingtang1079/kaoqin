package com.study.kaoqin.kaoqin.teacher.activity.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.runtime.transaction.process.ProcessModelInfo;
import com.raizlabs.android.dbflow.runtime.transaction.process.SaveModelTransaction;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.study.kaoqin.kaoqin.R;
import com.study.kaoqin.kaoqin.adapter.StudentCountAdapter;
import com.study.kaoqin.kaoqin.entity.Student;
import com.study.kaoqin.kaoqin.entity.Student_Table;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import qinshi.mylibrary.utils.LogUtils;
import qinshi.mylibrary.utils.Once;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CountFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @Bind(R.id.list)
    RecyclerView mList;
    StudentCountAdapter mCountAdapter;
    private String tag = "lalala";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private List<Student> students = new ArrayList<>();

    public CountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CountFragment newInstance(String param1, String param2) {
        CountFragment fragment = new CountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mList.setLayoutManager(layoutManager);
        mCountAdapter = new StudentCountAdapter(getActivity(), R.layout.item_student_qiandao, students);
        mList.setAdapter(mCountAdapter);


    }

    @Override
    public void onResume() {
        super.onResume();

        getData(0);
    }

    Subscription mSubscription;

    //0为正常排序，1为升序，2为降序
    public void getData(final int i) {


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

                students.clear();
                students.addAll(t);
                mCountAdapter.notifyDataSetChanged();
            }
        });


//        mSubscription = rx.Observable.create(new rx.Observable.OnSubscribe<List<Student>>() {
//            @Override
//            public void call(Subscriber<? super List<Student>> t) {
//                students.addAll(new Select().from(Student.class).queryList());
//                LogUtils.i("countlength-----------");
//                t.onNext(students);
//                t.onCompleted();
//            }
//        })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<List<Student>>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onNext(List<Student> t) {
//
//                        LogUtils.i("countlength"+t.size());
//
//                        mCountAdapter.notifyDataSetChanged();
//
//                    }
//                });
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
                List<Student> list = new ArrayList<>();
                Student s = new Student();
                s.setName("周杰伦");
                s.setCount(20);
                s.setXuehao(20001212);

                list.add(s);

                Student s1 = new Student();
                s1.setName("蔡依林");
                s1.setCount(22);
                s1.setXuehao(20001213);
                list.add(s1);

                Student s2 = new Student();
                s2.setName("昆凌");
                s2.setCount(24);
                s2.setXuehao(20001214);
                list.add(s2);

                LogUtils.i("数据保存成功");

                TransactionManager.getInstance().addTransaction(new SaveModelTransaction(ProcessModelInfo.withModels(list)));
            }
        });


    }


    @Override
    public void onPause() {
        super.onPause();
        //    mSubscription.unsubscribe();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_count, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_count, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.sort_sheng) {

            sheng();
        } else {
            jiang();

        }

        return super.onOptionsItemSelected(item);
    }

    private void jiang() {


        getData(2);
    }


    private void sheng() {
        getData(1);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


}
