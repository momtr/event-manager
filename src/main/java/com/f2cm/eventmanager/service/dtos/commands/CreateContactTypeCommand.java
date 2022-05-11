package com.f2cm.eventmanager.service.dtos.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateContactTypeCommand {

    @NotNull
    private String means;

    private boolean isSocialNetwork;

}
