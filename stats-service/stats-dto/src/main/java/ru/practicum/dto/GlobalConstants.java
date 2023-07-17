package ru.practicum.dto;

import java.time.format.DateTimeFormatter;

public class GlobalConstants {
    public static final String DT_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter DT_FORMATTER = DateTimeFormatter.ofPattern(DT_FORMAT);
}
