package bjtu.makeupapp.components;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.hardware.Camera.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.IOException;
import java.util.List;

import bjtu.makeupapp.activity.MainActivity;

/**
 * Created by lenovo on 2017/8/26.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    public static final String LOG_TAG = "CameraPreview";

    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;

    Size mPreviewSize;
    List<Camera.Size> mSupportedPreviewSizes;

    public CameraPreview(Context context) {
        super(context);
        init();
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera=camera;
        init();
    }

    /**
     * 初始化
     */
    private void init() {

        Log.d(LOG_TAG, "CameraPreview initialize");

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        Log.d(LOG_TAG, "surfaceCreated");

        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(LOG_TAG, "Error setting camera preview: " + e.getMessage());
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

//        Take care of releasing the Camera preview in your activity.

        Log.d(LOG_TAG, "surfaceDestroyed");

        if (null != mCamera) {
            mCamera.stopPreview();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

        Log.d(LOG_TAG, "surface changed");

        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (surfaceHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
            Log.d(LOG_TAG, "tried to stop a non-existent preview: " + e.getMessage());
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        if (mCamera!=null) {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);

            requestLayout();

            mCamera.setParameters(parameters);
            setCameraDisplayOrientation(MainActivity.cameraRotation,MainActivity.currentCameraId,mCamera);

            Log.d(LOG_TAG, "camera set parameters successfully!: "
                    + parameters);
        }

        // start preview with new settings
        try {
            if (null != mCamera) {

                mCamera.setPreviewDisplay(surfaceHolder);
                mCamera.startPreview();
            }

        } catch (Exception e) {
            Log.d(LOG_TAG,
                    "Error starting camera preview: " + e.getMessage());
        }
    }

    public void setCamera(Camera camera) {

        mCamera = camera;
        if (mCamera != null) {
            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // We purposely disregard child measurements because act as a
        // wrapper to a SurfaceView that centers the camera preview instead
        // of stretching it.

        final int width = resolveSize(getSuggestedMinimumWidth(),
                widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(),
                heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, Math.max(width,height),
                    Math.min(width,height));
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int curOrientation =
                getContext().getResources().getConfiguration().orientation;
        if (changed ) {
//            && this.getChildCount() > 0 || mLastOrientation != curOrientation
//            mLastOrientation = curOrientation;
//            final View child = getChildAt(0);
            final int width = right - left;
            final int height = bottom - top;
            int previewWidth = width;
            int previewHeight = height;
            if (mPreviewSize != null) {
                previewWidth = mPreviewSize.width;
                previewHeight = mPreviewSize.height;
                if (curOrientation == Configuration.ORIENTATION_PORTRAIT) {
                    previewWidth = mPreviewSize.height;
                    previewHeight = mPreviewSize.width;
                }
            }
            // Center the child SurfaceView within the parent.
            if (width * previewHeight > height * previewWidth) {
                final int scaledChildWidth = previewWidth * height / previewHeight;
                this.layout((width - scaledChildWidth) / 2, 0,
                        (width + scaledChildWidth) / 2, height);
            } else {
                final int scaledChildHeight = previewHeight * width / previewWidth;
                this.layout(0, (height - scaledChildHeight) / 2,
                        width, (height + scaledChildHeight) / 2);
            }
        }
    }

    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w/h;
        if (sizes == null)
            return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public static void setCameraDisplayOrientation(int rotation, int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        camera.setDisplayOrientation(result);
    }

}
