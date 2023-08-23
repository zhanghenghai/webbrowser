package com.one.browser.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.one.browser.MainActivity;
import com.one.browser.R;

/**
 * @author 18517
 */
public class NotificationUtil {
    private static final String MESSAGES_CHANNEL = "messages";

    private NotificationCompat.Builder builder;

    private Context context;

    public NotificationUtil(Context context) {
        this.context = context;
    }


    public void addNotification(String title, String content) {


        // 创建自定义布局
        RemoteViews customLayout = new RemoteViews(context.getPackageName(), R.layout.notification_contentview);

        // 添加单击事件
        Intent intent = new Intent(context, MainActivity.CustomButtonReceiver.class);
        intent.setAction("com.one.custom_button_click");

        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        }

        builder = new NotificationCompat.Builder(context, MESSAGES_CHANNEL)
                //设置图标
                .setSmallIcon(R.drawable.ic_launcher_background)
                //设置标题
                .setContentTitle("下载")
                //内容
                .setContentText("下载")
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.add_line,"暂停", pendingIntent)
                .setAutoCancel(true);
    }

    public void notified() {
        notify(builder);
    }

    private void notify(NotificationCompat.Builder builder) {
        final int NEW_MESSAGE_ID = 0;
        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NEW_MESSAGE_ID, notification);
    }

    public synchronized void schedule(int scheduleMax,int size){

        Log.i("TAG", "schedule: ");
        builder.setProgress(scheduleMax, size, false);
        notify();
    }


}
