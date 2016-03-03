package com.kent.widget.refreshlayout;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by lijianfeng on 2016/3/3 下午 4:50 .
 */
public class CoolSwipeRefreshLayout extends SwipeRefreshLayout implements SwipeRefreshLayout.OnRefreshListener {
    public static final String TAG = "CoolSwipeRefreshLayout";
    private OnLoadMoreListener mOnLoadMoreListener;
    private OnRefreshListener mOnRefreshListener;

    public CoolSwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public CoolSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        super.setOnRefreshListener(this);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener l) {
        this.mOnLoadMoreListener = l;
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mOnRefreshListener = listener;
    }

    @Override
    public void onRefresh() {
        if (mOnRefreshListener != null) {
            mOnRefreshListener.onRefresh();
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public interface OnRefreshListener extends SwipeRefreshLayout.OnRefreshListener {

    }
}
