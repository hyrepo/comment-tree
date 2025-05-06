package com.hyrepo.comments_tree.util;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class DateTimeUtil {
    public static long now() {
        return ZonedDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli();
    }
}
