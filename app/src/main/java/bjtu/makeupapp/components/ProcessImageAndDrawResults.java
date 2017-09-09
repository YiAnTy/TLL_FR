package bjtu.makeupapp.components;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;

import com.luxand.FSDK;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import bjtu.makeupapp.activity.MainActivity;
import bjtu.makeupapp.activity.R;
import bjtu.makeupapp.util.ScriptC_Rotate;

/**
 * Created by lenovo on 2017/9/7.
 *
 * Draw graphics on top of the video
 *
 */

public class ProcessImageAndDrawResults extends View {

    public FSDK.HTracker mTracker;

    final int MAX_FACES = 5;
    final FaceRectangle[] mFacePositions = new FaceRectangle[MAX_FACES];
    final FSDK.FSDK_Features[] mFacialFeatures = new FSDK.FSDK_Features[MAX_FACES];
    final long[] mIDs = new long[MAX_FACES];
    final RectF mRectangle = new RectF();
    final Lock faceLock = new ReentrantLock();
    int mTouchedIndex;
    public int mStopping;
    public int mStopped;

    Context mContext;
    Paint mPaintGreen, mPaintBlue, mPaintBlueTransparent,mPaintGreenTransparent;
    byte[] mYUVData;
    byte[] mRGBData;
    int mImageWidth, mImageHeight;
    boolean first_frame_saved;
    boolean rotated;

    private int GetFaceFrame(FSDK.FSDK_Features features, FaceRectangle fr) {
        if (features == null || fr == null)
            return FSDK.FSDKE_INVALID_ARGUMENT;

        float u1 = features.features[0].x;
        float v1 = features.features[0].y;
        float u2 = features.features[1].x;
        float v2 = features.features[1].y;
        float xc = (u1 + u2) / 2;
        float yc = (v1 + v2) / 2;
        int w = (int) Math.pow((u2 - u1) * (u2 - u1) + (v2 - v1) * (v2 - v1), 0.5);

        fr.x1 = (int) (xc - w * 1.6 * 0.9);
        fr.y1 = (int) (yc - w * 1.1 * 0.9);
        fr.x2 = (int) (xc + w * 1.6 * 0.9);
        fr.y2 = (int) (yc + w * 2.1 * 0.9);
        if (fr.x2 - fr.x1 > fr.y2 - fr.y1) {
            fr.x2 = fr.x1 + fr.y2 - fr.y1;
        } else {
            fr.y2 = fr.y1 + fr.x2 - fr.x1;
        }
        return 0;
    }

