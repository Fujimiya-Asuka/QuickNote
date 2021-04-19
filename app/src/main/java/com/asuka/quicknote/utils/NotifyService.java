package com.asuka.quicknote.utils;

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

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.asuka.quicknote.R;
import com.asuka.quicknote.domain.ToDo;
import com.asuka.quicknote.utils.db.ToDoCRUD;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NotifyService extends Service {
    private final String TAG ="NotifyService";
    private final Context mContext = NotifyService.this;

    public NotifyService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
                useForegroundService();
                //所有未提醒的待办设置提醒
                initAllNotifyToDo();
            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final int todoId = intent.getIntExtra("todoId",0);
        //如果todoId有效，弹出通知
        if (todoId>0){
            Log.d(TAG, "通知："+ Calendar.getInstance().getTime().toString()+"   todoID："+todoId);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    notifyToDo(todoId);
                }
            }).start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



    /**
     * 开启前台服务
     */
    private void useForegroundService(){
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel notificationChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel("NotifyService","前台服务", NotificationManager.IMPORTANCE_LOW);
            manager.createNotificationChannel(notificationChannel);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(NotifyService.this, 0,new Intent(), 0);
        Notification.Builder builder = new Notification.Builder(NotifyService.this,"NotifyService");
        builder.setContentTitle("QuickNote提醒服务");
        //通知栏小图标
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        //下拉通知栏大图标
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher_background));
        //以上4行必须
        builder.setContentIntent(pendingIntent);
        //开启前台服务，设置10000，保证优先级，尽量不被回收
        startForeground(10000,builder.build());
    }

    /**
     * 弹出通知的方法
     * @param todoId
     * 以todoId为参数，弹出对应待办的通知
     */
    private void notifyToDo(int todoId){
        NotificationManager notificationManager = new MyNotificationManager(NotifyService.this).getNotificationManager();
        NotificationCompat.Builder notification = new MyNotification(NotifyService.this).getNotification(new ToDoCRUD(mContext).getTodo(todoId));
        notificationManager.notify(todoId,notification.build());
        //设置对应待办状态为已完成
        new ToDoCRUD(mContext).setToDoNotify(todoId,0);
    }

    /**
     * 加载所有需要提醒的未提醒待办
     */
    private void initAllNotifyToDo(){
        List<ToDo> allNotNotifyTodo = new ToDoCRUD(mContext).getAllNotNotifyTodo();
        for (ToDo toDo : allNotNotifyTodo) {
            Date date = new TimeUtil(toDo.getTime()).getDate();
            Log.d(TAG, "initAllNotifyToDo: "+date.toString()+toDo.getId());
            AlarmUtil.setAlarm(mContext,(int)toDo.getId(),date);
        }
    }

}