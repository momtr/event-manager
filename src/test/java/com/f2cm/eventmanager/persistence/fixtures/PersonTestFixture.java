package com.f2cm.eventmanager.persistence.fixtures;


import com.f2cm.eventmanager.domain.people.Contact;
import com.f2cm.eventmanager.domain.people.Gender;
import com.f2cm.eventmanager.domain.people.Person;
import lombok.Getter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class PersonTestFixture {
    private final AddressTestFixture addressTestFixture;
    private final ContactTestFixture contactTestFixture;

    private final Person florian, moritz, p3, p4;

    public PersonTestFixture() {
        addressTestFixture = new AddressTestFixture();
        contactTestFixture = new ContactTestFixture();

        Set<Contact> floContacts = new HashSet<>();
        floContacts.add(contactTestFixture.getFlorianPhone());
        floContacts.add(contactTestFixture.getFlorianSnapchat());

        florian = Person.builder()
                .firstName("Florian").lastName("Flatscher")
                .titleBack("BACK")
                .titleFront("Dipl-Ing.")
                .birthday(LocalDate.of(2004, 1, 9))
                .mainJob("Student at HTL Spengergasse")
                .gender(Gender.MALE)
                .address(addressTestFixture.getFlorianHome())
                .contacts(floContacts)
                .primaryContact(contactTestFixture.getFlorianPhone())
                .build();

        moritz = Person.builder()
                .firstName("Moritz").lastName("Mitterdorfer")
                .birthday(LocalDate.of(2004, 4, 16))
                .mainJob("Student at HTL Spengergasse")
                .gender(Gender.MALE)
                .address(addressTestFixture.getMoritzHome())
                .contacts(Set.of(contactTestFixture.getMoritzSnapchat()))
                .build();

        p3 = Person.builder()
                .firstName("PName")
                .lastName("PersonLastName3")
                .birthday(LocalDate.of(2004, 4, 16))
                .gender(Gender.MALE)
                .build();

        p4 = Person.builder()
                .firstName("PName")
                .lastName("PersonLastName4")
                .token("TOKEN-123")
                .birthday(LocalDate.of(2003, 2, 12))
                .gender(Gender.FEMALE)
                .build();
    }

    public List<Person> getAllPersons() {
        return List.of(florian, moritz, p3, p4);
    }

}
