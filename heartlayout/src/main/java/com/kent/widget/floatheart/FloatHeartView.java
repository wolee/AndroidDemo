package com.kent.widget.floatheart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.*;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.ikent.heartlayout.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by lijianfeng on 2016/3/16 下午 2:10 .
 */
public class FloatHeartView extends FrameLayout {
    public static final String TAG = "FloatHeartView";

    private static final float HEART_ROTATE_RANGE = 100;
    private static final float HEART_SCALES[] = {1.30f, 1.10f, 1.00f, 0.85f, 0.90f, 1.20f, 0.95f, 1.25f, 1.15f, 0.75f, 0.70f, 0.80f, 1.25f, 1.05f, 1.35f};
    private static final int HEART_DURATION[] = {4500, 4600, 4700, 4800, 4900, 5000, 5100, 5200, 5300, 5400, 5500, 5600, 5700, 6800, 6900, 6000};

    private static final int ANIM_SCALE_DURATION = 500;

    private static final int MAX_CHILD_COUNT = 50;
    private static final int MIN_ADD_INTERVAL = 200;
    /** 曲线高度个数分割 */
    private static final int POINT_COUNT = 3;
    /** 曲度 */
    private static final float RATIO = 0.2f;
    private static final boolean DEBUG = false;

    private static final boolean SCALE_ENABLE = true;
    private static final boolean ROTATE_ENABLE = true;

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
    private final Random mRandom = new Random();
    private final HashMap<Integer, WeakReference<Bitmap>> mBitmapMap = new HashMap<Integer, WeakReference<Bitmap>>();
    private final Interpolator mTranslateInterpolator = new DecelerateInterpolator(0.55f);
    private final Interpolator mScaleInterpolator = new AccelerateInterpolator(0.55f);
    private final Interpolator mAlphaInterpolator = new AccelerateInterpolator(2);

    public FloatHeartView(Context context) {
        this(context, null);
    }

    public FloatHeartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatHeartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    public void clearAnimation() {
        super.clearAnimation();
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).clearAnimation();
        }
        removeAllViews();
    }

    public void aminPause() {
        mAddHeartEnable = false;
    }

    public void aminResume() {
        mAddHeartEnable = true;
    }

    private boolean mAddHeartEnable;
    private long mLastAddTs;
    private long mBeginWaitTs = 0;
    public void addHeart(final int resId) {
        long now = SystemClock.elapsedRealtime();
        int childCount = getChildCount();
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
                    addHeart(resId);
                }
            }, 200);
            return;
        }

        mBeginWaitTs = 0;
        mLastAddTs = now;
        HeartHolder heart = new HeartHolder(resId);
        heart.showHeart();

        if (DEBUG) {
            Drawable drawable = getPathDrawable();
            setBackgroundDrawable(drawable);
        }
    }

    private Drawable getPathDrawable() {
        Bitmap bitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(0xE0FFFFFF);
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            HeartHolder holder = (HeartHolder) view.getTag();
            drawPath(canvas, holder);
        }
        return new BitmapDrawable(getResources(), bitmap);
    }

    private void drawPath(Canvas canvas, HeartHolder holder) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(4);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(holder.path, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(2);
        paint.setTextSize(30);
        for (int i = 0; i < holder.points.size(); i++) {
            CPoint point = holder.points.get(i);
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

    private class HeartHolder extends Animation implements Animation.AnimationListener{
        private final Path path;
        private final float rotate;
        private final float scale;
        private final PathMeasure pathMeasure;
        private final List<CPoint> points;
        private final int heartWidth;
        private final int heartHeight;
        private final ImageView heartView;
        private final float pathLength;

        private long startTime;

        public HeartHolder(int resId) {
            rotate = mRandom.nextFloat() * HEART_ROTATE_RANGE - HEART_ROTATE_RANGE / 2;
            scale = SCALE_ENABLE ? HEART_SCALES[mRandom.nextInt(HEART_SCALES.length)] : 1.0f;

            setStartOffset(ANIM_SCALE_DURATION);
            setDuration(HEART_DURATION[mRandom.nextInt(HEART_DURATION.length)]);
            setInterpolator(new LinearInterpolator());
            setAnimationListener(this);

            Bitmap bitmap = getBitmap(resId);
            heartWidth = bitmap.getWidth();
            heartHeight = bitmap.getHeight();

            heartView = new ImageView(getContext());
            heartView.setImageBitmap(bitmap);
            heartView.setTag(HeartHolder.this);

            CPoint start = new CPoint(getMeasuredWidth() / 2.0f, getMeasuredHeight());
            points = getPoints(start);
            path = builderPath(points);
            pathMeasure = new PathMeasure(path, false);
            pathLength = pathMeasure.getLength();
        }

        public void showHeart() {
            addView(heartView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
            if (ROTATE_ENABLE) {
                heartView.setRotation(rotate);
            }
            heartView.startAnimation(this);
        }

        @Override
        public void onAnimationStart(Animation animation) {
            startTime = SystemClock.elapsedRealtime();
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            removeView(heartView);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation transformation) {
            float translateTime = mTranslateInterpolator.getInterpolation(interpolatedTime);
            float[] pos = new float[2];
            pathMeasure.getPosTan(pathLength * translateTime, pos, null);
            float x = pos[0];
            float y = pos[1];

            long now = SystemClock.elapsedRealtime();
            long spend;
            if (startTime == 0) {
                spend = 0;
            } else {
                spend = now - startTime;
            }
            if (spend <= getStartOffset()) {
                float percent = spend * 1.0f / getStartOffset();
                percent = mScaleInterpolator.getInterpolation(percent);
                setScale(percent, x, y);
                return;
            }

            heartView.setScaleX(scale);
            heartView.setScaleY(scale);

            float rightY = y - (heartHeight - (heartHeight - heartHeight * scale) / 2);
            float offsetTop = rightY - heartView.getTop();
            float offsetLeft = x - heartWidth / 2.0f - heartView.getLeft();

            heartView.offsetTopAndBottom((int) offsetTop);
            heartView.offsetLeftAndRight((int) offsetLeft);

            float alpha = 1 - mAlphaInterpolator.getInterpolation(interpolatedTime);
            heartView.setAlpha(alpha);
        }

        private void setScale(float interpolatedTime, float x, float y) {
            float tempScale = scale * interpolatedTime;
            heartView.setScaleX(tempScale);
            heartView.setScaleY(tempScale);

            float rightY = y - (heartHeight - (heartHeight - heartHeight * tempScale) / 2);
            float offsetTop = rightY - heartView.getTop();
            float offsetLeft = x - heartWidth / 2.0f - heartView.getLeft();

            heartView.offsetTopAndBottom((int) offsetTop);
            heartView.offsetLeftAndRight((int) offsetLeft);

            heartView.setAlpha(1.0f);
        }

        private List<CPoint> getPoints(CPoint start) {
            List<CPoint> points = new ArrayList<CPoint>();
            float w = heartWidth * scale;
            float h = heartHeight * scale;
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
}
