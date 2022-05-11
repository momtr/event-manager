package com.f2cm.eventmanager.service.dtos.objs.people;

import com.f2cm.eventmanager.domain.people.Gender;
import com.f2cm.eventmanager.domain.people.Person;
import com.f2cm.eventmanager.service.dtos.objs.Dto;
import com.f2cm.eventmanager.service.dtos.objs.places.AddressDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.f2cm.eventmanager.foundation.ensurer.EnsurerFactory.ensureThat;

@Getter
@Builder
@AllArgsConstructor
public class PersonDto implements Dto {

    private String _token;
    private LocalDateTime _createdAt;
    private String firstName;
    private String lastName;
    private String titleFront;
    private String titleBack;
    private LocalDate birthday;
    private String mainJob;
    private Gender gender;
    private AddressDto address;
    private Set<ContactDto> contacts;
    private ContactDto primaryContact;

    public static PersonDto of(Person person) {
        if (person == null) {
            return null;
        }
        return PersonDto.builder()
                ._token(person.getToken())
                ._createdAt(person.getCreatedAt())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .titleFront(person.getTitleFront())
                .titleBack(person.getTitleBack())
                .birthday(person.getBirthday())
                .mainJob(person.getMainJob())
                .gender(person.getGender())
                .address(AddressDto.of(person.getAddress()))
                .contacts(person.getContacts() != null ? person
                        .getContacts()
                        .stream()
                        .map(ContactDto::of)
                        .collect(Collectors.toSet()) : Collections.emptySet())
                .primaryContact(person.getPrimaryContact() != null ? ContactDto.of(person.getPrimaryContact()) : null)
                .build();
    }

}
