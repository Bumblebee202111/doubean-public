package com.doubean.ford.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    public static String dateTimeText(LocalDateTime localDateTime, DateTimeTextLength textLength) {
        if (localDateTime == null)
            return null;
        return localDateTime.format(DateTimeFormatter.ofPattern(getDateTimePattern(localDateTime, textLength)));
    }

    private static String getDateTimePattern(LocalDateTime localDateTime, DateTimeTextLength textLength) {
        switch (textLength) {
            case SHORT:
                return shortDateTimePattern(localDateTime);
            case NORMAL:
                return normalDateTimePattern(localDateTime);
            case LONG:
                return fullDateTimePattern();
        }
        return null;
    }

    private static String shortDateTimePattern(LocalDateTime localDateTime) {
        LocalDateTime now = LocalDateTime.now();
        if (now.getYear() == localDateTime.getYear()) {
            if (now.getMonth() == localDateTime.getMonth()) {
                if (now.getDayOfMonth() == localDateTime.getDayOfMonth()) {
                    return "HH:mm";
                } else {
                    return "MM-dd";
                }
            } else {
                return "MM-dd";
            }

        } else {
            return "yyyy-MM";
        }
    }

    private static String normalDateTimePattern(LocalDateTime localDateTime) {
        LocalDateTime now = LocalDateTime.now();
        if (now.getYear() == localDateTime.getYear()) {
            if (now.getMonth() == localDateTime.getMonth()) {
                if (now.getDayOfMonth() == localDateTime.getDayOfMonth()) {
                    return "HH:mm";
                } else {
                    return "MM-dd HH:mm";
                }
            } else {
                return "MM-dd HH:mm";
            }
        } else {
            return "yy-MM-dd HH:mm";
        }
    }

    private static String fullDateTimePattern() {
        return "yyyy-MM-dd HH:mm";
    }

    public enum DateTimeTextLength {
        SHORT, NORMAL, LONG, DEFAULT
    }
}
