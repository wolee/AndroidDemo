package com.example.ikent.demo;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.ikent.R;
import com.kent.widget.refreshlayout.CoolSwipeRefreshLayout;

import java.util.Random;

public class CoolSwipeRefreshActivity extends AppCompatActivity {

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private CoolSwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mListView;
    private ArrayAdapter<String> mListAdapter;

    private int mIndex = 0;
    private int mCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cool_swipe_refresh);

        mSwipeRefreshLayout = (CoolSwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mListView = (ListView) findViewById(R.id.refresh_listview);

        mListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1) {
            @Override
            public int getCount() {
                return mCount;
            }

            @Override
            public String getItem(int position) {
                return "List Item " + (mIndex + position);
            }

            @Override
            public int getPosition(String item) {
                return 0;
            }
        };

        mListView.setAdapter(mListAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                refresh();
            }
        });
    }
    private void refresh() {
        new RefreshBackgroundTask().execute();
    }

    private void loadMore() {
        new LoadMoreBackgroundTask().execute();
    }


    private void onRefreshComplete(int index, int count) {
        mIndex = index;
        mCount = count;
        mListAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void onLoadMoreComplete(int addCount) {
        mCount += addCount;
        mListAdapter.notifyDataSetChanged();
        //TODO 暂停上拉刷新
    }


    private class RefreshBackgroundTask extends AsyncTask<Void, Void, Integer> {
        static final int TASK_DURATION = 3 * 1000; // 3 seconds
        final Random mRandom = new Random();
        @Override
        protected Integer doInBackground(Void... params) {
            try {
                Thread.sleep(TASK_DURATION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return mRandom.nextInt(99999);
        }

        @Override
        protected void onPostExecute(Integer index) {
            super.onPostExecute(index);
            onRefreshComplete(index, mRandom.nextInt(50));
        }
    }

    private class LoadMoreBackgroundTask extends AsyncTask<Void, Void, Void> {
        static final int TASK_DURATION = 3 * 1000; // 3 seconds
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(TASK_DURATION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            onLoadMoreComplete(20);
        }
    }
}
