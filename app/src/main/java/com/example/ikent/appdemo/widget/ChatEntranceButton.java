package com.example.ikent.appdemo.widget;

import android.content.Context;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.ikent.appdemo.R;


/**
 * Created by lijianfeng on 16/1/22 下午6:09.
 */
public class ChatEntranceButton extends ViewGroup implements View.OnClickListener{
    public static final String TAG = "ChatEntranceButton";

    private ImageView mIconView;
    private DotView mUnreadView;
    private int mUnread = 0;

    public ChatEntranceButton(Context context) {
        super(context);
        setupViews();
    }

    public ChatEntranceButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupViews();
    }

    public ChatEntranceButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupViews();
    }

    private void setupViews() {
        setOnClickListener(this);
        setBackgroundResource(R.drawable.top_bar_btn);
        setFocusable(true);
        setClickable(true);
        setFocusableInTouchMode(true);

        mIconView = new ImageView(getContext());
        mUnreadView = new DotView(getContext());

        mIconView.setImageResource(R.drawable.chat_entrance_ic);

        TextPaint tp = mUnreadView.getPaint();
        tp.setFakeBoldText(true);

        addView(mIconView);
        addView(mUnreadView);
        setUnread(8);
        shouldUnreadViewDisplay();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    private void shouldUnreadViewDisplay() {
        if (mUnread <= 0) {
            mUnreadView.setVisibility(GONE);
        } else {
            mUnreadView.setVisibility(VISIBLE);
        }
    }

    public void setUnread(int unread) {
        mUnread = unread;
        if (mUnread >= 100) {
            mUnreadView.setText("99+");
        } else {
            mUnreadView.setText(String.valueOf(unread));
        }
        shouldUnreadViewDisplay();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth = measureWidth(widthMeasureSpec);
        int measureHeight = measureHeight(heightMeasureSpec);
        // 计算自定义的ViewGroup中所有子控件的大小
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        // 设置自定义的控件MyViewGroup的大小
        setMeasuredDimension(measureWidth, measureHeight);
    }

    private int measureWidth(int pWidthMeasureSpec) {
        int result = 0;
        int widthMode = MeasureSpec.getMode(pWidthMeasureSpec);// 得到模式
        int widthSize = MeasureSpec.getSize(pWidthMeasureSpec);// 得到尺寸

        switch (widthMode) {
            /**
             * mode共有三种情况，取值分别为MeasureSpec.UNSPECIFIED, MeasureSpec.EXACTLY,
             * MeasureSpec.AT_MOST。
             *
             *
             * MeasureSpec.EXACTLY是精确尺寸，
             * 当我们将控件的layout_width或layout_height指定为具体数值时如andorid
             * :layout_width="50dip"，或者为FILL_PARENT是，都是控件大小已经确定的情况，都是精确尺寸。
             *
             *
             * MeasureSpec.AT_MOST是最大尺寸，
             * 当控件的layout_width或layout_height指定为WRAP_CONTENT时
             * ，控件大小一般随着控件的子空间或内容进行变化，此时控件尺寸只要不超过父控件允许的最大尺寸即可
             * 。因此，此时的mode是AT_MOST，size给出了父控件允许的最大尺寸。
             *
             *
             * MeasureSpec.UNSPECIFIED是未指定尺寸，这种情况不多，一般都是父控件是AdapterView，
             * 通过measure方法传入的模式。
             */
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                result = widthSize;
                break;
        }
        return result;
    }

    private int measureHeight(int pHeightMeasureSpec) {
        int result = 0;

        int heightMode = MeasureSpec.getMode(pHeightMeasureSpec);
        int heightSize = MeasureSpec.getSize(pHeightMeasureSpec);

        switch (heightMode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                result = heightSize;
                break;
        }
        return result;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l;
        int height = b - t;
        Log.v(TAG, "onLayout changed=" + changed + ", l=" + l + ", t=" + t + ", r=" + r + ", b=" + b);
        Log.v(TAG, "onLayout width=" + width + ", height=" + height);
        layoutIcon(width, height);
        layoutUnread(width, height);
    }

    private void layoutIcon(int width, int height) {
        int icWidth = mIconView.getMeasuredWidth();
        int icHeight = mIconView.getMeasuredHeight();

        int l = (int)((width - icWidth) / 2.0f);
        int t = (int)((height - icHeight) / 2.0f);
        int r = l + icWidth;
        int b = t + icHeight;

        Log.v(TAG, "layoutIcon width=" + width + ", height=" + height + ", icWidth=" + icWidth + ", icHeight=" + icHeight);
        Log.v(TAG, "layoutIcon l=" + l + ", t=" + t + ", r=" + r + ", b=" + b);
        mIconView.layout(l, t, r, b);
    }

    private void layoutUnread(int width, int height) {

        int unreadWidth = mUnreadView.getMeasuredWidth();
        int unreadHeight = mUnreadView.getMeasuredHeight();

        int icRight = mIconView.getRight();
        int icTop = mIconView.getTop();

        int l = (int)(icRight - unreadWidth / 2.0f);
        int t = (int)(icTop - unreadHeight / 2.0f);
        int r = l + unreadWidth;
        int b = t + unreadHeight;

        Log.v(TAG, "layoutUnread icRight=" + icRight + ", icTop=" + icTop + ", unreadWidth=" + unreadWidth + ", unreadHeight=" + unreadHeight);
        Log.v(TAG, "layoutUnread l=" + l + ", t=" + t + ", r=" + r + ", b=" + b);
        mUnreadView.layout(l, t, r, b);
    }

    @Override
    public void onClick(View v) {
        //TODO 跳转会话入口页面
        if (mUnread <= 0) {
            setUnread(8);
        } else if (0 < mUnread && mUnread < 10) {
            setUnread(88);
        } else if (mUnread >= 88 & mUnread < 100) {
            setUnread(100);
        } else {
            setUnread(0);
        }
    }
}
