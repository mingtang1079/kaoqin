package com.study.kaoqin.kaoqin.teacher.activity.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.raizlabs.android.dbflow.runtime.FlowContentObserver;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.runtime.transaction.process.ProcessModelInfo;
import com.raizlabs.android.dbflow.runtime.transaction.process.SaveModelTransaction;
import com.raizlabs.android.dbflow.sql.language.SQLCondition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.Model;
import com.study.kaoqin.kaoqin.R;
import com.study.kaoqin.kaoqin.entity.Lesson;
import com.study.kaoqin.kaoqin.qrcode.CaptureActivity;
import com.study.kaoqin.kaoqin.teacher.activity.CourseDetailActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import qinshi.mylibrary.utils.LogUtils;
import qinshi.mylibrary.utils.Once;
import qinshi.mylibrary.utils.ToastUtils;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    FlowContentObserver observer = new FlowContentObserver();
    @Bind(R.id.add_qiandao)
    FloatingActionButton mFloatingActionButton;
    @Bind(R.id.qiandao_list)
    RecyclerView mQiandaoList;

    mainAdapter mAdapter;
    private String tag = "isFirstLoad";
    public static String STUDENT_TAG ="student_list";


    @OnClick(R.id.add_qiandao)
    void add() {


        View view = LayoutInflater.from(getActivity()).inflate(R.layout.new_course, null);
        final EditText e = (EditText) view.findViewById(R.id.input);
        new AlertDialog.Builder(getActivity()) //

                .setTitle("请输入课程名")
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!TextUtils.isEmpty(e.getText().toString())) {
                            Intent mIntent = new Intent(getActivity(), CaptureActivity.class);
                            mIntent.putExtra(CaptureActivity.COURSE_NAME, e.getText().toString());
                            getActivity().startActivity(mIntent);
                        } else
                            ToastUtils.show(getActivity(), "请输入课程名哟", Toast.LENGTH_SHORT);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                    }
                })
                .create().show();


    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MainFragment() {
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
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
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
                List<Lesson> list = new ArrayList<>();

                for (int i = 0; i < 4; i++) {
                    Lesson q = new Lesson();
                    q.setCount(i);
                    q.setTime(qinshi.mylibrary.utils.TimeUtils.getTime());
                    q.setName("工程经济学");

                    list.add(q);

                }

                TransactionManager.getInstance().addTransaction(new SaveModelTransaction(ProcessModelInfo.withModels(list)));
            }
        });


        observer.addModelChangeListener(new FlowContentObserver.OnModelStateChangedListener() {
            @Override
            public void onModelStateChanged(@Nullable Class<? extends Model> table, BaseModel.Action action, @NonNull SQLCondition[] primaryKeyValues) {

                int i = primaryKeyValues.length;
                for (int j = 0; j < i; j++) {
                    LogUtils.i("监听结果--->", action.name() + primaryKeyValues[j].columnName() + ":" + primaryKeyValues[j].value());

                }


            }
        });

        observer.registerForContentChanges(getActivity(), Lesson.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    List<Lesson> lists = new ArrayList<>();
    Subscription mSubscription;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mQiandaoList.setLayoutManager(layoutManager);
        mAdapter = new mainAdapter(lists);
        mQiandaoList.setAdapter(mAdapter);



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
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        mSubscription.unsubscribe();
        observer.unregisterForContentChanges(getActivity());
    }

    public void updateData() {
        mSubscription = rx.Observable.create(new rx.Observable.OnSubscribe<List<Lesson>>() {
            @Override
            public void call(Subscriber<? super List<Lesson>> t) {



                t.onNext(new Select().from(Lesson.class).queryList());
                t.onCompleted();

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Lesson>>() {
            @Override
            public void onCompleted() {
                LogUtils.i("complete");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<Lesson> t) {

                lists.clear();
                lists.addAll(t);

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
        updateData();

    }

    @Override
    public void onPause() {
        super.onPause();

        LogUtils.i("onPause");
        //    observer.unregisterForContentChanges(getActivity());

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();
        if (id == R.id.about) {


            new AlertDialog.Builder(getActivity()) //

                    .setTitle("请输入课程名")
                    .setMessage("教师考勤 1.0")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })

                    .create().show();


        } else if(item.getItemId()==R.id.output)
        {
            ToastUtils.showShort(getActivity(),"功能调试中");
        }

        return super.onOptionsItemSelected(item);
    }
//修改课程名

    private void alterName(final int position) {

        final Lesson mLesson =lists.get(position);



        View view = LayoutInflater.from(getActivity()).inflate(R.layout.new_course, null);
        final EditText e = (EditText) view.findViewById(R.id.input);
        new AlertDialog.Builder(getActivity()) //

                .setTitle("请输入课程名")
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!TextUtils.isEmpty(e.getText().toString())) {
                         mLesson.setName(e.getText().toString());
                            mLesson.update();
                            mAdapter.notifyItemChanged(position);

                        } else
                            ToastUtils.show(getActivity(), "请输入课程名哟", Toast.LENGTH_SHORT);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                    }
                })
                .create().show();





    }

    private void lookAllStudent(int location) {

        Bundle mBundle=new Bundle();
        mBundle.putLong(STUDENT_TAG,lists.get(location).id);
        CourseDetailActivity.launch(getActivity(),mBundle);

    }



    //adatper
    public class mainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


        List<Lesson> lists;


        public void addLists(List<Lesson> lists) {
            lists.addAll(lists);
        }

        public mainAdapter(List<Lesson> lists) {
            this.lists = lists;
        }

        mainAdapter() {


        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lesson, null);

            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            MyViewHolder mMyViewHolder = (MyViewHolder) holder;

            mMyViewHolder.mLessonName.setText(lists.get(position).getName());
            mMyViewHolder.mMainItemTime.setText(lists.get(position).getTime());
            mMyViewHolder.mQiandaoCount.setText("本次签到人数：" + lists.get(position).getCount() + "人");
            mMyViewHolder.mAvatar_tv.setText(lists.get(position).getName().substring(0, 1));

            mMyViewHolder.mMainItemXuanxiang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            mMyViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    new AlertDialog.Builder(getActivity()) //

                            .setItems(R.array.teacher, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    switch (which) {
                                        case 0:
                                            alterName(position);
                                            break;
                                        case 1:
                                            lookAllStudent(position);
                                            break;


                                    }


                                }


                            }).create().show();


                }


            });


        }

        @Override
        public int getItemCount() {
            return lists.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.student_name)
            TextView mLessonName;
            @Bind(R.id.qiandao_count)
            TextView mQiandaoCount;
            @Bind(R.id.main_item_xuanxiang)
            ImageView mMainItemXuanxiang;
            @Bind(R.id.main_item_time)
            TextView mMainItemTime;
            @Bind(R.id.avatar_tv)
            TextView mAvatar_tv;


            public MyViewHolder(View view) {


                super(view);
                ButterKnife.bind(this, view);

            }
        }


    }


}
