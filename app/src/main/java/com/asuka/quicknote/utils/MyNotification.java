package com.asuka.quicknote.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.asuka.quicknote.R;
import com.asuka.quicknote.activity.ToDoViewActivity;


public class MyNotification {
    private Context mContext;

    public MyNotification(Context context) {
        mContext=context;
    }

    public NotificationCompat.Builder getNotification() {
        Intent intent = new Intent(mContext, ToDoViewActivity.class);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(mContext, "1");
        //设置通知标题
        notification.setContentTitle("通知标题");
        //设置通知内容
        notification.setContentText("点击转跳到,可以查看展开内容");
        //设置通知图标（状态栏小图标）
        notification.setSmallIcon(R.drawable.ic_launcher_background);
        //设置大图标（下拉显示的大图标）
        notification.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_launcher_background));
        //点击通知后自动消除
        notification.setAutoCancel(true);
        //设置可展开的文字
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle().bigText("这是可以展开的内容这是可以展开的内容这是可以展开的内容这是可以展开的内容这是可以展开的内容这是可以展开的内容这是可以展开的内容这是可以展开的内容这是可以展开的内容这是可以展开的内容这是可以展开的内容");
        //设置展开的文字或者图片
        notification.setStyle(bigTextStyle);
        //根据系统默认设置，铃声，震动，通知效果.....
        notification.setDefaults(NotificationCompat.DEFAULT_ALL);
        //设置通知的意图
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
        notification.setContentIntent(pendingIntent);
        return notification;
    }

}
