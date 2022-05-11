package com.f2cm.eventmanager.service.dtos.commands;

import com.f2cm.eventmanager.domain.events.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateEventCommand {
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean luxury;
    private boolean wasHold;
    private EventType eventType;

    private String organizerToken;
    private String locationToken;
    private List<String> tagNames;
}
