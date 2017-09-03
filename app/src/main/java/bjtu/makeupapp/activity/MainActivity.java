package bjtu.makeupapp.activity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.CursorIndexOutOfBoundsException;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bjtu.makeupapp.components.CameraPreview;
import bjtu.makeupapp.adapter.StyleAdapter;
import bjtu.makeupapp.manager.StyleManager;
import bjtu.makeupapp.model.StyleItem;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements StyleManager.TextListener {

    public static final String LOG_TAG = "MainActivity";

    private FrameLayout cameraPreview;
    private ImageView img_camera_side;
    private ImageView cancel;
    private ImageView sure;
    private TextView name;

    private Camera mCamera;
    private CameraPreview mCameraPreview;

    private List<StyleItem> styleItems = new ArrayList<>();

    public static int currentCameraId;
    public static int cameraRotation;

    private static final int CAMERA_FRONT=Camera.CameraInfo.CAMERA_FACING_FRONT;
    private static final int CAMERA_BACK=Camera.CameraInfo.CAMERA_FACING_BACK;


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
        mCameraPreview=new CameraPreview(this);
//        FrameLayout.LayoutParams lp_framelayout_camprev=new FrameLayout.LayoutParams
//                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        lp_framelayout_camprev.gravity=Gravity.CENTER;
//        mCameraPreview.setLayoutParams(lp_framelayout_camprev);
//
        cameraPreview.addView(mCameraPreview);

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

        //妆容名称更替,文本内容监听
        StyleManager.Operater op = new StyleManager.Operater();

        op.setTextListener(MainActivity.this);

        op.doSomething();


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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
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
    @Override
    public void updateText() {
        name.setText(StyleManager.getInstance().getStyleItems().getName());
    }
}
