package com.doubean.ford.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    public static String dateTimeText(LocalDateTime localDateTime, IDateTimeStyle dateTimeStyle) {
        if (localDateTime == null)
            return null;
        return localDateTime.format(DateTimeFormatter.ofPattern(dateTimeStyle.getPattern(localDateTime)));
    }
}
