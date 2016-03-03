package com.example.ikent.appdemo;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.LinkedList;


/**
 * Created by lijianfeng on 2016/2/25 下午 7:56 .
 */
public class PopToast {
    public static final String TAG = "PopToast";
    private static PopToastManager mManager;

    private int mDuration;
    private LinearLayout mView;
    private OnClickListener mListener;
    private GestureDetector mDetector;

    public static PopToast make(Context context, CharSequence text, int duration, OnClickListener listener) {
        final PopToast toast = new PopToast(context);
        TextView textView = (TextView) View.inflate(context, R.layout.layout_push_toast, null);
        textView.setText(text);
        toast.setDuration(duration);
        toast.setView(textView);
        toast.setOnClickListener(listener);
        return toast;
    }

    public PopToast(Context context) {
        mView = new LinearLayout(context);
        mView.setGravity(Gravity.CENTER);
        mView.setBackgroundColor(0xffffffff);
        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mDetector.onTouchEvent(event);
            }
        });

        mDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (mListener != null) {
                    mListener.onClick(PopToast.this);
                    mManager.showNext();
                }
                return false;
            }
        });

        if (mManager == null) {
            mManager = new PopToastManager();
        }
        mDuration = 2000;
    }

    public void setBackgroundColor(int color) {
        mView.setBackgroundColor(color);
    }

    public void setBackgroundResource(int resid) {
        mView.setBackgroundResource(resid);
    }

    public void setView(View view) {
        mView.removeAllViews();
        mView.addView(view);
    }

    public void setOnClickListener(OnClickListener l) {
        mListener = l;
    }

    public View getView() {
        return mView;
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }

    public int getDuration() {
        return mDuration;
    }

    public void show() {
        mManager.add(this);
    }

    public void showRightNow() {
        mManager.addAfterClear(this);
    }

    private static class PopToastManager implements BaseActivity.IApplicationVisibileChangeListener {
        private final LinkedList<PopToast> mToasts = new LinkedList<PopToast>();
        private final Handler mHandler = new Handler(Looper.getMainLooper());
        private final WindowManager.LayoutParams mParams;
        private PopToast mShowingToast = null;
        private WindowManager mWM;

        private final Runnable mShow = new Runnable() {
            @Override
            public void run() {
                hideToast();
                if (mToasts.isEmpty()) {
                    mShowingToast = null;
                    return;
                }
                mShowingToast = mToasts.remove();
                showToast();
                mHandler.postDelayed(mShow, mShowingToast.getDuration());
            }
        };

        private final Runnable mVisibleRunner = new Runnable() {
            @Override
            public void run() {
                if (!BaseActivity.isApplicationVisible()) {
                    clearQueue();
                    showNext();
                }
            }
        };

        public PopToastManager() {
            mParams = new WindowManager.LayoutParams();
            mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            mParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            mParams.format = PixelFormat.TRANSLUCENT;
            mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            mParams.setTitle("PopToast");
            mParams.gravity = Gravity.TOP;
            mParams.x = 0;
            mParams.y = 0;

            BaseActivity.addApplicationVisibileChangeListener(this);
        }

        public void add(PopToast toast) {
            if (!BaseActivity.isApplicationVisible()) {
                Log.e(TAG, "add fail, mAppVisble is " + BaseActivity.isApplicationVisible());
                return;
            }
            mToasts.add(toast);
            if (mShowingToast == null) {
                mHandler.post(mShow);
            }
        }

        public void showNext() {
            mHandler.removeCallbacks(mShow);
            mHandler.postDelayed(mShow, 100);
        }

        public void addAfterClear(PopToast toast) {
            if (!BaseActivity.isApplicationVisible()) {
                Log.e(TAG, "addAfterClear fail, mAppVisble is " + BaseActivity.isApplicationVisible());
                return;
            }
            clearQueue();
            mToasts.add(toast);
            mHandler.post(mShow);
        }

        private void showToast() {
            if (mShowingToast == null || mShowingToast.getView() == null) {
                return;
            }
            Context context = mShowingToast.getView().getContext().getApplicationContext();
            if (context == null) {
                context = mShowingToast.getView().getContext();
            }
            mWM = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);

            if (mShowingToast.getView().getParent() != null) {
                mWM.removeView(mShowingToast.getView());
            }
            mWM.addView(mShowingToast.getView(), mParams);
        }

        private void hideToast() {
            if (mShowingToast != null && mShowingToast.getView() != null && mShowingToast.getView().getParent() != null) {
                mWM.removeView(mShowingToast.getView());
            }
            mShowingToast = null;
        }

        private void clearQueue() {
            mToasts.clear();
            mHandler.removeCallbacks(mShow);
        }

        @Override
        public void onApplicationVisbleChange(boolean visible) {
            mHandler.removeCallbacks(mVisibleRunner);
            mHandler.postDelayed(mVisibleRunner, 200);
        }
    }

    public interface OnClickListener {
        void onClick(PopToast toast);
    }
}
