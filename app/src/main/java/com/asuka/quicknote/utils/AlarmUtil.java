package com.asuka.quicknote.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.asuka.quicknote.activity.ToDoViewActivity;
import java.util.Calendar;


public  class  AlarmUtil {

    /**
     * @param context
     * @param todoId
     * 待办ID
     * @param calendar
     * 设置闹钟的日历，要先设置好时间
     */
    public static void setAlarm(Context context,int todoId,Calendar calendar){
        Intent intent=new Intent(context, NotifyService.class);
        //todoId用于唯一标识定时任务,不可重复，否则定时任务将会被覆盖
        PendingIntent pendingIntent = PendingIntent.getActivity(context,todoId,intent,0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
    }

}
