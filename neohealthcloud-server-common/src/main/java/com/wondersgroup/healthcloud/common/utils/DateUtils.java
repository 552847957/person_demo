package com.wondersgroup.healthcloud.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * Created by longshasha on 16/3/10.
 */
public class DateUtils {

    public final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public final static SimpleDateFormat sdf_day = new SimpleDateFormat("yyyy-MM-dd");
    public final static SimpleDateFormat sdf_day_hour = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public final static SimpleDateFormat sdf_hour = new SimpleDateFormat("HH:mm");

    public static String getWeekOfDate(Date date) {
        String[] weekOfDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar calendar = Calendar.getInstance();
        if(date != null){
            calendar.setTime(date);
        }
        int w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0){
            w = 0;
        }
        return weekOfDays[w];
    }

    /**
     * 根据日期返回时上午还是下午
     * @param date
     * @return
     */
    public static String getAPM(Date date) {
        String str = "";
        Calendar calendar = Calendar.getInstance();
        if(date != null){
            calendar.setTime(date);
        }
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if(hour<12){
            str = "上午";
        }else{
            str = "下午";
        }
        return str;
    }

    /**
     * 获取n周后的一天
     * @param date
     * @param n
     * @return
     */
    public static Date addWeekDay(Date date, int n) {
        Calendar calendar = Calendar.getInstance();
        if(date != null){
            calendar.setTime(date);
        }
        calendar.add(calendar.DATE,7*n);
        Date result = calendar.getTime();
        return result;
    }

    /**
     * 获取n月后的一天
     * @param date
     * @param n
     * @return
     */
    public static Date addMonthDay(Date date, int n) {
        Calendar calendar = Calendar.getInstance();
        if(date != null){
            calendar.setTime(date);
        }
        calendar.add(calendar.MONTH,n);
        Date result = calendar.getTime();
        return result;
    }

    /**
     * 获取n天后的一天
     * @param date
     * @param n
     * @return
     */
    public static Date addDay(Date date, int n) {
        Calendar calendar = Calendar.getInstance();
        if(date != null){
            calendar.setTime(date);
        }
        calendar.add(calendar.DATE,n);
        Date result = calendar.getTime();
        return result;
    }

    /**
     * 比较两个日期的大小
     * @param date1
     * @param date2
     * @return
     */
    public static long compareDate(Date date1, Date date2) {
        long date1l = date1.getTime();
        long date2l = date2.getTime();

        return (date1l-date2l);
    }
    /**
     * 计算两个日期之间相差的天数
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数
     */
    public static int daysBetween(Date smdate,Date bdate)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days=(time2-time1)/(1000*3600*24);

        return Integer.parseInt(String.valueOf(between_days));
    }
    
    public static int getAgeByBirthday(Date birthday) {
        Calendar cal = Calendar.getInstance();

        if (cal.before(birthday)) {
            throw new IllegalArgumentException("出生日期小于当前时间");
        }

        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH) + 1;
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

        cal.setTime(birthday);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH) + 1;
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                }
            } else {
                age--;
            }
        }
        return age;
    }

    public static Date parseString(String str){
        try {
            return sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Date parseString(String str,String format){
        try {
            return new SimpleDateFormat(format).parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 转换date to string
     * 一分钟内转为
     * @param date
     * @return
     */
    public static String formatDate2Custom(Date date){
        int defTime = (int) ((new Date().getTime() - date.getTime())/1000);
        if (defTime < 0){
            return sdf.format(date);
        }
        String create_day = sdf_day.format(date);
        if (defTime < 60) {
            return "刚刚";
        }else if (defTime < 3600){
            return defTime/60+"分钟前";
        }else if (defTime < 86400){
            return defTime/3600+"小时前";
        }else {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            String last_1_day = sdf_day.format(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            String last_2_day = sdf_day.format(calendar.getTime());
            if (last_1_day.equals(create_day)){
                return "昨天"+sdf_hour.format(date);
            }else if (last_2_day.equals(create_day)){
                return "前天"+sdf_hour.format(date);
            }
        }
        return sdf_day_hour.format(date);
    }

    public static String getTodayBegin(){
        return sdf_day.format(new Date()) + " 00:00:00";
    }
}
