package com.f2cm.eventmanager.persistence.fixtures;

import com.f2cm.eventmanager.domain.events.TimeSlot;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class TimeSlotTestFixture {
    private final TimeSlot midnightSpecial;
    private final TimeSlot talk;

    public TimeSlotTestFixture() {
        midnightSpecial = TimeSlot.builder()
                .token("timeSlotToken1")
                .name("Midnight Special")
                .description("Something special")
                .from(LocalDateTime.of(2021, 1, 2, 23, 59))
                .to(LocalDateTime.of(2021, 1, 2, 1, 0))
                .build();
        talk = TimeSlot.builder()
                .token("timeSlotToken2")
                .name("Talk 1")
                .description("Talk 1")
                .from(LocalDateTime.of(2021, 4, 16, 13, 59))
                .to(LocalDateTime.of(2021, 4, 16, 15, 0))
                .build();
    }

    public List<TimeSlot> getAllTimeSlots() {
        return List.of(midnightSpecial, talk);
    }
}
