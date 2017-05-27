/**
 *
 */
package com.wondersgroup.healthcloud.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author zhangzhixiu
 */
public class DateFormatter {

    private static final String dateTimePattern = "yyyy-MM-dd HH:mm:ss";

    private static final String dateTime2Pattern = "yyyy-MM-dd HH:mm";

    private static final String yearPattern = "yyyy";

    private static final String monthDayPattern = "MM-dd";

    private static final String monthTimePattern = "MM-dd HH:mm";

    private static final String datePattern = "yyyy-MM-dd";

    private static final String scheduleDatePattern = "yyyy年MM月dd日";

    private static final String orderTimePattern = "yyyy年MM月dd日 HH:mm";

    private static final String datePatternY = "yy-MM-dd";

    private static final String idCardDatePattern = "yyyyMMdd";

    private static final String questionDatePattern = "yyyy-MM-dd HH:mm";

    private static final String hourPattern = "HH:mm";

    public static String format(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        DateFormat df = createFromPattern(pattern);
        return df.format(date);
    }

    public static Date parse(String dateStr, String pattern) {
        if (dateStr == null) {
            return null;
        }
        DateFormat df = createFromPattern(pattern);
        try {
            return df.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static DateFormat createFromPattern(String pattern) {
        return new SimpleDateFormat(pattern);
    }

    public static String dateTimeFormat(Date date) {
        return format(date, dateTimePattern);
    }

    public static String dateTim2eFormat(Date date) {
        return format(date, dateTime2Pattern);
    }

    public static String monthDayFormat(Date date) {
        return format(date, monthDayPattern);
    }

    public static String yearFormat(Date date) {
        return format(date, yearPattern);
    }

    public static String getCurrentYear() {
        return format(new Date(), yearPattern);
    }

    public static String dateFormat(Date date) {
        return format(date, datePattern);
    }

    public static String scheduleDateFormat(Date date) {
        return format(date, scheduleDatePattern);
    }

    public static String orderTimeFormat(Date date) {
        return format(date, orderTimePattern);
    }

    public static String dateFormatY(Date date) {
        return format(date, datePatternY);
    }

    public static String questionDateFormat(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
            return format(date, monthTimePattern);
        } else {
            return format(date, questionDatePattern);
        }
    }

    public static String hourDateFormat(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return format(date, hourPattern);
    }

    public static String questionListDateFormat(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
            return format(date, monthDayPattern);
        } else {
            return format(date, datePattern);
        }
    }

    public static String questionDateFormat(String date) {
        if (null == date) {
            return null;
        }
        return questionDateFormat(parseDateTime(date));
    }


    public static Date parseDateTime(String date) {
        return parse(date, dateTimePattern);
    }

    public static Date parseDate(String date) {
        return parse(date, datePattern);
    }

    public static Date parseIdCardDate(String date) {
        return parse(date, idCardDatePattern);
    }
    
    /**
     * 两个日期之间相差的天数
     * @param begin
     * @param end
     * @param type
     * @return long
     */
    public static long dateMinusDateForDays(String begin, String end, String type) {
        long quot = 0;
        SimpleDateFormat ft = new SimpleDateFormat(type);
        try {
            Date date1 = ft.parse(begin);
            Date date2 = ft.parse(end);
            quot = date2.getTime() - date1.getTime();
            quot = quot / 1000 / 60 / 60 / 24;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return quot < 0 ? quot * -1 : quot;
    }

}
