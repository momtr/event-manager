package com.f2cm.eventmanager.service.dtos.objs.people;

import com.f2cm.eventmanager.domain.people.Person;
import com.f2cm.eventmanager.service.dtos.objs.Dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SimplePersonDto implements Dto {

    private String _token;
    private String firstName;
    private String lastName;
    private String titleFront;
    private String titleBack;

    public static SimplePersonDto of(Person person) {
        if(person == null) {
            return null;
        }
        return SimplePersonDto.builder()
                ._token(person.getToken())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .titleFront(person.getTitleFront())
                .titleBack(person.getTitleBack())
                .build();
    }

}
