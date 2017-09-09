package bjtu.makeupapp.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.luxand.FSDK;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bjtu.makeupapp.adapter.StyleAdapter;
import bjtu.makeupapp.components.CameraPreview;
import bjtu.makeupapp.components.ProcessImageAndDrawResults;
import bjtu.makeupapp.model.StyleItem;

public class MainActivity extends AppCompatActivity{

    public static boolean sIsShowingProcessedFrameOnly = true;
    public static boolean sIsUsingRenderScript = true;
    public static boolean sIsRotatingWithRenderScript = true && sIsUsingRenderScript;

    public static final String LOG_TAG = "MainActivity";

    private FrameLayout cameraPreview;
    private ImageView img_camera_side;
    private ImageView cancel;
    private ImageView sure;
    private TextView name;

    private Camera mCamera;
    private CameraPreview mCameraPreview;
    private ProcessImageAndDrawResults mDraw;
    private boolean mIsFailed = false;

    private List<StyleItem> styleItems = new ArrayList<>();



    public static int currentCameraId;
    public static int cameraRotation;

    public static float sDensity = 1.0f;

    private static final int CAMERA_FRONT=Camera.CameraInfo.CAMERA_FACING_FRONT;
    private static final int CAMERA_BACK=Camera.CameraInfo.CAMERA_FACING_BACK;

    private static final String KeyOfFSDK="d9GsVchpIt49Mr/Euq1tUtor0Zn4bR6uPmv+hc0X2cOnhGfZzEAQcMgMZK/UJnstf+3rRBCF2URjLaY" +
            "vvu7FSMHe2makJVmB6+P5FA3sIVEhRvoibCqSN8IOHrOeDyYsDctxXi/ShcXAs6ErfTEVsTMiHsdDgphn/xmKdhLP/kw=";

