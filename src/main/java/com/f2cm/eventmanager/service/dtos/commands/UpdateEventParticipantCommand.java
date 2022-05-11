package com.f2cm.eventmanager.service.dtos.commands;

import com.f2cm.eventmanager.domain.people.EventRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventParticipantCommand {

    @NotNull
    private boolean isPaid;

    @NotNull
    private boolean isInternal;

    @NotNull
    private String eventRoleSlug;

}
