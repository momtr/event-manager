package com.f2cm.eventmanager.service.dtos.objs.events;

import com.f2cm.eventmanager.domain.events.Event;
import com.f2cm.eventmanager.domain.events.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

import static com.f2cm.eventmanager.foundation.ensurer.EnsurerFactory.ensureThat;

@Getter
@Builder
@AllArgsConstructor
public class SimpleEventDto {

    private String _token;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private EventType type;

    public static SimpleEventDto of(Event event) {
        if(event == null) {
            return null;
        }
        return SimpleEventDto.builder()
                ._token(event.getToken())
                .name(event.getName())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .type(event.getEventType())
                .build();
    }

}
