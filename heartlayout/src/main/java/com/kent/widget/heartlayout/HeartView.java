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
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;


public class HeartView extends ImageView {

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

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        mHeartWidth = drawable.getIntrinsicWidth();
        mHeartHeight = drawable.getIntrinsicHeight();
    }

    public int getHeartHeight() {
        return mHeartHeight;
    }

    public int getHeartWidth() {
        return mHeartWidth;
    }
}
