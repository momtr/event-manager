package com.f2cm.eventmanager.service.dtos.objs.events;

import com.f2cm.eventmanager.domain.events.TimeSlot;
import com.f2cm.eventmanager.service.dtos.objs.Dto;
import com.f2cm.eventmanager.service.dtos.objs.people.PersonDto;
import com.f2cm.eventmanager.service.dtos.objs.people.SimplePersonDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.f2cm.eventmanager.foundation.ensurer.EnsurerFactory.ensureThat;

@Getter
@Builder
@AllArgsConstructor
public class TimeSlotDto implements Dto {

    private String _token;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
    private String name;
    private String description;
    private SimplePersonDto contactPerson;
    private SimpleEventDto event;

    public static TimeSlotDto of(TimeSlot timeSlot) {
        if(timeSlot == null) {
            return null;
        }
        return TimeSlotDto.builder()
                ._token(timeSlot.getToken())
                .dateFrom(timeSlot.getFrom())
                .dateTo(timeSlot.getTo())
                .name(timeSlot.getName())
                .description(timeSlot.getDescription())
                .contactPerson(SimplePersonDto.of(timeSlot.getContact()))
                .event(SimpleEventDto.of(timeSlot.getEvent()))
                .build();
    }

}
