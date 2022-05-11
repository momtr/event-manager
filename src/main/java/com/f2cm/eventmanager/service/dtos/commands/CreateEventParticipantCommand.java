package com.f2cm.eventmanager.service.dtos.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateEventParticipantCommand {

    @Valid
    private String eventToken;

    @Valid
    private String personToken;

    @Valid
    private String eventRoleSlug;

    private boolean isPaid;
    private boolean isInternal;

}
