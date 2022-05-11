package com.f2cm.eventmanager.service.dtos.objs.events;

import com.f2cm.eventmanager.domain.events.Event;
import com.f2cm.eventmanager.domain.events.EventType;
import com.f2cm.eventmanager.domain.people.Contact;
import com.f2cm.eventmanager.service.dtos.objs.Dto;
import com.f2cm.eventmanager.service.dtos.objs.people.PersonDto;
import com.f2cm.eventmanager.service.dtos.objs.places.LocationDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.f2cm.eventmanager.foundation.ensurer.EnsurerFactory.ensureThat;

@Getter
@Builder
@AllArgsConstructor
public class EventDto implements Dto {

    private String _token;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean wasHold;
    private boolean isLuxury;
    private LocationDto location;
    private PersonDto organizer;
    private EventType type;
    private List<TimeSlotDto> timeSlots;
    private List<TagDto> tags;

    public static EventDto of(Event event) {
        ensureThat(event).isNotNull();
        return EventDto.builder()
                ._token(event.getToken())
                .name(event.getName())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .wasHold(event.isWasHold())
                .isLuxury(event.isLuxury())
                .location(LocationDto.of(event.getLocation()))
                .organizer(PersonDto.of(event.getOrganizer()))
                .type(event.getEventType())
                .timeSlots(event
                        .getTimeSlots()
                        .stream()
                        .map(TimeSlotDto::of)
                        .collect(Collectors.toList()))
                .tags(event
                        .getTags()
                        .stream()
                        .map(TagDto::of)
                        .collect(Collectors.toList())
                ).build();
    }

}
