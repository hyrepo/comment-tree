package com.hyrepo.comments_tree.util;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;


class DateTimeUtilTest {
    @Test
    void shouldGerCurrentTimestampAtGmtTimezone() {
        ZonedDateTime secondsBefore = Instant.now().atZone(ZoneOffset.UTC).minusSeconds(5);
        ZonedDateTime secondsAfter = Instant.now().atZone(ZoneOffset.UTC).plusSeconds(5);
        ZonedDateTime now = Instant.ofEpochMilli(DateTimeUtil.now()).atZone(ZoneOffset.UTC);

        assertThat(now.isAfter(secondsBefore)).isTrue();
        assertThat(now.isBefore(secondsAfter)).isTrue();
    }
}