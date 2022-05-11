package com.f2cm.eventmanager.service.dtos.commands;

import com.f2cm.eventmanager.domain.people.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePersonCommand {

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    private String titleFront;
    private String titleBack;

    @NotNull
    private LocalDate birthday;

    @NotNull
    private String mainJob;

    @NotNull
    private Gender gender;

    private String streetNumber;
    private String zipCode;
    private String city;

    @NotNull
    private String country;

}
