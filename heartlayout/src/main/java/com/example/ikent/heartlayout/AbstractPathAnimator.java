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

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Path;
import android.view.View;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


public abstract class AbstractPathAnimator {
    private final Random mRandom;
    protected final Config mConfig;


    public AbstractPathAnimator(Config config) {
        mConfig = config;
        mRandom = new Random();
    }

    public float randomRotation() {
        return mRandom.nextFloat() * 28.6F - 14.3F;
    }

    public Path createPath(AtomicInteger counter, View view, int factor) {
        Random r = mRandom;
        int x = r.nextInt(mConfig.xRand * 2) - mConfig.xRand;
        int x2 = r.nextInt(mConfig.xRand * 2) - mConfig.xRand;

        x = mConfig.xPointFactor + x;
        x2 = mConfig.xPointFactor + x2;

        x = x < 0 ? 0 : x;
        x2 = x2 < 0 ? 0 : x2;

        x = x > mConfig.xRand ? mConfig.xRand : x;
        x2 = x2 > mConfig.xRand ? mConfig.xRand : x2;

        int y = view.getHeight() - mConfig.initY;
        int y2 = counter.intValue() * 15 + mConfig.animLength * factor + r.nextInt(mConfig.animLengthRand);
        factor = y2 / mConfig.bezierFactor;

        int y3 = y - y2;
        y2 = y - y2 / 2;
        Path p = new Path();
        p.moveTo(mConfig.initX, y);
        p.cubicTo(mConfig.initX, y - factor, x, y2 + factor, x, y2);
        p.moveTo(x, y2);
        p.cubicTo(x, y2 - factor, x2, y3 + factor, x2, y3);
        return p;
    }

    public abstract void start(HeartView child, HeartLayout parent);

    public void setInitX(int initX) {
        if (mConfig != null) {
            mConfig.initX = initX;
        }
    }

    public void setInitY(int initY) {
        if (mConfig != null) {
            mConfig.initY = initY;
        }
    }

    public void setxRand(int xRand) {
        if (mConfig != null) {
            mConfig.xRand = xRand;
        }
    }

    public void setAnimLengthRand(int animLengthRand) {
        if (mConfig != null) {
            mConfig.animLengthRand = animLengthRand;
        }
    }

    public void setBezierFactor(int bezierFactor) {
        if (mConfig != null) {
            mConfig.bezierFactor = bezierFactor;
        }
    }

    public void setxPointFactor(int xPointFactor) {
        if (mConfig != null) {
            mConfig.xPointFactor = xPointFactor;
        }
    }

    public void setAnimLength(int animLength) {
        if (mConfig != null) {
            mConfig.animLength = animLength;
        }
    }

    public void setHeartWidth(int heartWidth) {
        if (mConfig != null) {
            mConfig.heartWidth = heartWidth;
        }
    }

    public void setHeartHeight(int heartHeight) {
        if (mConfig != null) {
            mConfig.heartHeight = heartHeight;
        }
    }

    public void setAnimDuration(int animDuration) {
        if (mConfig != null) {
            mConfig.animDuration = animDuration;
        }
    }

    public static class Config {
        public int initX;
        public int initY;
        public int xRand;
        public int animLengthRand;
        public int bezierFactor;
        public int xPointFactor;
        public int animLength;
        public int heartWidth;
        public int heartHeight;
        public int animDuration;

        static Config fromTypeArray(TypedArray typedArray) {
            Config config = new Config();
            Resources res = typedArray.getResources();
            config.initX = (int) typedArray.getDimension(R.styleable.HeartLayout_initX,
                    res.getDimensionPixelOffset(R.dimen.heart_anim_init_x));
            config.initY = (int) typedArray.getDimension(R.styleable.HeartLayout_initY,
                    res.getDimensionPixelOffset(R.dimen.heart_anim_init_y));
            config.xRand = (int) typedArray.getDimension(R.styleable.HeartLayout_xRand,
                    res.getDimensionPixelOffset(R.dimen.heart_anim_bezier_x_rand));
            config.animLength = (int) typedArray.getDimension(R.styleable.HeartLayout_animLength,
                    res.getDimensionPixelOffset(R.dimen.heart_anim_length));
            config.animLengthRand = (int) typedArray.getDimension(R.styleable.HeartLayout_animLengthRand,
                    res.getDimensionPixelOffset(R.dimen.heart_anim_length_rand));
            config.bezierFactor = typedArray.getInteger(R.styleable.HeartLayout_bezierFactor,
                    res.getInteger(R.integer.heart_anim_bezier_factor));
            config.xPointFactor = (int) typedArray.getDimension(R.styleable.HeartLayout_xPointFactor,
                    res.getDimensionPixelOffset(R.dimen.heart_anim_x_point_factor));
            config.heartWidth = (int) typedArray.getDimension(R.styleable.HeartLayout_heart_width,
                    res.getDimensionPixelOffset(R.dimen.heart_size_width));
            config.heartHeight = (int) typedArray.getDimension(R.styleable.HeartLayout_heart_height,
                    res.getDimensionPixelOffset(R.dimen.heart_size_height));
            config.animDuration = typedArray.getInteger(R.styleable.HeartLayout_anim_duration,
                    res.getInteger(R.integer.anim_duration));
            return config;
        }
    }
}

