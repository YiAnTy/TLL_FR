package bjtu.makeupapp;

import android.content.Context;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.hardware.Camera.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

/**
 * Created by lenovo on 2017/8/26.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    public static final String LOG_TAG="CameraPreview";

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

        // The Surface has been created, now tell the camera where to draw the
        // preview.

        try {
            if (null != mCamera) {
                mCamera.setPreviewDisplay(surfaceHolder);
            }
        } catch (IOException e) {
            e.printStackTrace();

            Log.d(LOG_TAG,
                    "Error setting camera preview display: " + e.getMessage());
        }

        try {
            if (null != mCamera) {
                mCamera.startPreview();
            }
            Log.d(LOG_TAG, "surfaceCreated successfully! ");
        }catch (Exception e){
            Log.d(LOG_TAG,
                    "Error setting camera preview: " + e.getMessage());
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

        Log.d(LOG_TAG, "surface changed");

        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (null == surfaceHolder.getSurface()) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            if (null != mCamera) {
                mCamera.stopPreview();
            }
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        if (null != mCamera) {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);

            requestLayout();

            mCamera.setParameters(parameters);
            mCamera.setDisplayOrientation(90);

            Log.d(LOG_TAG, "camera set parameters successfully!: "
                    + parameters);
        }
        // 这里可以用来设置尺寸

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

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        Log.d(LOG_TAG, "surfaceDestroyed");

        if (null != mCamera) {
            mCamera.stopPreview();
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

        if (mSupportedPreviewSizes != null)
        {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width,
                    height);
        }
    }

    private Size getOptimalPreviewSize(List<Size> sizes,int w,int h){
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null)
            return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes)
        {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff)
            {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null)
        {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes)
            {
                if (Math.abs(size.height - targetHeight) < minDiff)
                {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

}
