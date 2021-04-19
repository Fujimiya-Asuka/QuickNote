package com.asuka.quicknote.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.util.Date;


public  class  AlarmUtil {

    /**
     * @param context
     * @param todoId
     * 待办ID
     * @param notifyTime
     * 设置闹钟的日历，要先设置好时间
     */
    public static void setAlarm(Context context, int todoId, Date notifyTime){
        Intent intent = new Intent(context, NotifyService.class);
        intent.putExtra("todoId",todoId);
        //todoId用于唯一标识定时任务,不可重复，否则定时任务将会被覆盖
        PendingIntent pi = PendingIntent.getService(context,todoId,intent,0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,notifyTime.getTime(),pi);
    }

}
