package com.f2cm.eventmanager.service.dtos.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class UpdateAddressCommand {

    @NotNull
    private String address;

}
