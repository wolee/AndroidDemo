package com.example.ikent.demo.notify;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.example.ikent.BaseActivity;
import com.example.ikent.R;

/**
 * Created by lijianfeng on 16/5/16 下午5:32.
 */
public class NotifyActivity extends BaseActivity {
    public static final String TAG = "NotifyActivity";

    public static final int NOTIFICATION_ID = 1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);
    }


    public void sendNotification(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://developer.android.com/reference/android/app/Notification.html"));
        String title = "BasicNotifications Sample";
        String content = "Time to learn about notifications!";
        String subText = "Tap to view documentation about notifications.";
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        String tag = "tag";
        int notifyId = NOTIFICATION_ID;
        NotifyUtil.showNotify(this, title, content, content, intent, tag, notifyId, largeIcon, null, true);
    }
}
