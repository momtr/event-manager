package com.f2cm.eventmanager.service.dtos.objs.people;

import com.f2cm.eventmanager.domain.people.Contact;
import com.f2cm.eventmanager.domain.people.Gender;
import com.f2cm.eventmanager.domain.people.Person;
import com.f2cm.eventmanager.domain.places.Address;
import com.f2cm.eventmanager.persistence.fixtures.ContactTestFixture;
import com.f2cm.eventmanager.persistence.fixtures.PersonTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PersonDtoTest {

    private PersonTestFixture personTestFixture;
    private ContactTestFixture contactTestFixture;

    @BeforeEach
    void setup() {
        personTestFixture = new PersonTestFixture();
        contactTestFixture = new ContactTestFixture();
    }

    @Test
    void ensureFactoryMethodReturnsPersonDtoFromPerson() {
        Contact contact = contactTestFixture.getFlorianPhone();
        Address address = Address.builder().city("Vienna").build();
        String token = "token";
        String mainJob = "main job";
        String titleFront = "Dr.";
        String titleBack = "MA";
        String firstName = "Max";
        String lastName = "Mustermann";
        Gender gender = Gender.MALE;
        LocalDate date = LocalDate.of(2004, 4, 16);
        LocalDateTime ts = LocalDateTime.now();
        Person person = Person.builder()
                .contacts(Set.of(contact))
                .primaryContact(contact)
                .token(token)
                .createdAt(ts)
                .address(address)
                .mainJob(mainJob)
                .titleFront(titleFront)
                .titleBack(titleBack)
                .firstName(firstName)
                .lastName(lastName)
                .gender(gender)
                .birthday(date)
                .build();
        PersonDto personDto = PersonDto.of(person);
        assertThat(personDto.get_createdAt()).isEqualTo(ts);
        assertThat(personDto.get_token()).isEqualTo(token);
        assertThat(personDto.getContacts().size()).isEqualTo(1);
        assertThat(personDto.getContacts()
                .stream()
                .collect(Collectors.toList())
                .get(0).getValue()).isEqualTo(contactTestFixture.getFlorianPhone().getValue());
        assertThat(personDto.getPrimaryContact().getValue()).isEqualTo(contactTestFixture.getFlorianPhone().getValue());
        assertThat(personDto.getBirthday()).isEqualTo(date);
        assertThat(personDto.getMainJob()).isEqualTo(mainJob);
        assertThat(personDto.getTitleFront()).isEqualTo(titleFront);
        assertThat(personDto.getTitleBack()).isEqualTo(titleBack);
        assertThat(personDto.getFirstName()).isEqualTo(firstName);
        assertThat(personDto.getLastName()).isEqualTo(lastName);
        assertThat(personDto.getGender()).isEqualTo(gender);
        assertThat(personDto.getAddress().getCity()).isEqualTo(address.getCity());
    }

    @Test
    void ensureFactoryMethodReturnsNullFromNullPerson() {
        Person person = null;
        PersonDto personDto = PersonDto.of(person);
        assertThat(personDto).isNull();
    }

}
