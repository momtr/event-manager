package com.f2cm.eventmanager.service.fixtures;

import com.f2cm.eventmanager.domain.events.Event;
import com.f2cm.eventmanager.domain.events.TimeSlot;
import com.f2cm.eventmanager.domain.people.Person;
import com.f2cm.eventmanager.service.dtos.commands.CreateTimeSlotCommand;

import java.time.LocalDateTime;

public class TimeSlotFixtures {

    public static String getTimeSlotToken1() {
        return "timeSlotT1";
    }

    public static String getTimeSlotToken2() {
        return "timeSlotT2";
    }

    public static TimeSlot getTimeSlot1(Person contact, Event event) {
        return TimeSlot.builder()
                .name("First Timeslot")
                .description("Some description")
                .contact(contact)
                .event(event)
                .from(LocalDateTime.of(2000, 10, 1, 10, 0))
                .to(LocalDateTime.of(2000, 10, 1, 10, 30))
                .token(getTimeSlotToken1())
                .build();
    }

    public static TimeSlot getTimeSlot2(Person contact, Event event) {
        return TimeSlot.builder()
                .name("Second Timeslot")
                .description("Other description")
                .contact(contact)
                .event(event)
                .from(LocalDateTime.of(2010, 10, 1, 10, 0))
                .to(LocalDateTime.of(2010, 10, 1, 10, 30))
                .token(getTimeSlotToken1())
                .build();
    }

    public static CreateTimeSlotCommand getCreateTimeSlotCommand1(String contactToken) {
        return CreateTimeSlotCommand.builder()
                .name("First Timeslot")
                .description("Some description")
                .contactToken(contactToken)
                .from(LocalDateTime.of(2000, 10, 1, 10, 0))
                .to(LocalDateTime.of(2000, 10, 1, 10, 30))
                .build();
    }

    public static CreateTimeSlotCommand getCreateTimeSlotCommand2(String contactToken) {
        return CreateTimeSlotCommand.builder()
                .name("Second Timeslot")
                .description("Other description")
                .contactToken(contactToken)
                .from(LocalDateTime.of(2010, 10, 1, 10, 0))
                .to(LocalDateTime.of(2010, 10, 1, 10, 30))
                .build();
    }
}
