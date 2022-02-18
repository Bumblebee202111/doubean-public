package com.doubean.ford.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {
    public static String getShortDateString(Date date) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        String pattern;
        if (now.getYear() == localDateTime.getYear()) {
            if (now.getDayOfYear() == localDateTime.getDayOfYear()) {
                pattern = "HH:mm";
            } else {
                pattern = "MM-dd HH:mm";
            }
        } else {
            pattern = "yyyy-MM-dd";
        }
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String getFullDateString(Date date) {
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
