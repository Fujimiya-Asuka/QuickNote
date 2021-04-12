package com.asuka.quicknote.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class MyNotificationManager {

    private Context mContext;

    public MyNotificationManager(Context context) {
        mContext=context;
    }

    public NotificationManager getNotificationManager(){
        NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        //安卓8.0及以上需要设置通知渠道
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            //Build.VERSION.SDK_INT -->当前系统版本
            //Build.VERSION_CODES.O --> API26 安卓8.0
            NotificationChannel notificationChannel = new NotificationChannel("1","普通通知",NotificationManager.IMPORTANCE_HIGH);
            //                    id：channelID，自定义，保证全局唯一性
            //                    name：channelName，自定义，给用户展示，便于用户理解该渠道通知用途
            //                    重要等级：渠道重要性级别影响该渠道中所有通知的显示，在创建NotificationChannel对象的构造方法中必须要指定级别
            //                    紧急(发出声音并显示为提醒通知)	    IMPORTANCE_HIGH
            //                    高(发出声音)	                IMPORTANCE_DEFAULT
            //                    中等(没有声音)	                IMPORTANCE_LOW
            //                    低(无声音并且不会出现在状态栏中)	IMPORTANCE_MIN
            notificationChannel.enableVibration(true); //开启震动
            notificationChannel.enableLights(true); //开启呼吸灯
            manager.createNotificationChannel(notificationChannel);
        }
        return manager;
    }

}
