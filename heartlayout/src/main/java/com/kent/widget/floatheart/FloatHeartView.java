package com.kent.widget.floatheart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.example.ikent.heartlayout.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by lijianfeng on 2016/3/14 下午 4:04 .
 */
public class FloatHeartView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    public static final String TAG = "FloatHeartView";

    private static final float HEART_ROTATE_RANGE = 120;
    private static final float HEART_SCALES[] = {0.65f, 1.30f, 1.10f, 1.00f, 0.60f, 0.85f, 0.90f, 1.20f, 0.95f, 1.25f, 1.15f, 0.75f, 0.70f, 0.80f, 1.25f, 1.05f, 1.35f};
    private static final int HEART_DURATION[] = {5500, 5600, 5700, 5800, 5900, 6000, 6100, 6200, 6300, 6400, 6500, 6600, 6700, 6800, 6900, 7000};

    private static final long DEFAULT_FRAME_DELAY = 10;
    private static final int DEFAULT_QUEUE_SIZE = 50;
    private static final int DEFAULT_ADD_INTERVAL = 200;
    /** 曲线高度个数分割 */
    private static final int POINT_COUNT = 3;
    /** 曲度 */
    private static final float RATIO = 0.2f;

    private static final boolean DEBUG = false;


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



    private final HashMap<Integer, WeakReference<Bitmap>> mBitmapMap;
    private final ArrayList<HeartPath> mHeartQueue;
    private final SurfaceHolder mHolder;
    private Interpolator mInterpolator ;
    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private long mLastAddTs;
    private final Random mRandom = new Random();
    private AtomicBoolean mRunable = new AtomicBoolean(false);

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
        mHeartQueue = new ArrayList<HeartPath>();
        mBitmapMap = new HashMap<Integer, WeakReference<Bitmap>>();

        mInterpolator = new AccelerateDecelerateInterpolator();
    }

    private void ensureThread() {
        if (mHandler == null) {
            if (mHandlerThread != null) {
                mHandlerThread.quit();
            }
            mHandlerThread = new HandlerThread("FloatHeartView");
            mHandlerThread.start();
            mHandler = new Handler(mHandlerThread.getLooper());
        }
    }

    private void destroyThread() {
        if (mHandlerThread != null) {
            mHandlerThread.quit();
            mHandlerThread = null;
        }
        mHandler = null;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated");
        ensureThread();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed");
        destroyThread();
    }

    public void destroy() {
        Log.d(TAG, "destroy");
        destroyThread();
    }

    public void clearHeart() {
        if (mHandler == null) {
            mHeartQueue.clear();
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mHeartQueue.clear();
            }
        });
    }

    private long mBeginWaitTs = 0;
    public void addHeart(final int resId) {
        Log.d(TAG, "addHeart resid=" + resId);
        long now = SystemClock.elapsedRealtime();
        if (Math.abs(now - mLastAddTs) < DEFAULT_ADD_INTERVAL || mHeartQueue.size() >= DEFAULT_QUEUE_SIZE) {
            Log.e(TAG, "addHeart abandon");
            return;
        }
        ensureThread();
        if (getMeasuredHeight() == 0 || getMeasuredWidth() == 0) {
            if (mBeginWaitTs == 0) {
                mBeginWaitTs = SystemClock.elapsedRealtime();
            }
            if (Math.abs(now - mBeginWaitTs) > 10000) {
                mBeginWaitTs = 0;
                return;
            }
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    addHeart(resId);
                }
            }, 200);
            return;
        }
        mBeginWaitTs = 0;
        mLastAddTs = now;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                HeartPath stat = new HeartPath(resId);
                if (mHeartQueue.isEmpty()) {
                    mHeartQueue.add(stat);
                    startFloatAnim();
                } else {
                    mHeartQueue.add(stat);
                }
            }
        });
    }

    public void startFloatAnim() {
        Log.d(TAG, "startFloatAnim");
        ensureThread();
        mRunable.set(true);
        mHandler.post(this);
    }

    public void stopFloatAnim() {
        Log.d(TAG, "stopFloatAnim");
        ensureThread();
        mRunable.set(false);
        mHandler.removeCallbacks(this);
    }

    @Override
    public void run() {
        if (!mRunable.get()) {
            Log.e(TAG, "mRunable is false");
            return;
        }
        Canvas canvas = mHolder.lockCanvas();
        if (canvas == null) {
            Log.e(TAG, "canvas is null");
            return;
        }
        canvas.drawColor(Color.WHITE);
        List<HeartPath> removes = new ArrayList<HeartPath>();
        for (int i = 0; i < mHeartQueue.size(); i++) {
            HeartPath heartPath = mHeartQueue.get(i);
            if (DEBUG) {
                drawPath(canvas, heartPath);
            }
            if (!heartPath.draw(canvas)) {
                removes.add(heartPath);
            }
        }
        mHeartQueue.removeAll(removes);
        mHolder.unlockCanvasAndPost(canvas);
        if (!mHeartQueue.isEmpty()) {
            mHandler.postDelayed(this, DEFAULT_FRAME_DELAY);
        }
    }

    private void drawPath(Canvas canvas, HeartPath heartPath) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(4);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(heartPath.path, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(2);
        paint.setTextSize(30);
        for (int i = 0; i < heartPath.points.size(); i++) {
            CPoint point = heartPath.points.get(i);
            Log.d(TAG, point.toString());
            canvas.drawText(String.valueOf(i+1), point.x, point.y, paint);
        }
    }

    private Bitmap getBitmap(int resId) {
        WeakReference<Bitmap> weakReference = mBitmapMap.get(resId);
        if (weakReference == null || weakReference.get() == null || weakReference.get().isRecycled()) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
            weakReference = new WeakReference<Bitmap>(bitmap);
            mBitmapMap.put(resId, weakReference);
        }
        return weakReference.get();
    }

    private class HeartPath {


        private final int resId;
        private final Path path;
        private final float rotate;
        private  final Paint paint;
        private final float scale;
        private final int duration;
        private final PathMeasure pathMeasure;
        private final List<CPoint> points;


        private float alpha;
        private long beginTime;
        private float x;
        private float y;

        public HeartPath(int resId) {
            this.resId = resId;
            alpha = 1;
            y = getMeasuredHeight();
            x = getMeasuredWidth() / 2.0f;

            rotate = mRandom.nextFloat() * HEART_ROTATE_RANGE - HEART_ROTATE_RANGE / 2;
            scale = HEART_SCALES[mRandom.nextInt(HEART_SCALES.length)];
            duration = HEART_DURATION[mRandom.nextInt(HEART_DURATION.length)];

            CPoint start = new CPoint(x, y);
            points = getPoints(start);
            path = builderPath(points);
            pathMeasure = new PathMeasure(path, false);

            paint = new Paint();
            paint.setAntiAlias(true);
        }

        public boolean draw(Canvas canvas) {
            if (beginTime == 0) {
                beginTime = SystemClock.elapsedRealtime();
            }
            long now = SystemClock.elapsedRealtime();
            float factor = (now - beginTime) * 1.0f / duration;
            if (factor > 1) {
                Log.d(TAG, "return");
                return false;
            }

            Bitmap bitmap = getBitmap(resId);
            factor = mInterpolator.getInterpolation(factor);
            float[] pos = new float[2];
            pathMeasure.getPosTan(pathMeasure.getLength() * factor, pos, null);
            x = pos[0];
            y = pos[1];

            Log.d(TAG, "HeartPath draw factor=" + factor);
            float tempScale = scale;
            if (3000.0F * factor < 200.0F) {
                tempScale = scale(factor, 0.0D, 0.06666667014360428D, 0.20000000298023224D, scale);
            }

            float alphaChangedFlag = (getMeasuredHeight() - bitmap.getHeight() * scale) * 0.35f;
            if ((y - bitmap.getHeight() * scale) > alphaChangedFlag) {
                alpha = 1;
            } else {
                alpha = (y - bitmap.getHeight() * scale) / alphaChangedFlag;
            }
            paint.setAlpha((int)(alpha * 255));

            Matrix matrix = new Matrix();
            matrix.postScale(tempScale, tempScale, bitmap.getWidth() / 2.0f, bitmap.getHeight() / 2.0f);
            matrix.postRotate(rotate, bitmap.getWidth() / 2.0f, bitmap.getHeight() / 2.0f);
            matrix.postTranslate(x - bitmap.getWidth() / 2.0f, y - bitmap.getHeight()  * tempScale);

            canvas.drawBitmap(bitmap, matrix, paint);
            Log.d(TAG, "HeartPath draw x=" + x + ", y=" + y + ", alpha=" + alpha + ", rotate=" + rotate);
            return true;
        }

        private List<CPoint> getPoints(CPoint start) {
            List<CPoint> points = new ArrayList<CPoint>();
            Bitmap bitmap = getBitmap(resId);
            float w = bitmap.getWidth() * scale;
            float h = bitmap.getHeight() * scale;
            for (int i = 0; i < POINT_COUNT; i++) {
                if (i == 0) {
                    points.add(start);
                } else {
                    CPoint tmp = new CPoint(0, 0);
                    tmp.x = mRandom.nextInt((int)(getMeasuredWidth() - w)) + w / 2.0f;
                    float range = 0;
                    float len = (getMeasuredHeight() - h) / (float) (POINT_COUNT - 1);
                    if (i < POINT_COUNT - 1) {
                        range = len * 0.4f;
                    }

                    float dy = mRandom.nextFloat() * range - range / 2.0f;
                    tmp.y = getMeasuredHeight() - len * i + dy;
                    points.add(tmp);
                }
            }
            return points;
        }

        private Path builderPath(List<CPoint> points) {
            Path p = new Path();
            if (points.size() > 1) {
                for (int j = 0; j < points.size(); j++) {
                    CPoint point = points.get(j);
                    if (j == 0) {
                        CPoint next = points.get(j + 1);
                        point.dx = ((next.x - point.x) * RATIO);
                        point.dy = ((next.y - point.y) * RATIO);
                    } else if (j == points.size() - 1) {
                        CPoint prev = points.get(j - 1);
                        point.dx = ((point.x - prev.x) * RATIO);
                        point.dy = ((point.y - prev.y) * RATIO);
                    } else {
                        CPoint next = points.get(j + 1);
                        CPoint prev = points.get(j - 1);
                        point.dx = ((next.x - prev.x) * RATIO);
                        point.dy = ((next.y - prev.y) * RATIO);
                    }

                    // create the cubic-spline path
                    if (j == 0) {
                        p.moveTo(point.x, point.y);
                    } else {
                        CPoint prev = points.get(j - 1);
                        p.cubicTo(prev.x + prev.dx, (prev.y + prev.dy),
                                point.x - point.dx, (point.y - point.dy),
                                point.x, point.y);
                    }
                }
            }
            return p;
        }
    }

    private class CPoint {

        public float x = 0f;
        public float y = 0f;

        /** x-axis distance */
        public float dx = 0f;

        /** y-axis distance */
        public float dy = 0f;

        public CPoint(float x, float y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "CPoint:[x="+ x +", y="+ y +"]";
        }
    }
    private static float scale(double a, double b, double c, double d, double e) {
        return (float) ((a - b) / (c - b) * (e - d) + d);
    }

}
