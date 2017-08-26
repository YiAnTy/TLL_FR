package bjtu.makeupapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "MainActivity";

    private FrameLayout cameraPreview;

    private android.hardware.Camera mCamera;
    private CameraPreview mCameraPreview;
    private int numOfCamera;
    private int cameraCurrentlyLocked;

    // The first rear facing camera
    private int defaultCameraId;

    private int screenWidth, screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate");
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // 得到屏幕的大小
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        screenHeight = display.getHeight();
        screenWidth = display.getWidth();

        cameraPreview = (FrameLayout) findViewById(R.id.camera_preview);
        mCameraPreview = (CameraPreview) findViewById(R.id.my_camera_preview);

        // 得到默认的相机ID
        defaultCameraId = getDefaultCameraId();
        cameraCurrentlyLocked = defaultCameraId;


    }

    /**
     * 得到默认相机的ID
     *
     * @return
     */
    private int getDefaultCameraId() {
        Log.d(LOG_TAG, "getDefaultCameraId");

        int defaultId = -1;

        // Find the total number of cameras available
        numOfCamera = Camera.getNumberOfCameras();

        // Find the ID of the default camera
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numOfCamera; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            Log.d(LOG_TAG, "camera info: " + cameraInfo.orientation);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
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
        mCamera = getCameraInstance(cameraCurrentlyLocked);

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
     * Check if this device has a camera
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
}