    public ProcessImageAndDrawResults(Context context) {
        super(context);

        for (int i = 0; i < MAX_FACES; ++i) {
            mFacialFeatures[i] = new FSDK.FSDK_Features();
        }

        mTouchedIndex = -1;

        mStopping = 0;
        mStopped = 0;
        rotated = false;
        mContext = context;

        mPaintGreen = new Paint();
        mPaintGreen.setStyle(Paint.Style.FILL);
        mPaintGreen.setColor(Color.GREEN);
        mPaintGreen.setTextSize(18 * MainActivity.sDensity);
        mPaintGreen.setTextAlign(Paint.Align.CENTER);
        mPaintBlue = new Paint();
        mPaintBlue.setStyle(Paint.Style.FILL);
        mPaintBlue.setColor(Color.BLUE);
        mPaintBlue.setTextSize(18 * MainActivity.sDensity);
        mPaintBlue.setTextAlign(Paint.Align.CENTER);

        mPaintBlueTransparent = new Paint();
        mPaintBlueTransparent.setStyle(Paint.Style.STROKE);
        mPaintBlueTransparent.setStrokeWidth(2);
        mPaintBlueTransparent.setColor(Color.BLUE);
        mPaintBlueTransparent.setTextSize(25);

        mPaintGreenTransparent = new Paint();
        mPaintGreenTransparent.setStyle(Paint.Style.STROKE);
        mPaintGreenTransparent.setStrokeWidth(2 * MainActivity.sDensity);
        mPaintGreenTransparent.setColor(Color.GREEN);
        mPaintGreenTransparent.setTextSize(18 * MainActivity.sDensity);

        //mBitmap = null;
        mYUVData = null;
        mRGBData = null;

        first_frame_saved = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        if (mStopping == 1) {
            mStopped = 1;
            super.onDraw(canvas);
            return;
        }

        if (mYUVData == null || mTouchedIndex != -1) {
            super.onDraw(canvas);
            return; //nothing to process or name is being entered now
        }

        int canvasWidth = canvas.getWidth();
        //int canvasHeight = canvas.getHeight();

        // Convert from YUV to RGB
        decodeYUV420SP(mRGBData, mYUVData, mImageWidth, mImageHeight);

        // Load image to FaceSDK
        FSDK.HImage Image = new FSDK.HImage();
        FSDK.FSDK_IMAGEMODE imagemode = new FSDK.FSDK_IMAGEMODE();
        imagemode.mode = FSDK.FSDK_IMAGEMODE.FSDK_IMAGE_COLOR_24BIT;
        FSDK.LoadImageFromBuffer(Image, mRGBData, mImageWidth, mImageHeight, mImageWidth*3, imagemode);
        FSDK.MirrorImage(Image, false);
        FSDK.HImage RotatedImage = new FSDK.HImage();
        FSDK.CreateEmptyImage(RotatedImage);

        //it is necessary to work with local variables (onDraw called not the time when mImageWidth,... being reassigned, so swapping mImageWidth and mImageHeight may be not safe)
        int ImageWidth = mImageWidth;
        //int ImageHeight = mImageHeight;
        if (rotated) {
            ImageWidth = mImageHeight;
            //ImageHeight = mImageWidth;
            FSDK.RotateImage90(Image, -1, RotatedImage);
        } else {
            FSDK.CopyImage(Image, RotatedImage);
        }
        FSDK.FreeImage(Image);

        // Save first frame to gallery to debug (e.g. rotation angle)
		/*
		if (!first_frame_saved) {
			first_frame_saved = true;
			String galleryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
			FSDK.SaveImageToFile(RotatedImage, galleryPath + "/first_frame.jpg"); //frame is rotated!
		}
		*/

        long IDs[] = new long[MAX_FACES];
        long face_count[] = new long[1];

        FSDK.FeedFrame(mTracker, 0, RotatedImage, face_count, IDs);
        FSDK.FreeImage(RotatedImage);

        faceLock.lock();

        for (int i=0; i<MAX_FACES; ++i) {
            mFacePositions[i] = new FaceRectangle();
            mFacePositions[i].x1 = 0;
            mFacePositions[i].y1 = 0;
            mFacePositions[i].x2 = 0;
            mFacePositions[i].y2 = 0;
            mIDs[i] = IDs[i];
        }

        float ratio = (canvasWidth * 0.96f) / ImageWidth;
        for (int i = 0; i < (int)face_count[0]; ++i) {
            FSDK.FSDK_Features Eyes = new FSDK.FSDK_Features();
            FSDK.GetTrackerEyes(mTracker, 0, mIDs[i], Eyes);
            FSDK.GetTrackerFacialFeatures(mTracker, 0, mIDs[i], mFacialFeatures[i]);

            GetFaceFrame(Eyes, mFacePositions[i]);
            mFacePositions[i].x1 *= ratio;
            mFacePositions[i].y1 *= ratio;
            mFacePositions[i].x2 *= ratio;
            mFacePositions[i].y2 *= ratio;
        }

        faceLock.unlock();

        // Mark and name faces
//        for (int i=0; i<face_count[0]; ++i) {
//            canvas.drawRect(mFacePositions[i].x1, mFacePositions[i].y1,
//                    mFacePositions[i].x2, mFacePositions[i].y2, mPaintBlueTransparent);
//        }
        RectF rec_eyebrow_left=new RectF();
        RectF rec_eyebrow_right=new RectF();
        // Mark faces and features
        for (int i = 0; i < face_count[0]; ++i) {
            mRectangle.left    = mFacePositions[i].x1 * ratio;
            mRectangle.top     = mFacePositions[i].y1 * ratio;
            mRectangle.right   = mFacePositions[i].x2 * ratio;
            mRectangle.bottom  = mFacePositions[i].y2 * ratio;
            float diam    = 2 * MainActivity.sDensity;
            for (int j = 0; j < FSDK.FSDK_FACIAL_FEATURE_COUNT; ++j) {
                mRectangle.left   = mFacialFeatures[i].features[j].x * ratio - diam;
                mRectangle.top    = mFacialFeatures[i].features[j].y * ratio - diam;
                mRectangle.right  = mFacialFeatures[i].features[j].x * ratio + diam;
                mRectangle.bottom = mFacialFeatures[i].features[j].y * ratio + diam;
                if (j == FSDK.FSDKP_LEFT_EYE_LOWER_LINE2 || j == FSDK.FSDKP_LEFT_EYE_UPPER_LINE2
                        || j == FSDK.FSDKP_RIGHT_EYE_LOWER_LINE2 || j == FSDK.FSDKP_RIGHT_EYE_UPPER_LINE2) {
                    canvas.drawOval(mRectangle, mPaintGreenTransparent);
                } else {
                    canvas.drawOval(mRectangle, mPaintBlueTransparent);
                }

                if(j==FSDK.FSDKP_LEFT_EYEBROW_INNER_CORNER){
                    rec_eyebrow_left.left=mFacialFeatures[i].features[j].x*ratio-diam;
                    rec_eyebrow_left.top=mFacialFeatures[i].features[j].y*ratio-diam;
                    rec_eyebrow_left.right=mFacialFeatures[i].features[j].x*ratio+diam;
                    rec_eyebrow_left.bottom=mFacialFeatures[i].features[j].y*ratio+diam;
                }
                if(j==FSDK.FSDKP_RIGHT_EYEBROW_INNER_CORNER){
                    rec_eyebrow_right.left=mFacialFeatures[i].features[j].x*ratio-diam;
                    rec_eyebrow_right.top=mFacialFeatures[i].features[j].y*ratio-diam;
                    rec_eyebrow_right.right=mFacialFeatures[i].features[j].x*ratio+diam;
                    rec_eyebrow_right.bottom=mFacialFeatures[i].features[j].y*ratio+diam;
                }
            }
        }
        RectF rec_eyebrow_center=new RectF();
        rec_eyebrow_center.left=(rec_eyebrow_right.left*0.5f)+(rec_eyebrow_left.left*0.5f)-10;
        rec_eyebrow_center.right=(rec_eyebrow_right.right*0.5f)+(rec_eyebrow_left.right*0.5f)+10;
        rec_eyebrow_center.top=(rec_eyebrow_right.top*0.5f)+(rec_eyebrow_left.top*0.5f)-10;
        rec_eyebrow_center.bottom=(rec_eyebrow_right.bottom*0.5f)+(rec_eyebrow_left.bottom*0.5f)+10;
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.flower1);
        canvas.drawBitmap(bitmap,null,rec_eyebrow_center,null);

        super.onDraw(canvas);
    } // end onDraw method

    static public void decodeYUV420SP(byte[] rgb, byte[] yuv420sp, int width, int height) {
        final int frameSize = width * height;
        int yp = 0;
        for (int j = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0) y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }
                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);
                if (r < 0) r = 0; else if (r > 262143) r = 262143;
                if (g < 0) g = 0; else if (g > 262143) g = 262143;
                if (b < 0) b = 0; else if (b > 262143) b = 262143;

                rgb[3*yp] = (byte) ((r >> 10) & 0xff);
                rgb[3*yp+1] = (byte) ((g >> 10) & 0xff);
                rgb[3*yp+2] = (byte) ((b >> 10) & 0xff);
                ++yp;
            }
        }
    }
}

class FaceRectangle {
    public int x1, y1, x2, y2;
}
