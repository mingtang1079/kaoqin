package com.study.kaoqin.kaoqin.teacher.activity.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.Select;
import com.study.kaoqin.kaoqin.R;
import com.study.kaoqin.kaoqin.adapter.StudentCountAdapter;
import com.study.kaoqin.kaoqin.entity.Student;
import com.study.kaoqin.kaoqin.entity.Student_Table;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import qinshi.mylibrary.utils.LogUtils;
import qinshi.mylibrary.utils.ToastUtils;
import qinshi.mylibrary.view.recyclerview.BaseAdapterHelper;
import qinshi.mylibrary.view.recyclerview.BaseQuickAdapter;
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
 * Use the {@link AddStudentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddStudentFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @Bind(R.id.student_list)
    RecyclerView mStudentList;
    @Bind(R.id.add_student)
    FloatingActionButton mAddStudent;

    StudentAdapter mCountAdapter;
    private List<Student> students = new ArrayList<>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AddStudentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddStudentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddStudentFragment newInstance(String param1, String param2) {
        AddStudentFragment fragment = new AddStudentFragment();
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
        mCountAdapter = new StudentAdapter(getActivity(), R.layout.item_student, students);
        mCountAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {


                new AlertDialog.Builder(getActivity()) //

                        .setTitle("提示")
                        .setMessage("删除该学生？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(@NonNull DialogInterface dialog, int which) {
                            }
                        })
                        .create().show();


            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        getData(0);
    }

    Subscription mSubscription;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_student, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mStudentList.setLayoutManager(layoutManager);
        mStudentList.setAdapter(mCountAdapter);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.add_student)
    public void onClick() {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_add_student, null);
        final EditText xuehao = (EditText) view.findViewById(R.id.xuehao);
        final EditText name = (EditText) view.findViewById(R.id.name);
        new AlertDialog.Builder(getActivity()) //

                .setTitle("添加学生")
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!TextUtils.isEmpty(xuehao.getText().toString()) && !TextUtils.isEmpty(name.getText().toString())) {

                            Student s = new Student();
                            s.setName(name.getText().toString());
                            s.setXuehao(Long.valueOf(xuehao.getText().toString()));
                            s.setCount(0);
                            s.save();

                            ToastUtils.showShort(getActivity(), "添加学生成功");

                            getData(0);

                        } else
                            ToastUtils.show(getActivity(), "请把信息填完整", Toast.LENGTH_SHORT);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                    }
                })
                .create().show();


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
        void onFragmentInteraction(Uri uri);
    }


    public static class StudentAdapter extends QuickAdapter<Student> {


        public StudentAdapter(Context context, int layoutResId, List<Student> data) {
            super(context, layoutResId, data);
        }

        @Override
        protected void convert(BaseAdapterHelper helper, Student item) {
            helper.getTextView(R.id.avatar_tv).setText(item.getName().substring(0, 1));
            helper.getTextView(R.id.student_name).setText(item.getName());
            helper.getTextView(R.id.student_xuehao).setText("学号：" + item.getXuehao());


        }
    }
}
