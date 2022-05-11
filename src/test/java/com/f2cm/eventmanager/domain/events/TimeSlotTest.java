package com.f2cm.eventmanager.domain.events;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TimeSlotTest {

    @Test
    void ensureHoursInBetweenYieldsCorrectNumOfHoursForTimeSlot() {
        TimeSlot ts = TimeSlot.builder().from(LocalDateTime.of(2022, 12, 29, 23, 30)).to(LocalDateTime.of(2022, 12, 30, 1, 30)).build();
        assertThat(ts.hoursInBetween()).isEqualTo(2);
    }

    @Test
    void ensureWithinDayEqualsToIfTimeSlotSpansOverDay() {
        TimeSlot ts1 = TimeSlot.builder().from(LocalDateTime.of(2022, 12, 29, 23, 30)).to(LocalDateTime.of(2022, 12, 30, 1, 30)).build();
        TimeSlot ts2 = TimeSlot.builder().from(LocalDateTime.of(2022, 12, 29, 1, 30)).to(LocalDateTime.of(2022, 12, 29, 23, 30)).build();
        assertThat(ts1.takesPlaceWithinOneDay()).isFalse();
        assertThat(ts2.takesPlaceWithinOneDay()).isTrue();
    }

}
