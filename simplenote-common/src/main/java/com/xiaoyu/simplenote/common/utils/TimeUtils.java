/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.simplenote.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;

/**
 * @author xiaoyu 2016年3月19日
 */
public class TimeUtils extends DateUtils {

    /**
     * 一周以周日为起始日
     */
    private static final int FIRST_DAY = Calendar.SUNDAY;

    /**
     * 根据给定天数查找的那周中各天的日期
     * 
     * @author xiaoyu
     * @param str
     * @return 一周七天每天的日期
     */
    public static List<String> getDatesInTheWeekByGivenDate(String str) {
        Date date = null;
        Calendar calendar = null;
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final List<String> list = new ArrayList<>(7);
        try {
            date = dateFormat.parse(str);
            calendar = Calendar.getInstance();
            calendar.setTime(date);
        } catch (final ParseException e) {
            return list;
        }
        TimeUtils.setFirstDay(calendar);
        for (int i = 0; i < 7; i++) {
            // dateFormat = new SimpleDateFormat("yyyy-MM-dd EE");
            String s = dateFormat.format(calendar.getTime());
            list.add(s);
            calendar.add(Calendar.DATE, 1);
        }
        return list;
    }

    /**
     * 设置到一周的第一天
     * 
     * @param calendar
     */
    private static void setFirstDay(Calendar calendar) {
        while (calendar.get(Calendar.DAY_OF_WEEK) != TimeUtils.FIRST_DAY) {
            calendar.add(Calendar.DATE, -1);
        }
    }

