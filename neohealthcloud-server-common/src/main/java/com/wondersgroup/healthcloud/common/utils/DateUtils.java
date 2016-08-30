package com.wondersgroup.healthcloud.common.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by longshasha on 16/3/10.
 */
public class DateUtils {

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
}
