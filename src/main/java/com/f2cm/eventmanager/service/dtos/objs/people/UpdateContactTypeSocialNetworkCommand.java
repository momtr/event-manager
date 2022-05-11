package com.f2cm.eventmanager.service.dtos.objs.people;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateContactTypeSocialNetworkCommand {

    @NotNull
    private boolean isSocialNetwork;

}
