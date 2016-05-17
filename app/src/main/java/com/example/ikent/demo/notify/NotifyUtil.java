package com.example.ikent.demo.notify;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;

import com.example.ikent.R;

/**
 * Created by lijianfeng on 16/5/16 下午6:20.
 */
public class NotifyUtil {
    public static final String TAG = "NotifyUtil";

    public static int getNotifySmallIconId(){
        return R.mipmap.ic_launcher;
    }

    public static void showNotify(Context context, String title, String ticker, String content, Intent intent, String notifyTag,
                                  int notifyId, Bitmap largeIcon, Bitmap bigPicture, boolean showHandsUp) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(getNotifySmallIconId())
                .setContentTitle(title)
                .setTicker(ticker)
                .setContentText(content);
        if (largeIcon != null) {
            builder.setLargeIcon(largeIcon);
        }

        if (bigPicture != null) {
            NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
            style.setBigContentTitle(title);
            style.setSummaryText(content);
            style.bigPicture(bigPicture);
            if (largeIcon != null) {
                style.bigLargeIcon(largeIcon);
            }
            builder.setStyle(style);
        }

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, notifyId, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        builder.setAutoCancel(true);

        if (showHandsUp) {
            Intent fullScreenIntent = new Intent(HeadsUpNotifyReceiver.ACTION_FULL_SCREEN);
            fullScreenIntent.putExtra(HeadsUpNotifyReceiver.KEY_NOTIFY_TAG, notifyTag);
            fullScreenIntent.putExtra(HeadsUpNotifyReceiver.KEY_NOTIFY_ID, notifyId);
            fullScreenIntent.putExtra(HeadsUpNotifyReceiver.KEY_NOTIFY_TITLE, title);
            fullScreenIntent.putExtra(HeadsUpNotifyReceiver.KEY_NOTIFY_CONTENT, content);
            fullScreenIntent.putExtra(HeadsUpNotifyReceiver.KEY_NOTIFY_TICKER, ticker);
            fullScreenIntent.putExtra(HeadsUpNotifyReceiver.KEY_NOTIFY_INTENT, intent);

            PendingIntent fullScreenPendingIntent = PendingIntent.getBroadcast(context, 1, fullScreenIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setFullScreenIntent(fullScreenPendingIntent, true);
//            context.sendBroadcast(fullScreenIntent);

            Intent deleteIntent = new Intent(HeadsUpNotifyReceiver.ACTION_DELETE);
            deleteIntent.putExtra(HeadsUpNotifyReceiver.KEY_NOTIFY_TAG, notifyTag);
            deleteIntent.putExtra(HeadsUpNotifyReceiver.KEY_NOTIFY_ID, notifyId);

            PendingIntent deletePendingIntent = PendingIntent.getBroadcast(context, 0, deleteIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setDeleteIntent(deletePendingIntent);
        }

        builder.setDefaults(Notification.DEFAULT_ALL);
        NotificationManagerCompat mgr = NotificationManagerCompat.from(context);
        Notification notification = builder.build();
        if (TextUtils.isEmpty(notifyTag)) {
            mgr.notify(notifyId, notification);
        } else {
            mgr.notify(notifyTag, notifyId, notification);
        }
    }
}
