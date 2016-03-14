package com.kent.widget.floatheart;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by lijianfeng on 2016/3/14 下午 4:04 .
 */
public class FloatHeartView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    public static final String TAG = "FloatHeartView";

    private static final long DEFAULT_FRAME_DELAY = 10;

    private final SurfaceHolder mHolder;
    private HandlerThread mHandlerThread;
    private Handler mHandler;

    public FloatHeartView(Context context) {
        this(context, null);
    }

    public FloatHeartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatHeartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mHandlerThread = new HandlerThread("FloatHeartView");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mHandlerThread != null) {
            mHandlerThread.quit();
        }
        mHandlerThread = null;

    }

    @Override
    public void run() {

    }



}
