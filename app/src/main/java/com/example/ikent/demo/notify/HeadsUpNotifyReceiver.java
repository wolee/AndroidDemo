package com.example.ikent.demo.notify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.example.ikent.BuildConfig;

import java.util.HashMap;

/**
 * Created by lijianfeng on 16/5/16 下午6:17.
 */
public class HeadsUpNotifyReceiver extends BroadcastReceiver {
    public static final String TAG = "HeadsUpNotifyReceiver";

    public static final String ACTION_FULL_SCREEN = BuildConfig.APPLICATION_ID + ".FULL_SCREEN_RECEIVER";
    public static final String ACTION_DELETE = BuildConfig.APPLICATION_ID + ".DELETE_RECEIVER";


    public static final String KEY_NOTIFY_TAG = "key_notify_tag";
    public static final String KEY_NOTIFY_ID = "key_notify_id";

    public static final String KEY_NOTIFY_INTENT = "key_notify_intent";
    public static final String KEY_NOTIFY_TITLE = "key_notify_title";
    public static final String KEY_NOTIFY_CONTENT = "key_notify_content";
    public static final String KEY_NOTIFY_TICKER = "key_notify_ticker";


    private static final int SHOW_TIME = 3 * 1000;

    private Handler mHander = new Handler(Looper.getMainLooper());
    private HashMap<String, Runnable> mTaskMap = new HashMap<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "action=" + action);
        if (TextUtils.equals(action, ACTION_FULL_SCREEN) || TextUtils.equals(action, ACTION_DELETE)) {
            String notifyTag = intent.getStringExtra(KEY_NOTIFY_TAG);
            int notifyId = intent.getIntExtra(KEY_NOTIFY_ID, 0);
            String key = notifyKey(notifyTag, notifyId);
            Runnable runnable = mTaskMap.remove(key);
            if (runnable != null) {
                mHander.removeCallbacks(runnable);
            }
            if (TextUtils.equals(action, ACTION_FULL_SCREEN)) {
                Intent notifyIntent = intent.getParcelableExtra(KEY_NOTIFY_INTENT);
                String title = intent.getStringExtra(KEY_NOTIFY_TITLE);
                String ticker = intent.getStringExtra(KEY_NOTIFY_TICKER);
                String content = intent.getStringExtra(KEY_NOTIFY_CONTENT);
                runnable = new NotifyRunner(context, notifyIntent, title, ticker, content, notifyTag, notifyId);
                mTaskMap.put(key, runnable);
                mHander.postDelayed(runnable, SHOW_TIME);
            }
        }

    }

    private String notifyKey(String tag, int id) {
        String key = id + "_";
        if (!TextUtils.isEmpty(tag)) {
            key += tag;
        }
        return key;
    }

    private class NotifyRunner implements Runnable {
        Context context;
        Intent intent;
        String title;
        String ticker;
        String content;
        String notifyTag;
        int notifyId;

        public NotifyRunner(Context context, Intent intent, String title, String ticker, String content, String notifyTag, int notifyId) {
            this.context = context;
            this.intent = intent;
            this.title = title;
            this.ticker = ticker;
            this.content = content;
            this.notifyTag = notifyTag;
            this.notifyId = notifyId;
        }

        @Override
        public void run() {
            NotifyUtil.showNotify(context, title, ticker, content, intent, notifyTag, notifyId, null, null, false);
            String key = notifyKey(notifyTag, notifyId);
            mTaskMap.remove(key);
        }
    }
}
