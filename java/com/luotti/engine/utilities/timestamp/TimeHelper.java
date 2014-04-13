package com.luotti.engine.utilities.timestamp;

import java.util.Date;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import com.luotti.engine.Environment;

public class TimeHelper {

    private static Calendar CALENDAR;
    private static DateFormat DATE_FORMAT;

    static {
        TimeHelper.CALENDAR = GregorianCalendar.getInstance();
        TimeHelper.DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy hh:MM:ss");
    }

    public static Date getDate()
    {
        return new Date(Environment.traceMilliTime());
    }

    public static long getTimestamp()
    {
        return (Environment.traceMilliTime() / 1000);
    }

    public static String getCurrentDate()
    {
        return TimeHelper.DATE_FORMAT.format(new Date());
    }

    public static long getTodayTimestamp()
    {
        long now = Environment.traceMilliTime();
        long days = now / 86400000L;
        long hours = (now - days * 86400000L) / 3600000L;
        long minutes = (now - days * 86400000L - hours * 3600000L) / 60000L;
        long seconds = (now - days * 86400000L - hours * 3600000L - minutes * 60000L) / 1000L;

        return now / 1000L - (seconds + minutes * 60L + hours * 60L * 60L);
    }

    public static String getGivenDate(long timestamp)
    {
        timestamp = (timestamp * 1000l);
        Date date = new Date(timestamp);
        return TimeHelper.DATE_FORMAT.format(date);
    }

    public static long calculateElapsedTime(long timestamp)
    {
        timestamp = (TimeHelper.getTimestamp() - timestamp);
        return (timestamp / (24 * 60 * 60));
    }
}
