package com.doubean.ford.util;

import java.time.LocalDateTime;

public interface IDateTimeStyle {
    String getPattern(LocalDateTime localDateTime);
}
