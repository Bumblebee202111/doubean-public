package com.doubean.ford.util;

import android.text.TextUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DateUtil {
    public static String getShortDateString(Date date) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        String datePattern;
        if (now.getYear() == localDateTime.getYear()) {
            if (now.getDayOfYear() == localDateTime.getDayOfYear()) {
                datePattern = "HH:mm";
            } else {
                datePattern = "MM-dd";
            }
        } else {
            datePattern = "yyyy-MM-dd";
        }
        LocalDateTime dayNow = LocalDateTime.from(now);

        String timePattern = ChronoUnit.DAYS.between(localDateTime.toLocalDate(), now.toLocalDate()) <= 1 ? "HH:mm" : "";
        String pattern = datePattern;
        if (!TextUtils.isEmpty(datePattern) || !TextUtils.isEmpty(timePattern))
            pattern += ' ';
        pattern += timePattern;
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String getFullDateString(Date date) {
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
