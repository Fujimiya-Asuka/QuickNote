package com.asuka.quicknote.utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.asuka.quicknote.R;

import java.util.Calendar;

public class NotifyService extends Service {
    private final String TAG ="NotifyService";
    private final Context mContext = NotifyService.this;

    public NotifyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        //        在服务中处理耗时任务要放在子线程中处理,默认在主线程中处理,处理时长超过5秒会被判定ANR
        new Thread(new Runnable() {
            @Override
            public void run() {
                //使用前台服务
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationChannel notificationChannel = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationChannel = new NotificationChannel("NotifyService","前台服务", NotificationManager.IMPORTANCE_LOW);
                    manager.createNotificationChannel(notificationChannel);
                }
                PendingIntent pendingIntent = PendingIntent.getActivity(NotifyService.this, 0,new Intent(), 0);
                Notification.Builder builder = new Notification.Builder(NotifyService.this,"NotifyService");
                builder.setContentTitle("QuickNote提醒服务");
                builder.setSmallIcon(R.drawable.ic_launcher_foreground);
                builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher_background));
                //以上4行必须
                builder.setContentIntent(pendingIntent);
                //开启前台服务
                startForeground(10000,builder.build());
                Intent intent = new Intent(mContext, NotifyService.class);
                intent.putExtra("todoId",999);
                PendingIntent pendingIntent1 = PendingIntent.getService(mContext,1,intent,0);
                AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP,Calendar.getInstance().getTimeInMillis()+60000,pendingIntent1);

            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int todoId = intent.getIntExtra("todoId",0);
        Log.d(TAG, "onStartCommand: "+ Calendar.getInstance().getTime().toString()+"todoID："+todoId);
        NotificationManager notificationManager = new MyNotificationManager(NotifyService.this).getNotificationManager();
        NotificationCompat.Builder notification = new MyNotification(NotifyService.this).getNotification();
        notificationManager.notify(1,notification.build());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}