    /**
     * 根据给定的周数设置日历类
     * 
     * @param year
     * @param week
     * @return
     */
    public static Calendar setCalByGivenWeek(String year, String week) {
        final int y = Integer.parseInt(year);
        final int w = Integer.parseInt(week);
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, y);
        cal.set(Calendar.WEEK_OF_YEAR, w);
        return cal;
    }

    /**
     * 根据给定的年份中某一周查找那周各天的日期
     * 
     * @param year
     * @param week
     * @return
     * @author xiaoyu
     */
    public static List<String> getDatesInTheWeekByGivenWeek(String year, String week) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final Calendar calendar = TimeUtils.setCalByGivenWeek(year, week);
        final List<String> list = new ArrayList<>(7);
        TimeUtils.setFirstDay(calendar);
        for (int i = 0; i < 7; i++) {
            final String s = dateFormat.format(calendar.getTime());
            list.add(s);
            calendar.add(Calendar.DATE, 1);
        }
        return list;
    }

    /**
     * 获取两个日期之间的天数
     * 
     * @param before
     * @param after
     * @return
     */
    public static double getDistanceOfTwoDate(Date before, Date after) {
        final long beforeTime = before.getTime();
        final long afterTime = after.getTime();
        return (afterTime - beforeTime) / (1000 * 3600 * 24);
    }

    /**
     * 获取两个日期之间的分钟
     * 
     * @param before
     * @param after
     * @return
     */
    public static long getMinuteOfTwoDate(Date before, Date after) {
        final long beforeTime = before.getTime();
        final long afterTime = after.getTime();
        return (afterTime - beforeTime) / (1000 * 60);
    }

    /**
     * 几小时说法
     * 
     * @param before
     * @param after
     * @return
     */
    public static String getChineseForInterval(Date before, Date after) {
        final long interval = TimeUtils.getMinuteOfTwoDate(before, after);
        if (0 == interval) {
            return "刚刚";
        } else if (interval < 60) {
            return interval + "分钟前";
        } else if (interval >= 60 && interval <= 24 * 60) {
            return interval / 60 + "小时前";
        } else {
            return DateFormatUtils.format(before, "yyyy-MM-dd");
        }
    }

    /**
     * // 获得当天0点时间
     * 
     * @return
     */
    public static Date getTimesmorning() {
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * // 获得当天24点时间
     * 
     * @return
     */
    public static Date getTimesnight() {
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * The UTC time zone (often referred to as GMT). This is private as it is
     * mutable.
     */
    private static final TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("GMT");
    /**
     * ISO 8601 formatter for date-time without time zone. The format used is
     * {@code yyyy-MM-dd'T'HH:mm:ss}.
     */
    public static final FastDateFormat ISO_DATETIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * ISO 8601 formatter for date-time with time zone. The format used is
     * {@code yyyy-MM-dd'T'HH:mm:ssZZ}.
     */
    public static final FastDateFormat ISO_DATETIME_TIME_ZONE_FORMAT = FastDateFormat
            .getInstance("yyyy-MM-dd'T'HH:mm:ssZZ");

    /**
     * ISO 8601 formatter for date without time zone. The format used is
     * {@code yyyy-MM-dd}.
     */
    public static final FastDateFormat ISO_DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");

    /**
     * ISO 8601-like formatter for date with time zone. The format used is
     * {@code yyyy-MM-ddZZ}. This pattern does not comply with the formal ISO 8601
     * specification as the standard does not allow a time zone without a time.
     */
    public static final FastDateFormat ISO_DATE_TIME_ZONE_FORMAT = FastDateFormat.getInstance("yyyy-MM-ddZZ");

    /**
     * ISO 8601 formatter for time without time zone. The format used is
     * {@code 'T'HH:mm:ss}.
     */
    public static final FastDateFormat ISO_TIME_FORMAT = FastDateFormat.getInstance("'T'HH:mm:ss");

    /**
     * ISO 8601 formatter for time with time zone. The format used is
     * {@code 'T'HH:mm:ssZZ}.
     */
    public static final FastDateFormat ISO_TIME_TIME_ZONE_FORMAT = FastDateFormat.getInstance("'T'HH:mm:ssZZ");

    /**
     * ISO 8601-like formatter for time without time zone. The format used is
     * {@code HH:mm:ss}. This pattern does not comply with the formal ISO 8601
     * specification as the standard requires the 'T' prefix for times.
     */
    public static final FastDateFormat ISO_TIME_NO_T_FORMAT = FastDateFormat.getInstance("HH:mm:ss");

    /**
     * ISO 8601-like formatter for time with time zone. The format used is
     * {@code HH:mm:ssZZ}. This pattern does not comply with the formal ISO 8601
     * specification as the standard requires the 'T' prefix for times.
     */
    public static final FastDateFormat ISO_TIME_NO_T_TIME_ZONE_FORMAT = FastDateFormat.getInstance("HH:mm:ssZZ");

    /**
     * SMTP (and probably other) date headers. The format used is
     * {@code EEE, dd MMM yyyy HH:mm:ss Z} in US locale.
     */
    public static final FastDateFormat SMTP_DATETIME_FORMAT = FastDateFormat.getInstance("EEE, dd MMM yyyy HH:mm:ss Z",
            Locale.US);

    // //-----------------------------------------------------------------------
    // /**
    // * <p>DateFormatUtils instances should NOT be constructed in standard
    // programming.</p>
    // *
    // * <p>This constructor is public to permit tools that require a JavaBean
    // instance
    // * to operate.</p>
    // */
    // public DateFormatUtils() {
    // super();
    // }

    /**
     * <p>
     * Formats a date/time into a specific pattern using the UTC time zone.
     * </p>
     * 
     * @param millis
     *            the date to format expressed in milliseconds
     * @param pattern
     *            the pattern to use to format the date, not null
     * @return the formatted date
     */
    public static String formatUTC(final long millis, final String pattern) {
        return TimeUtils.format(new Date(millis), pattern, TimeUtils.UTC_TIME_ZONE, null);
    }

    /**
     * <p>
     * Formats a date/time into a specific pattern using the UTC time zone.
     * </p>
     * 
     * @param date
     *            the date to format, not null
     * @param pattern
     *            the pattern to use to format the date, not null
     * @return the formatted date
     */
    public static String formatUTC(final Date date, final String pattern) {
        return TimeUtils.format(date, pattern, TimeUtils.UTC_TIME_ZONE, null);
    }

    /**
     * <p>
     * Formats a date/time into a specific pattern using the UTC time zone.
     * </p>
     * 
     * @param millis
     *            the date to format expressed in milliseconds
     * @param pattern
     *            the pattern to use to format the date, not null
     * @param locale
     *            the locale to use, may be <code>null</code>
     * @return the formatted date
     */
    public static String formatUTC(final long millis, final String pattern, final Locale locale) {
        return TimeUtils.format(new Date(millis), pattern, TimeUtils.UTC_TIME_ZONE, locale);
    }

    /**
     * <p>
     * Formats a date/time into a specific pattern using the UTC time zone.
     * </p>
     * 
     * @param date
     *            the date to format, not null
     * @param pattern
     *            the pattern to use to format the date, not null
     * @param locale
     *            the locale to use, may be <code>null</code>
     * @return the formatted date
     */
    public static String formatUTC(final Date date, final String pattern, final Locale locale) {
        return TimeUtils.format(date, pattern, TimeUtils.UTC_TIME_ZONE, locale);
    }

    /**
     * <p>
     * Formats a date/time into a specific pattern.
     * </p>
     * 
     * @param millis
     *            the date to format expressed in milliseconds
     * @param pattern
     *            the pattern to use to format the date, not null
     * @return the formatted date
     */
    public static String format(final long millis, final String pattern) {
        return TimeUtils.format(new Date(millis), pattern, null, null);
    }

    /**
     * <p>
     * Formats a date/time into a specific pattern.
     * </p>
     * 
     * @param date
     *            the date to format, not null
     * @param pattern
     *            the pattern to use to format the date, not null
     * @return the formatted date
     */
    public static String format(final Date date, final String pattern) {
        return TimeUtils.format(date, pattern, null, null);
    }

    /**
     * <p>
     * Formats a calendar into a specific pattern.
     * </p>
     * 
     * @param calendar
     *            the calendar to format, not null
     * @param pattern
     *            the pattern to use to format the calendar, not null
     * @return the formatted calendar
     * @see FastDateFormat#format(Calendar)
     * @since 2.4
     */
    public static String format(final Calendar calendar, final String pattern) {
        return TimeUtils.format(calendar, pattern, null, null);
    }

    /**
     * <p>
     * Formats a date/time into a specific pattern in a time zone.
     * </p>
     * 
     * @param millis
     *            the time expressed in milliseconds
     * @param pattern
     *            the pattern to use to format the date, not null
     * @param timeZone
     *            the time zone to use, may be <code>null</code>
     * @return the formatted date
     */
    public static String format(final long millis, final String pattern, final TimeZone timeZone) {
        return TimeUtils.format(new Date(millis), pattern, timeZone, null);
    }

    /**
     * <p>
     * Formats a date/time into a specific pattern in a time zone.
     * </p>
     * 
     * @param date
     *            the date to format, not null
     * @param pattern
     *            the pattern to use to format the date, not null
     * @param timeZone
     *            the time zone to use, may be <code>null</code>
     * @return the formatted date
     */
    public static String format(final Date date, final String pattern, final TimeZone timeZone) {
        return TimeUtils.format(date, pattern, timeZone, null);
    }

    /**
     * <p>
     * Formats a calendar into a specific pattern in a time zone.
     * </p>
     * 
     * @param calendar
     *            the calendar to format, not null
     * @param pattern
     *            the pattern to use to format the calendar, not null
     * @param timeZone
     *            the time zone to use, may be <code>null</code>
     * @return the formatted calendar
     * @see FastDateFormat#format(Calendar)
     * @since 2.4
     */
    public static String format(final Calendar calendar, final String pattern, final TimeZone timeZone) {
        return TimeUtils.format(calendar, pattern, timeZone, null);
    }

    /**
     * <p>
     * Formats a date/time into a specific pattern in a locale.
     * </p>
     * 
     * @param millis
     *            the date to format expressed in milliseconds
     * @param pattern
     *            the pattern to use to format the date, not null
     * @param locale
     *            the locale to use, may be <code>null</code>
     * @return the formatted date
     */
    public static String format(final long millis, final String pattern, final Locale locale) {
        return TimeUtils.format(new Date(millis), pattern, null, locale);
    }

    /**
     * <p>
     * Formats a date/time into a specific pattern in a locale.
     * </p>
     * 
     * @param date
     *            the date to format, not null
     * @param pattern
     *            the pattern to use to format the date, not null
     * @param locale
     *            the locale to use, may be <code>null</code>
     * @return the formatted date
     */
    public static String format(final Date date, final String pattern, final Locale locale) {
        return TimeUtils.format(date, pattern, null, locale);
    }

    /**
     * <p>
     * Formats a calendar into a specific pattern in a locale.
     * </p>
     * 
     * @param calendar
     *            the calendar to format, not null
     * @param pattern
     *            the pattern to use to format the calendar, not null
     * @param locale
     *            the locale to use, may be <code>null</code>
     * @return the formatted calendar
     * @see FastDateFormat#format(Calendar)
     * @since 2.4
     */
    public static String format(final Calendar calendar, final String pattern, final Locale locale) {
        return TimeUtils.format(calendar, pattern, null, locale);
    }

    /**
     * <p>
     * Formats a date/time into a specific pattern in a time zone and locale.
     * </p>
     * 
     * @param millis
     *            the date to format expressed in milliseconds
     * @param pattern
     *            the pattern to use to format the date, not null
     * @param timeZone
     *            the time zone to use, may be <code>null</code>
     * @param locale
     *            the locale to use, may be <code>null</code>
     * @return the formatted date
     */
    public static String format(final long millis, final String pattern, final TimeZone timeZone, final Locale locale) {
        return TimeUtils.format(new Date(millis), pattern, timeZone, locale);
    }

    /**
     * <p>
     * Formats a date/time into a specific pattern in a time zone and locale.
     * </p>
     * 
     * @param date
     *            the date to format, not null
     * @param pattern
     *            the pattern to use to format the date, not null, not null
     * @param timeZone
     *            the time zone to use, may be <code>null</code>
     * @param locale
     *            the locale to use, may be <code>null</code>
     * @return the formatted date
     */
    public static String format(final Date date, final String pattern, final TimeZone timeZone, final Locale locale) {
        final FastDateFormat df = FastDateFormat.getInstance(pattern, timeZone, locale);
        return df.format(date);
    }

    /**
     * <p>
     * Formats a calendar into a specific pattern in a time zone and locale.
     * </p>
     * 
     * @param calendar
     *            the calendar to format, not null
     * @param pattern
     *            the pattern to use to format the calendar, not null
     * @param timeZone
     *            the time zone to use, may be <code>null</code>
     * @param locale
     *            the locale to use, may be <code>null</code>
     * @return the formatted calendar
     * @see FastDateFormat#format(Calendar)
     * @since 2.4
     */
    public static String format(final Calendar calendar, final String pattern, final TimeZone timeZone,
            final Locale locale) {
        final FastDateFormat df = FastDateFormat.getInstance(pattern, timeZone, locale);
        return df.format(calendar);
    }

    /**
     * 将数据库的时间转化为标准时间
     * 
     * @author xiaoyu
     * @param date
     * @param pattern
     * @return pattern
     * @throws ParseException
     * @time 2016年3月25日下午2:43:13
     */
    public static String formatDate(final Date date, final String pattern) throws ParseException {
        return TimeUtils.format(date, pattern);
    }

    /**
     * @author xiaoyu
     * @param date
     * @return yyyy-MM-dd HH:mm:ss
     * @throws ParseException
     * @time 2016年3月25日下午2:55:38
     */
    public static String formatDate(final Date date) throws ParseException {
        return TimeUtils.format(date, "yyyy-MM-dd HH:mm:ss");
    }

}
