package ru.practicum.ewm.dto;

import java.time.format.DateTimeFormatter;

public class GlobalConstants {
    public static final String DT_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter DT_FORMATTER = DateTimeFormatter.ofPattern(DT_FORMAT);
    public static final String PAGE_DEFAULT_FROM = "0";
    public static final String PAGE_DEFAULT_SIZE = "10";
    public static final int MIN_LENGTH_ANNOTATION = 20;
    public static final int MAX_LENGTH_ANNOTATION = 2000;
    public static final int MIN_LENGTH_DESCRIPTION = 20;
    public static final int MAX_LENGTH_DESCRIPTION = 7000;
    public static final int MIN_LENGTH_TITLE = 3;
    public static final int MAX_LENGTH_TITLE = 120;
    public static final int MIN_LENGTH_CATEGORY = 1;
    public static final int MAX_LENGTH_CATEGORY = 50;
    public static final int MIN_LENGTH_EMAIL = 6;
    public static final int MAX_LENGTH_EMAIL = 254;
    public static final int MIN_LENGTH_USER_NAME = 2;
    public static final int MAX_LENGTH_USER_NAME = 250;
    public static final int MAX_LENGTH_TITLE_COMPILATION = 50;
    public static final int MIN_LENGTH_COMMENT = 3;
    public static final int MAX_LENGTH_COMMENT = 2000;
}
