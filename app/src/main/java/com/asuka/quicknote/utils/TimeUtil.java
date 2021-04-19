package com.asuka.quicknote.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
    private String timeString;
    private Date date;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");



    public TimeUtil(Date date) {
        this.timeString = simpleDateFormat.format(date);
    }

    public TimeUtil(String time) {
        Date date = null;
        try {
            date = simpleDateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.date = date;
    }

    /**
     * 获取格式化后的时间字符串
     * @return
     * 指定格式的时间字符串
     */
    public String getTimeString() {
        return timeString;
    }

    /**
     * 获取Data对象
     * @return
     *
     */
    public Date getDate() {
        return date;
    }

}
