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
package com.example.ikent.heartlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RelativeLayout;


public class HeartLayout extends RelativeLayout {

    private AbstractPathAnimator mAnimator;

    public static final int[] HEART_COLORS = {R.color.heart_color_0,
            R.color.heart_color_1,
            R.color.heart_color_2,
            R.color.heart_color_3,
            R.color.heart_color_4,
            R.color.heart_color_5,
            R.color.heart_color_6,
            R.color.heart_color_7};

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

    @Override
    public void setLayerType(int layerType, Paint paint) {
        if (supportNewApi()) {
            super.setLayerType(layerType, paint);
        }
    }

    private void init(AttributeSet attrs, int defStyleAttr) {

        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.HeartLayout, defStyleAttr, 0);

        mAnimator = new PathAnimator(AbstractPathAnimator.Config.fromTypeArray(a));

        a.recycle();
    }

    public AbstractPathAnimator getAnimator() {
        return mAnimator;
    }

    public void setAnimator(AbstractPathAnimator animator) {
        clearAnimation();
        mAnimator = animator;
    }

    public void clearAnimation() {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).clearAnimation();
        }
        removeAllViews();
    }

    public void addHeart(int color) {
        HeartView heartView = new HeartView(getContext());
        heartView.setColor(color);

        addHeart(color, R.drawable.heart, R.drawable.heart_border);
    }

    public void addHeart(int color, int heartResId, int heartBorderResId) {
        HeartView heartView = new HeartView(getContext());
        heartView.setColorAndDrawables(color, heartResId, heartBorderResId);


        int heartWidth = heartView.getHeartWidth();
        int heartHeight = heartView.getHeartHeight();

        float rightPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 27.5f, getResources().getDisplayMetrics());
        float x = getWidth() - rightPadding - heartWidth / 2.0f;

        mAnimator.setInitX((int) x);
        mAnimator.setxRand(getWidth() - heartWidth);
        mAnimator.setAnimLength(getMeasuredHeight() / 2);
        mAnimator.setHeartWidth(heartWidth);
        mAnimator.setHeartHeight(heartHeight);
        mAnimator.setAnimDuration(4000);

        mAnimator.start(heartView, this);
    }

    public boolean supportNewApi() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            return true;
        }
        return false;
    }
}