    public void showErrorAndClose(String error, int code) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(error + ": " + code)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        //android.os.Process.killProcess(android.os.Process.myPid());
                    }
                })
                .show();
    }

    public void showMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setCancelable(false) // cancel with button only
                .show();
    }

    private void resetTrackerParameters() {
        int errpos[] = new int[1];
        FSDK.SetTrackerMultipleParameters(mDraw.mTracker, "ContinuousVideoFeed=true;FacialFeatureJitterSuppression=0;RecognitionPrecision=1;Threshold=0.996;Threshold2=0.9995;ThresholdFeed=0.97;MemoryLimit=2000;HandleArbitraryRotations=false;DetermineFaceRotationAngle=false;InternalResizeWidth=70;FaceDetectionThreshold=3;", errpos);
        if (errpos[0] != 0) {
            showErrorAndClose("Error setting tracker parameters, position", errpos[0]);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate");
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        cameraPreview = (FrameLayout) findViewById(R.id.camera_preview);
        img_camera_side= (ImageView) findViewById(R.id.img_camera_side);

        currentCameraId=getDefaultCameraId();
        cameraRotation=getWindowManager().getDefaultDisplay().getRotation();
        sDensity = getResources().getDisplayMetrics().scaledDensity;

        int res = FSDK.ActivateLibrary(KeyOfFSDK);
        if (res != FSDK.FSDKE_OK) {
            mIsFailed = true;
            showErrorAndClose("FaceSDK activation failed", res);
        } else {
            FSDK.Initialize();

            // Lock orientation
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            // Camera layer and drawing layer
            mDraw = new ProcessImageAndDrawResults(getBaseContext());
            mCameraPreview=new CameraPreview(this,mDraw);
            mDraw.mTracker = new FSDK.HTracker();
            res = FSDK.CreateTracker(mDraw.mTracker);
            if (FSDK.FSDKE_OK != res) {
                showErrorAndClose("Error creating tracker", res);
            }
            resetTrackerParameters();


//        FrameLayout.LayoutParams lp_framelayout_camprev=new FrameLayout.LayoutParams
//                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        lp_framelayout_camprev.gravity=Gravity.CENTER;
//        mCameraPreview.setLayoutParams(lp_framelayout_camprev);
//
            cameraPreview.addView(mCameraPreview);
            cameraPreview.addView(mDraw,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        }

        //妆容选择初始化
        initStyle();
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        StyleAdapter styleAdapter = new StyleAdapter(styleItems);
        recyclerView.setAdapter(styleAdapter);

        //底部样式初始化
        cancel = (ImageView)findViewById(R.id.img_cancel);
        sure = (ImageView)findViewById(R.id.img_sure);
        name = (TextView)findViewById(R.id.style_name);

        //取消注册，返回素颜监听
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 点叉查看素颜
                Toast.makeText(getBaseContext(),"查看素颜",Toast.LENGTH_SHORT).show();
            }
        });

        //确认该妆容监听
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 选择该妆容
                Toast.makeText(getBaseContext(),"给出该妆容对应化妆步骤",Toast.LENGTH_SHORT).show();

            }
        });

        // 注册图片按钮（切换摄像头）监听
        img_camera_side.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getBaseContext(),"切换摄像头",Toast.LENGTH_SHORT).show();
                try {
                    changeCameraSide();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 得到默认相机的ID
     *
     * @return
     */
    private int getDefaultCameraId() {
        Log.d(LOG_TAG, "getDefaultCameraId");

        int defaultId = -1;
        int numOfCamera;

        // Find the total number of cameras available
        numOfCamera = Camera.getNumberOfCameras();

        // Find the ID of the default camera
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numOfCamera; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            Log.d(LOG_TAG, "camera info: " + cameraInfo.orientation);
            if (cameraInfo.facing == CAMERA_FRONT) {
                defaultId = i;
            }
        }

        if (-1 == defaultId) {
            if (numOfCamera > 0) {
                // 如果没有后向摄像头
                defaultId = 0;
            } else {
                // 没有摄像头
                Toast.makeText(getBaseContext(), "没有摄像头", Toast.LENGTH_SHORT).show();
            }
        }

        return defaultId;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");

        // Open the default i.e. the first rear facing camera.
        mCamera = getCameraInstance(currentCameraId);

        mCameraPreview.setCamera(mCamera);

        if (mIsFailed)
            return;
        resumeProcessingFrames();
    }

    /**
     * A safe way to get an instance of the Camera object.
     *
     * @param cameraId
     * @return
     */
    public static Camera getCameraInstance(int cameraId) {
        Log.d(LOG_TAG, "getCameraInstance");

        Camera cam = null;
        try {
            cam = Camera.open(cameraId);// attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            e.printStackTrace();
            Log.e(LOG_TAG, "Camera is not available");
        }

        currentCameraId=cameraId;

        return cam; // returns null if camera is unavailable
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause");

        // Because the Camera object is a shared resource, it's very
        // important to release it when the activity is paused.
        if (mCamera != null) {
            mCameraPreview.setCamera(null);
            Log.d(LOG_TAG, "onPause --> Realease camera");
            mCamera.release();
            mCamera = null;
        }

        if (mIsFailed)
            return;
        pauseProcessingFrames();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    private void pauseProcessingFrames() {
        mDraw.mStopping = 1;

        // It is essential to limit wait time, because stopped will not be set to 0, if no frames are feeded to mDraw
        for (int i = 0; i < 100; ++i) {
            if (mDraw.mStopped != 0)
                break;
            try {
                Thread.sleep(10);
            } catch (Exception ex) {
            }
        }
    }

    private void resumeProcessingFrames() {
        mDraw.mStopped = 0;
        mDraw.mStopping = 0;
    }

    /**
     * If your application does not specifically require a camera using a manifest declaration,
     * you should check to see if a camera is available at runtime.
     *
     * @param context
     * @return
     */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    private void initStyle(){
        for(int i = 0;i<2;i++){
            StyleItem s1 = new StyleItem(R.drawable.style_one,"烟熏妆");
            styleItems.add(s1);
            StyleItem s2 = new StyleItem(R.drawable.style_two,"橘系妆容");
            styleItems.add(s2);
            StyleItem s3 = new StyleItem(R.drawable.style_three,"御姐妆");
            styleItems.add(s3);
        }
    }

    private void changeCameraSide() throws IOException {
        mCamera.stopPreview();
        mCamera.release();

        if(currentCameraId == CAMERA_FRONT){
            mCamera = getCameraInstance(CAMERA_BACK);
        }else if(currentCameraId == CAMERA_BACK){
            mCamera = getCameraInstance(CAMERA_FRONT);
        }

        mCameraPreview.setCamera(mCamera);
    }

    public TextView getName(){
        return name;
    }



}



