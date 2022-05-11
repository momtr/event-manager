package com.f2cm.eventmanager.service.dtos.commands;

import com.f2cm.eventmanager.domain.people.ContactType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CreateContactCommand {

    @NotNull
    private String contactType;

    private boolean contactTypeIsSocialNetwork;

    @NotNull
    private String address;

    private boolean isBusiness;

}
