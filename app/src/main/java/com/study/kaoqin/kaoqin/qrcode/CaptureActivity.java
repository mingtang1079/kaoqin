package com.study.kaoqin.kaoqin.qrcode;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.raizlabs.android.dbflow.sql.language.Select;
import com.study.kaoqin.kaoqin.R;
import com.study.kaoqin.kaoqin.base.BaseActivity;
import com.study.kaoqin.kaoqin.entity.Lesson;
import com.study.kaoqin.kaoqin.entity.LessonStudent;
import com.study.kaoqin.kaoqin.entity.Student;
import com.study.kaoqin.kaoqin.entity.Student_Table;
import com.study.kaoqin.kaoqin.qrcode.camera.CameraManager;
import com.study.kaoqin.kaoqin.qrcode.decode.DecodeUtils;
import com.study.kaoqin.kaoqin.qrcode.utils.BeepManager;
import com.study.kaoqin.kaoqin.qrcode.utils.InactivityTimer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import qinshi.mylibrary.utils.LogUtils;
import qinshi.mylibrary.utils.TimeUtils;

/**
 * This activity opens the camera and does the actual scanning on a background thread. It draws a
 * viewfinder to help the user place the barcode correctly, shows feedback as the image processing
 * is happening, and then overlays the results when a scan is successful.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */

public class CaptureActivity extends BaseActivity implements SurfaceHolder.Callback {


    public static final String COURSE_NAME = "course_name";

    private Lesson mLesson = new Lesson();
    private List<LessonStudent> mBridgeStudents = new ArrayList<>();
    private ArrayList<Student> mlist = new ArrayList<>();


    @Bind(R.id.capture_preview)
    SurfaceView capturePreview;
    @Bind(R.id.capture_error_mask)
    ImageView captureErrorMask;
    @Bind(R.id.capture_scan_mask)
    ImageView captureScanMask;
    @Bind(R.id.capture_crop_view)
    FrameLayout captureCropView;

    @Bind(R.id.capture_light_btn)
    Button captureLightBtn;

    @Bind(R.id.capture_container)
    RelativeLayout captureContainer;

    private CaptureActivityHandler handler;

    private boolean hasSurface;
    private boolean isLightOn;

    private InactivityTimer mInactivityTimer;
    private BeepManager mBeepManager;

    private int mQrcodeCropWidth = 0;
    private int mQrcodeCropHeight = 0;
    private int mBarcodeCropWidth = 0;
    private int mBarcodeCropHeight = 0;

    private ObjectAnimator mScanMaskObjectAnimator = null;

