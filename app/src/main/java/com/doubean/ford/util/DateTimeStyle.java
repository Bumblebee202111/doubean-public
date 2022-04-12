package com.doubean.ford.util;

import java.time.LocalDateTime;

public enum DateTimeStyle implements IDateTimeStyle {
    SHORT {
        @Override
        public String getPattern(LocalDateTime localDateTime) {
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
    },
    NORMAL {
        @Override
        public String getPattern(LocalDateTime localDateTime) {
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
    },

    LONG {
        @Override
        public String getPattern(LocalDateTime localDateTime) {
            return "yyyy-MM-dd HH:mm";
        }
    }

}
