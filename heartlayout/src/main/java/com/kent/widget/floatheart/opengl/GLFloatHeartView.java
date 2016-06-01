package com.kent.widget.floatheart.opengl;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;

import com.example.ikent.heartlayout.R;


/**
 *
 */
public class GLFloatHeartView extends GLSurfaceView {
    public static final String TAG = "FloatHeartView";

    private static final int MAX_CHILD_COUNT = 80;
    private static final int MIN_ADD_INTERVAL = 0;

    public static final int[] HEART_RES_IDS = {
            R.mipmap.like_other1,
            R.mipmap.like_other2,
            R.mipmap.like_other3,
            R.mipmap.like_other4,
            R.mipmap.like_other5,
            R.mipmap.like_other6,
            R.mipmap.like_other7,
            R.mipmap.like_self1,
            R.mipmap.like_self2,
            R.mipmap.like_self3,
    };

    public GLFloatHeartView(Context context) {
        this(context, null);
    }

    public GLFloatHeartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public int statusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void clearAnimation() {
        super.clearAnimation();
        clear();
    }

    public void aminPause() {
        mAddHeartEnable = false;
        onPause();
        setVisibility(INVISIBLE);
    }

    public void aminResume() {
        mAddHeartEnable = true;
        onResume();
        setVisibility(VISIBLE);
    }

    private boolean mAddHeartEnable;
    private long mLastAddTs;
    private long mBeginWaitTs = 0;
    public void addHeart(final int index) {
        long now = SystemClock.elapsedRealtime();
        int childCount = mRenderer.getQueueCount();
        if (!mAddHeartEnable || Math.abs(now - mLastAddTs) < MIN_ADD_INTERVAL || childCount > MAX_CHILD_COUNT) {
            Log.e(TAG, "addHeart abandon");
            return;
        }
        if (getMeasuredHeight() == 0 || getMeasuredWidth() == 0) {
            if (mBeginWaitTs == 0) {
                mBeginWaitTs = SystemClock.elapsedRealtime();
            }
            if (Math.abs(now - mBeginWaitTs) > 10000) {
                mBeginWaitTs = 0;
                return;
            }
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    renderAddHeart(index);
                }
            }, 200);
            return;
        }

        mBeginWaitTs = 0;
        mLastAddTs = now;

        renderAddHeart(index);
    }

    private GLRenderer mRenderer;

    private void init(Context context) {
        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(false);
        setZOrderMediaOverlay(true);
        mRenderer = new GLRenderer(context);
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    private void renderAddHeart(int index) {
        mRenderer.addHeart(index);
    }

    @Override
    public void onPause() {
        super.onPause();
        clear();
//        mRenderer.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mRenderer.onResume();
    }

    public void clear() {
        mRenderer.clear();
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }
}
