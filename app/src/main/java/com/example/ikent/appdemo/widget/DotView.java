package com.example.ikent.appdemo.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

/**
 * Created by lijianfeng on 16/1/22 下午8:58.
 */
public class DotView extends TextView {
    public static final String TAG = "DotView";



    private int mDotColor;
    private float mSize;

    public DotView(Context context) {
        super(context);
        setDefault(true);
    }

    public DotView(Context context, float size, int color) {
        super(context);
        setDefault(false);
        mSize = size;
        mDotColor = color;
        change();
    }

    public DotView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDefault(true);
    }

    public DotView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setDefault(true);
    }

    public void setDotColor(int mDotColor) {
        this.mDotColor = mDotColor;
        change();
    }

    public float getSize() {
        return mSize;
    }

    public void setSize(float mSize) {
        this.mSize = mSize;
        setHeight((int)mSize);
        setMinWidth((int) mSize);
        change();
    }

    private void setDefault(boolean changeNow) {
        mDotColor = 0xb7ff0000;
        setGravity(Gravity.CENTER);
        setTextColor(Color.WHITE);
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, metrics);

        int paddingLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, metrics);
        int paddingTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -0.5f, metrics);
        int paddingRight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, metrics);
        int paddingBottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, metrics);

        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

        setHeight((int) mSize);
        setMinWidth((int) mSize);

        Log.v(TAG, "setDefault mSize=" + mSize + ", mDotColor=" + mDotColor + ", paddingLeft=" + paddingLeft + ", paddingTop=" + paddingTop + ", paddingRight=" + paddingRight + ", paddingBottom=" + paddingBottom);
        if (changeNow) {
            change();
        }
    }

    private void change() {
        float[] outerR = new float[] { mSize / 2, mSize / 2, mSize / 2, mSize / 2, mSize / 2, mSize / 2, mSize / 2, mSize / 2};
        Shape shape = new RoundRectShape(outerR, null, null);
        ShapeDrawable shapeDrawable = new ShapeDrawable(shape);
        shapeDrawable.setIntrinsicHeight((int)mSize);
        shapeDrawable.setIntrinsicWidth((int) mSize);
        shapeDrawable.setPadding(0, 0, 0, 0);
        shapeDrawable.getPaint().setColor(mDotColor);
        shapeDrawable.getPaint().setStyle(Paint.Style.FILL);
        setBackgroundDrawable(shapeDrawable);
    }
}
