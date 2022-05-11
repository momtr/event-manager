package com.f2cm.eventmanager.service.fixtures;

import com.f2cm.eventmanager.domain.people.Contact;
import com.f2cm.eventmanager.domain.people.ContactType;
import com.f2cm.eventmanager.domain.people.Gender;
import com.f2cm.eventmanager.domain.people.Person;
import com.f2cm.eventmanager.domain.places.Address;
import com.f2cm.eventmanager.service.dtos.commands.CreateContactCommand;
import com.f2cm.eventmanager.service.dtos.commands.CreatePersonCommand;

import java.time.LocalDate;
import java.util.HashSet;

public class PersonFixtures {

    public static final String DEFAULT_TOKEN = "TOKEN-DEF-1";
    public static final String DEFAULT_MEANS = "email";
    public static final String EMAIL = "email@provider.com";

    public static CreatePersonCommand getCreatePersonCommandFixture() {
        return new CreatePersonCommand("Moritz", "Mitterdorfer",null,null, LocalDate.of(2004, 04, 16) ,"tester", Gender.FEMALE,"Street 1","1000","City","Country");
    }

    public static Person getPersonFixture1() {
        return Person.builder().token(DEFAULT_TOKEN).address(Address.builder().zipCode("1000").country("Country").city("City").streetNumber("Stereet 1").build()).contacts(new HashSet<>()).build();
    }

    public static Person getPersonFixture2() {
        return Person.builder().token("TOKEN-2").address(Address.builder().zipCode("1000").country("Country").city("City").streetNumber("Stereet 1").build()).contacts(new HashSet<>()).build();
    }

    public static ContactType getContactTypeFixture() {
        return ContactType.builder().means(DEFAULT_MEANS).build();
    }

    public static CreateContactCommand getCreateContactCommandFixture() {
        return new CreateContactCommand(DEFAULT_MEANS, false, EMAIL, true);
    }

    public static Contact getContactFixture() {
        return Contact.builder().contactType(getContactTypeFixture()).token("CONTACT-TOKEN").value(EMAIL).build();
    }

}
