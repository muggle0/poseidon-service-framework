package com.muggle.psf.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author: muggle
 * @Date: 2020/7/29
 **/
public class DateUtils {
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String DATE_PATTERN1 = "yyyy_MM_dd";
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME_PATTERN = "HH:mm:ss";

    public DateUtils() {
    }

    public static String format(final long millis, final String pattern) {
        return format(new Date(millis), pattern);
    }

    public static long getDateMinus(final String startDate, final String endDate, final String pattern) {
        final SimpleDateFormat myFormatter = new SimpleDateFormat(pattern);

        try {
            final Date date = myFormatter.parse(startDate);
            final Date mydate = myFormatter.parse(endDate);
            final long day = (mydate.getTime() - date.getTime()) / 60000L;
            return day;
        } catch (final ParseException var8) {
            var8.printStackTrace();
            return 0L;
        }
    }

    public static String addDay(final String s, final int n) {
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            final Calendar cd = Calendar.getInstance();
            cd.setTime(sdf.parse(s));
            cd.add(5, n);
            return sdf.format(cd.getTime());
        } catch (final Exception var4) {
            return null;
        }
    }

    public static int getDaysByYearMonth(final String dateString) {
        final int year = Integer.parseInt(dateString.substring(0, 4));
        final int month = Integer.parseInt(dateString.substring(5, 7));
        final Calendar a = Calendar.getInstance();
        a.set(1, year);
        a.set(2, month - 1);
        a.set(5, 1);
        a.roll(5, -1);
        final int maxDate = a.get(5);
        return maxDate;
    }

    public static String getLastDayOfMonth(final int year, final int month) {
        final Calendar cal = Calendar.getInstance();
        cal.set(1, year);
        cal.set(2, month - 1);
        final int lastDay = cal.getActualMaximum(5);
        cal.set(5, lastDay);
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String lastDayOfMonth = sdf.format(cal.getTime());
        return lastDayOfMonth;
    }

    public static Date ConverToDate(final String strDate) {
        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        try {
            return df.parse(strDate);
        } catch (final ParseException var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public static List<Date> findDates(final String startTime, final String endTime) {
        final Date startDate = ConverToDate(startTime);
        final Date endDate = ConverToDate(endTime);
        final List<Date> lDate = new ArrayList();
        lDate.add(startDate);
        final Calendar calBegin = Calendar.getInstance();
        calBegin.setTime(startDate);
        final Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(endDate);

        while (endDate.after(calBegin.getTime())) {
            calBegin.add(5, 1);
            lDate.add(calBegin.getTime());
        }

        return lDate;
    }

    public static int getWeek(final Date date) {
        final int[] weeks = new int[]{0, 1, 2, 3, 4, 5, 6};
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week_index = cal.get(7) - 1;
        if (week_index < 0) {
            week_index = 0;
        }

        return weeks[week_index];
    }

    public static String getTimeDifference(final String startTime, final String endTime) {
        final SimpleDateFormat dfs = new SimpleDateFormat("HH:mm:ss");
        long between = 0L;

        try {
            final Date begin = dfs.parse(startTime);
            final Date end = dfs.parse(endTime);
            between = end.getTime() - begin.getTime();
        } catch (final Exception var14) {
            var14.printStackTrace();
        }

        final long day = between / 86400000L;
        final long hour = between / 3600000L - day * 24L;
        final long min = between / 60000L - day * 24L * 60L - hour * 60L;
        final long s = between / 1000L - day * 24L * 60L * 60L - hour * 60L * 60L - min * 60L;
        String date = day + "天" + hour + "小时" + min + "分" + s + "秒";
        if (day == 0L && hour != 0L) {
            date = hour + "小时" + min + "分" + s + "秒";
        } else if (day == 0L && hour == 0L && min != 0L) {
            date = min + "分" + s + "秒";
        } else if (day == 0L && hour == 0L && min == 0L) {
            date = s + "秒";
        }

        return date;
    }

    public static int getNextDaySec() {
        final Date date = new Date();
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(6, 1);
        cal.set(11, 9);
        cal.set(12, 0);
        cal.set(13, 0);
        final Long tm = cal.getTimeInMillis() - date.getTime();
        return (int) (tm / 1000L);
    }

    public static String getWeekOfDate(final Date date) {
        final String[] weekOfDays = new String[]{"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week_index = cal.get(7) - 1;
        if (week_index < 0) {
            week_index = 0;
        }

        return weekOfDays[week_index];
    }

    public static String format(final Date date, final String pattern) {
        final DateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(date);
    }

    public static String formatDate(final Date date) {
        return format(date, "yyyy-MM-dd");
    }

    public static String formatTime(final Date date) {
        return format(date, "HH:mm:ss");
    }

    public static String formatDateTime(final Date date) {
        return format(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String formatCurrent(final String pattern) {
        return format(new Date(), pattern);
    }

    public static String formatCurrentDate() {
        return format(new Date(), "yyyy-MM-dd");
    }

    public static String formatCurrentTime() {
        return format(new Date(), "HH:mm:ss");
    }

    public static String formatCurrentDateTime() {
        return format(new Date(), "yyyy-MM-dd HH:mm:ss");
    }

    public static Date getCurrentDate() {
        final Calendar cal = Calendar.getInstance();
        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(14, 0);
        return cal.getTime();
    }

    public static Date getTheDate(final Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(14, 0);
        return cal.getTime();
    }

    public static int compareDate(Date start, Date end) {
        if (start == null && end == null) {
            return 0;
        } else if (end == null) {
            return 1;
        } else {
            if (start == null) {
                start = new Date();
            }

            start = getTheDate(start);
            end = getTheDate(end);
            return start.compareTo(end);
        }
    }

    public static Date stringToDate(final String dateString, final String pattern) {
        final SimpleDateFormat formatter = new SimpleDateFormat(pattern);

        try {
            return formatter.parse(dateString);
        } catch (final ParseException var4) {
            throw new RuntimeException(var4);
        }
    }

    public static Date addYears(final Date date, final int amount) {
        return add(date, 1, amount);
    }

    public static Date addMonths(final Date date, final int amount) {
        return add(date, 2, amount);
    }

    public static Date addWeeks(final Date date, final int amount) {
        return add(date, 3, amount);
    }

    public static Date addDays(final Date date, final int amount) {
        return add(date, 5, amount);
    }

    public static Date addHours(final Date date, final int amount) {
        return add(date, 11, amount);
    }

    public static Date addMinutes(final Date date, final int amount) {
        return add(date, 12, amount);
    }

    public static Date addSeconds(final Date date, final int amount) {
        return add(date, 13, amount);
    }

    public static Date addMilliseconds(final Date date, final int amount) {
        return add(date, 14, amount);
    }

    private static Date add(final Date date, final int calendarField, final int amount) {
        if (date == null) {
            throw new IllegalArgumentException("日期对象不允许为null!");
        } else {
            final Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(calendarField, amount);
            return c.getTime();
        }
    }

    public static Date getDateAfterNatureDays(Date date, final int days, final int hours) {
        if (date == null) {
            date = new Date();
        }

        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(5, days);
        c.add(10, hours);
        return c.getTime();
    }

    public static Date getDateAfterWorkDays(Date date, final int days, final int hours) {
        if (date == null) {
            date = new Date();
        }

        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(5, days);
        c.add(10, hours);
        return c.getTime();
    }

    public static String dateToStamp(final String s) throws ParseException {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final Date date = simpleDateFormat.parse(s);
        final long ts = date.getTime();
        final String res = String.valueOf(ts);
        return res;
    }

    public static String stampToDate(final String s) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final long lt = new Long(s);
        final Date date = new Date(lt);
        return simpleDateFormat.format(date);
    }

    public static void main(final String[] args) {
        final String s = stampToDate("1502964348398");
        System.out.println(s);
    }
}


