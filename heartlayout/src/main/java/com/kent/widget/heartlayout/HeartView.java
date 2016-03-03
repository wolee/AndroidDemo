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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.example.ikent.heartlayout.R;


public class HeartView extends ImageView {

    private static final Paint sPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private int mHeartResId = R.drawable.heart;
    private int mHeartBorderResId = R.drawable.heart_border;
    private static Bitmap sHeart;
    private static Bitmap sHeartBorder;
    private static final Canvas sCanvas = new Canvas();

    private int mHeartWidth;
    private int mHeartHeight;

    public HeartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public HeartView(Context context) {
        super(context);
    }

    public void setColor(int color) {
        Bitmap heart = createHeart(color);
        mHeartWidth = heart.getWidth();
        mHeartHeight = heart.getHeight();
        setImageDrawable(new BitmapDrawable(getResources(), heart));
    }

    @Override
    public void setScaleX(float scaleX) {
        if (supportNewApi()) {
            super.setScaleX(scaleX);
        }
    }

    @Override
    public void setScaleY(float scaleY) {
        if (supportNewApi()) {
            super.setScaleY(scaleY);
        }
    }

    @Override
    public void setRotation(float rotation) {
        if (supportNewApi()) {
            super.setRotation(rotation);
        }
    }

    public int getHeartHeight() {
        return mHeartHeight;
    }

    public int getHeartWidth() {
        return mHeartWidth;
    }

    public void setColorAndDrawables(int color, int heartResId, int heartBorderResId) {
        if (heartResId != mHeartResId) {
            sHeart = null;
        }
        if (heartBorderResId != mHeartBorderResId) {
            sHeartBorder = null;
        }
        mHeartResId = heartResId;
        mHeartBorderResId = heartBorderResId;
        setColor(color);
    }

    public Bitmap createHeart(int color) {
        if (sHeart == null) {
            sHeart = BitmapFactory.decodeResource(getResources(), mHeartResId);
        }
        if (sHeartBorder == null) {
            sHeartBorder = BitmapFactory.decodeResource(getResources(), mHeartBorderResId);
        }
        Bitmap heart = sHeart;
        Bitmap heartBorder = sHeartBorder;
        Bitmap bm = createBitmapSafely(heartBorder.getWidth(), heartBorder.getHeight());
        if (bm == null) {
            return null;
        }
        Canvas canvas = sCanvas;
        canvas.setBitmap(bm);
        Paint p = sPaint;
        canvas.drawBitmap(heartBorder, 0, 0, p);
        p.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP));
        float dx = (heartBorder.getWidth() - heart.getWidth()) / 2f;
        float dy = (heartBorder.getHeight() - heart.getHeight()) / 2f;
        canvas.drawBitmap(heart, dx, dy, p);
        p.setColorFilter(null);
        if (supportNewApi()) {
            canvas.setBitmap(null);
        }
        return bm;
    }

    private static Bitmap createBitmapSafely(int width, int height) {
        try {
            return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
        }
        return null;
    }

    public boolean supportNewApi() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            return true;
        }
        return false;
    }
}