    private Rect cropRect;
    private int dataMode = DecodeUtils.DECODE_DATA_MODE_QRCODE;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        ButterKnife.bind(this);
        initViewsAndEvents();
    }

    protected void initViewsAndEvents() {
        //  mLesson.setLists(mlist);
        mLesson.setName(getIntent().getStringExtra(COURSE_NAME));
        mLesson.setTime(TimeUtils.getTime());
        mLesson.setCount(0);
        hasSurface = false;
        mInactivityTimer = new InactivityTimer(this);
        mBeepManager = new BeepManager(this);


        captureLightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLightOn) {
                    cameraManager.setTorch(false);
                    captureLightBtn.setSelected(false);
                } else {
                    cameraManager.setTorch(true);
                    captureLightBtn.setSelected(true);
                }
                isLightOn = !isLightOn;
            }
        });


        initCropViewAnimator();
    }


    private void initCropViewAnimator() {
        mQrcodeCropWidth = getResources().getDimensionPixelSize(R.dimen.qrcode_crop_width);
        mQrcodeCropHeight = getResources().getDimensionPixelSize(R.dimen.qrcode_crop_height);

        mBarcodeCropWidth = getResources().getDimensionPixelSize(R.dimen.barcode_crop_width);
        mBarcodeCropHeight = getResources().getDimensionPixelSize(R.dimen.barcode_crop_height);
    }


    public Handler getHandler() {
        return handler;
    }


    CameraManager cameraManager;


    public CameraManager getCameraManager() {

        return cameraManager;
    }


    public void initCrop() {
        int cameraWidth = cameraManager.getCameraResolution().y;
        int cameraHeight = cameraManager.getCameraResolution().x;

        int[] location = new int[2];
        captureCropView.getLocationInWindow(location);

        int cropLeft = location[0];
        int cropTop = location[1];

        int cropWidth = captureCropView.getWidth();
        int cropHeight = captureCropView.getHeight();

        int containerWidth = captureContainer.getWidth();
        int containerHeight = captureContainer.getHeight();

        int x = cropLeft * cameraWidth / containerWidth;
        int y = cropTop * cameraHeight / containerHeight;

        int width = cropWidth * cameraWidth / containerWidth;
        int height = cropHeight * cameraHeight / containerHeight;

        setCropRect(new Rect(x, y, width + x, height + y));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // CameraManager must be initialized here, not in onCreate(). This is necessary because we don't
        // want to open the camera driver and measure the screen size if we're going to show the help on
        // first launch. That led to bugs where the scanning rectangle was the wrong size and partially
        // off screen.
        cameraManager = new CameraManager(getApplication());

        // handler = new CaptureActivityHandler(this,cameraManager);

        if (hasSurface) {
            // The activity was paused but not stopped, so the surface still exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(capturePreview.getHolder());
        } else {
            // Install the callback and wait for surfaceCreated() to init the camera.
            capturePreview.getHolder().addCallback(this);
        }

        mInactivityTimer.onResume();
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }

        mBeepManager.close();
        mInactivityTimer.onPause();
        cameraManager.closeDriver();

        if (!hasSurface) {
            capturePreview.getHolder().removeCallback(this);
        }

        if (null != mScanMaskObjectAnimator && mScanMaskObjectAnimator.isStarted()) {
            mScanMaskObjectAnimator.cancel();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mInactivityTimer.shutdown();
        super.onDestroy();
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e("surface", "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!hasSurface) {
            hasSurface = true;
//            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        initCamera(holder);
    }

    /**
     * A valid barcode has been found, so give an indication of success and show the results.
     */
    //解码二维码
    public void handleDecode(String result, Bundle bundle) {


        LessonStudent mLessonStudent = new LessonStudent();

        System.out.print("xuehao--->" + getXuehao(result));

        if (numberOfStr(result, "&") != 2) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("该二维码无效");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.restartPreviewAndDecode();
                }
            });
            builder.show();
            return;
        }


        Student mStudent = null;



        try {
            mStudent = new Select().from(Student.class).where(Student_Table.xuehao.eq(Long.valueOf(getXuehao(result)))).querySingle();
        } catch (Exception e) {

        }

        if (mStudent != null) {
            mStudent.setCount(mStudent.getCount() + 1);
            LogUtils.i("有该同学的签到次数", mStudent.count + "");
            mStudent.update();
        } else {
            LogUtils.i("没有该同学，创建新同学");
            mStudent = new Student();
            mStudent.setName(getName(result));
            mStudent.setXuehao(Long.valueOf(getXuehao(result)));
            mStudent.setCount(1);
            mStudent.save();

        }

        mLesson.count++;

        mLessonStudent.setStudent(mStudent);
        mLessonStudent.setLesson(mLesson);
        // 签到表里的 集合
        mBridgeStudents.add(mLessonStudent);

        mlist.add(mStudent);

        //     mLesson.setCount(mLesson.getCount() + 1);


        //  mInactivityTimer.onActivity();


        //   mBeepManager.playBeepSoundAndVibrate();


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("学生姓名");
        builder.setMessage(getName(result) + " 签到成功");

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.restartPreviewAndDecode();
            }
        });
        builder.show();

        mLessonStudent.save();
    }

    public int numberOfStr(String str, String con) {
        str = " " + str;
        if (str.endsWith(con)) {
            return str.split(con).length;
        } else {
            return str.split(con).length - 1;
        }
    }


    public String getName(String s) {

        return s.substring(0, s.indexOf("&"));

    }

    public String getXuehao(String s) {

        int i = s.indexOf("&");
        int j = s.indexOf("&", i + 1);

        return s.substring(++i, j);
    }

    public String getTime(String s) {


        int i = s.lastIndexOf("&");
        return s.substring(++i);

    }

    private void onCameraPreviewSuccess() {
        initCrop();
        captureErrorMask.setVisibility(View.GONE);

//        ViewHelper.setPivotX(captureScanMask, 0.0f);
//        ViewHelper.setPivotY(captureScanMask, 0.0f);

        mScanMaskObjectAnimator = ObjectAnimator.ofFloat(captureScanMask, "scaleY", 0.0f, 1.0f);
        mScanMaskObjectAnimator.setDuration(2000);
        mScanMaskObjectAnimator.setInterpolator(new DecelerateInterpolator());
        mScanMaskObjectAnimator.setRepeatCount(-1);
        mScanMaskObjectAnimator.setRepeatMode(ObjectAnimator.RESTART);
        mScanMaskObjectAnimator.start();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {

            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a RuntimeException.
            if (handler == null) {
                handler = new CaptureActivityHandler(this, cameraManager);
            }

            onCameraPreviewSuccess();
        } catch (IOException ioe) {

            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service

            displayFrameworkBugMessageAndExit();
        }

    }

    private void displayFrameworkBugMessageAndExit() {
        captureErrorMask.setVisibility(View.VISIBLE);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("打开相机失败");

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (mLesson.getCount() != 0) {

        }
//        Log.i("log", "发送广播");
//        Intent startIntent = new Intent();
//        startIntent.setAction(TeacherActivity.UPDATE_DATA);
//    //    startIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
//        sendBroadcast(startIntent);

    }

    public Rect getCropRect() {
        return cropRect;
    }

    public void setCropRect(Rect cropRect) {
        this.cropRect = cropRect;
    }

    public int getDataMode() {
        return dataMode;
    }

    public void setDataMode(int dataMode) {
        this.dataMode = dataMode;
    }


}
