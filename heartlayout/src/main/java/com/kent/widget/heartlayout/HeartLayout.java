/*
 * Copyright (C) 2015 tyrantgit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kent.widget.heartlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RelativeLayout;

import com.example.ikent.heartlayout.R;

import java.util.LinkedList;


public class HeartLayout extends RelativeLayout {

    private static final int DURATION = 4500;
    private static final int INTERVAL_SHOW_NOW = 150;
    private LinkedList<Integer> mResIdsQueue = new LinkedList<Integer>();
    private AbstractPathAnimator mAnimator;
    private long mLastShowRightNowTime = 0;
    private Handler mHanlder;
    private boolean mAminPaused = true;

//    public static final int[] HEART_RES_IDS = {
//            R.drawable.icon_live_heart0,
//            R.drawable.icon_live_heart1,
//            R.drawable.icon_live_heart2,
//            R.drawable.icon_live_heart3,
//            R.drawable.icon_live_heart4,
//            R.drawable.icon_live_heart5,
//            R.drawable.icon_live_heart6,
//            R.drawable.icon_live_heart7,
//    };

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

    public HeartLayout(Context context) {
        super(context);
        init(null, 0);
    }

    public HeartLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public HeartLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {

        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.HeartLayout, defStyleAttr, 0);
        mHanlder = new Handler(Looper.getMainLooper());
        mAnimator = new PathAnimator(AbstractPathAnimator.Config.fromTypeArray(a));
        a.recycle();
    }

    public void clearAnimation() {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).clearAnimation();
        }
        mResIdsQueue.clear();
        removeAllViews();
    }

    public void showHeart(int resId) {
        mResIdsQueue.add(resId);
        if (mResIdsQueue.size() <= 1) {
            mHanlder.post(new Runnable() {
                @Override
                public void run() {
                    startShow();
                }
            });
        }
    }

    public void showHeartNow(int resId) {
        long time = SystemClock.elapsedRealtime();
        if (Math.abs(time - mLastShowRightNowTime) > INTERVAL_SHOW_NOW) {
            mLastShowRightNowTime = time;
            Drawable drawable = getResources().getDrawable(resId);
            addHeart(drawable);
        }
    }

    private void startShow() {
        if (mResIdsQueue.isEmpty() || mAminPaused) {
            return;
        }
        Integer resId = mResIdsQueue.remove();
        if (resId != null) {
            Drawable drawable = getResources().getDrawable(resId);
            addHeart(drawable);
        }
        mHanlder.postDelayed(mCheckTask, INTERVAL_SHOW_NOW);
    }

    private Runnable mCheckTask = new Runnable() {
        @Override
        public void run() {
            startShow();
        }
    };

    public void aminPause() {
        mAminPaused = true;
        mHanlder.removeCallbacks(mCheckTask);
    }

    public void aminResume() {
        mAminPaused = false;
        mHanlder.removeCallbacks(mCheckTask);
        mHanlder.postDelayed(mCheckTask, INTERVAL_SHOW_NOW);
    }

    private void addHeart(Drawable drawable) {
        HeartView heartView = new HeartView(getContext());
        heartView.setImageDrawable(drawable);

        int heartWidth = heartView.getHeartWidth();
        int heartHeight = heartView.getHeartHeight();

        android.util.Log.v("HeartLayout", "addHeart, heartWidth=" + heartWidth + ", heartHeight=" + heartHeight);

        float rightPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 27.5f, getResources().getDisplayMetrics());
        rightPadding = getWidth() / 2.0f;
        float x = getWidth() - rightPadding - heartWidth / 2.0f;

        mAnimator.setInitX((int) x);
        mAnimator.setxRand(getWidth() - heartWidth);
        mAnimator.setAnimLength(getMeasuredHeight() - heartHeight * 2);
        mAnimator.setHeartWidth(heartWidth);
        mAnimator.setHeartHeight(heartHeight);
        mAnimator.setAnimDuration(DURATION);

        mAnimator.start(heartView, this);
    }
}
