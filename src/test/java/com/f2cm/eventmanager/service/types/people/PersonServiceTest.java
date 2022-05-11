package com.f2cm.eventmanager.service.types.people;

import com.f2cm.eventmanager.domain.people.Contact;
import com.f2cm.eventmanager.domain.people.ContactType;
import com.f2cm.eventmanager.domain.people.Person;
import com.f2cm.eventmanager.foundation.time.TemporalValueFactory;
import com.f2cm.eventmanager.persistence.types.people.ContactRepository;
import com.f2cm.eventmanager.persistence.types.people.PersonRepository;
import com.f2cm.eventmanager.service.TokenService;
import com.f2cm.eventmanager.service.dtos.commands.CreateContactCommand;
import com.f2cm.eventmanager.service.dtos.commands.CreatePersonCommand;
import com.f2cm.eventmanager.service.exceptions.NotFoundException;
import com.f2cm.eventmanager.service.exceptions.ServiceException;
import com.f2cm.eventmanager.service.fixtures.PersonFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.PersistenceException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private TemporalValueFactory temporalValueFactory;

    @Mock
    private TokenService tokenService;

    @Mock
    private ContactTypeService contactTypeService;

    private PersonService personService;

    private PersonFixtures personFixtures;

    @BeforeEach
    void setup() {
        assertThat(personRepository).isNotNull();
        assertThat(contactRepository).isNotNull();
        assertThat(temporalValueFactory).isNotNull();
        assertThat(tokenService).isNotNull();
        assertThat(contactTypeService).isNotNull();
        personService = new PersonService(personRepository, contactRepository, temporalValueFactory, tokenService, contactTypeService);
        personFixtures = new PersonFixtures();
    }

    @Test
    void ensureDatabaseProblemsGotWrappedProperly() {
        CreatePersonCommand createPersonCommand = personFixtures.getCreatePersonCommandFixture();
        Exception pe = new PersistenceException("Persistence Exception");
        when(personRepository.save(any())).thenThrow(pe);
        var ex = assertThrows(ServiceException.class, () -> personService.createPerson(createPersonCommand));
        assertThat(ex).hasMessageContaining("Person")
                .hasMessageContaining("database problems")
                .hasRootCause(pe);
    }

    @Test
    void ensureCreatingPersonWorksProperly() {
        LocalDateTime time = LocalDateTime.now();
        when(temporalValueFactory.now()).thenReturn(time);
        when(tokenService.createNanoIdWithType(any())).thenReturn("TOKEN");
        Person person = personService.createPerson(personFixtures.getCreatePersonCommandFixture());
        assertThat(person).isNotNull();
        assertThat(person.getToken()).isNotBlank();
        assertThat(person.getCreatedAt()).isEqualTo(time);
        verify(personRepository).save(any());
    }

    @Test
    void ensureGetOptionalPersonByTokenWorksProperly() {
        String token = "TOKEN";
        Optional<Person> personOptional = Optional.of(Person.builder().token(token).build());
        when(personRepository.findByToken(token)).thenReturn(personOptional);
        Optional<Person> person = personService.getOptionalPerson(token);
        assertThat(person.get()).isNotNull();
        assertThat(person.get().getToken()).isEqualTo(token);
        verify(personRepository).findByToken(token);
        verifyNoMoreInteractions(personRepository);
    }

    @Test
    void ensureGetPersonByNotExistingTokenReturnsEmptyOptional() {
        when(personRepository.findByToken(any())).thenReturn(Optional.empty());
        Optional<Person> person = personService.getOptionalPerson("TOKEN");
        assertThat(person.isEmpty()).isTrue();
        verify(personRepository).findByToken(any());
    }

    @Test
    void ensureGettingPersonAddressTextWorksProperly() {
        Person person = personFixtures.getPersonFixture1();
        String token = PersonFixtures.DEFAULT_TOKEN;
        when(personRepository.findByToken(token)).thenReturn(Optional.of(person));
        String addressText = personService.getPersonAddressText(token);
        assertThat(addressText).isNotNull();
        assertThat(addressText).contains(person.getFullAddressText());
    }

    @Test
    void ensureGetAddressOfUnkownPersonThrowsException() {
        assertThrows(NotFoundException.class, () -> personService.getPersonAddressText("OTHER-TOKEN"));
    }

    @Test
    void ensureGetPersonsWorksProperly() {
        Person p1 = personFixtures.getPersonFixture1();
        Person p2 = personFixtures.getPersonFixture2();
        when(personRepository.findAll()).thenReturn(List.of(p1, p2));
        List<Person> persons = personService.getPersons();
        assertThat(persons.size()).isEqualTo(2);
        verify(personRepository).findAll();
    }

    @Test
    void ensureReplacingByTokenWorks() {
        String token = PersonFixtures.DEFAULT_TOKEN;
        when(personRepository.existsByToken(token)).thenReturn(true);
        CreatePersonCommand createPersonCommand = personFixtures.getCreatePersonCommandFixture();
        Person newPerson = personService.replacePerson(token, createPersonCommand);
        assertThat(newPerson).isNotNull();
        assertThat(newPerson.getToken()).isEqualTo(token);
        verify(personRepository).existsByToken(any());
        verify(personRepository).save(any());
    }

    @Test
    void ensureReplacingByNotExistingTokenThrowsException() {
        String token = PersonFixtures.DEFAULT_TOKEN;
        when(personRepository.existsByToken(token)).thenReturn(false);
        CreatePersonCommand createPersonCommand = personFixtures.getCreatePersonCommandFixture();
        assertThrows(NotFoundException.class, () -> personService.replacePerson(token, createPersonCommand));
    }

    @Test
    void ensureAddPrimaryContactToPersonWorksProperlyForKnownContactType() {
        String personToken = PersonFixtures.DEFAULT_TOKEN;
        Person p1 = personFixtures.getPersonFixture2();
        String means = PersonFixtures.DEFAULT_MEANS;
        ContactType contactType = personFixtures.getContactTypeFixture();
        CreateContactCommand createContactCommand = personFixtures.getCreateContactCommandFixture();
        when(personRepository.findByToken(personToken)).thenReturn(Optional.of(p1));
        when(contactTypeService.getContactTypeOptional(means)).thenReturn(Optional.of(contactType));
        String contactToken = "CONTACT-TOKEN-1";
        when(tokenService.createNanoIdWithType(any())).thenReturn(contactToken);
        LocalDateTime ts = LocalDateTime.now();
        when(temporalValueFactory.now()).thenReturn(ts);
        Contact contact = personService.attachPrimaryContactToPerson(personToken, createContactCommand);
        assertThat(contact).isNotNull();
        assertThat(contact.getToken()).isEqualTo(contactToken);
        assertThat(contact.getCreatedAt()).isEqualTo(ts);
        assertThat(contact.getContactType()).isEqualTo(contactType);
        assertThat(contact.getValue()).isEqualTo(createContactCommand.getAddress());
        assertThat(p1.getContacts().size()).isEqualTo(1);
        assertThat(p1.getPrimaryContact()).isEqualTo(contact);
        verify(contactRepository).save(any());
        verify(personRepository).save(any());
    }

    @Test
    void ensureAddContactToPersonWorksProperlyForKnownContactType() {
        String personToken = PersonFixtures.DEFAULT_TOKEN;
        Person p1 = personFixtures.getPersonFixture2();
        String means = PersonFixtures.DEFAULT_MEANS;
        ContactType contactType = personFixtures.getContactTypeFixture();
        CreateContactCommand createContactCommand = personFixtures.getCreateContactCommandFixture();
        when(personRepository.findByToken(personToken)).thenReturn(Optional.of(p1));
        when(contactTypeService.getContactTypeOptional(means)).thenReturn(Optional.of(contactType));
        String contactToken = "CONTACT-TOKEN-1";
        when(tokenService.createNanoIdWithType(any())).thenReturn(contactToken);
        LocalDateTime ts = LocalDateTime.now();
        when(temporalValueFactory.now()).thenReturn(ts);
        Contact contact = personService.attachContactToPerson(personToken, createContactCommand, false);
        assertThat(contact).isNotNull();
        assertThat(contact.getToken()).isEqualTo(contactToken);
        assertThat(contact.getCreatedAt()).isEqualTo(ts);
        assertThat(contact.getContactType()).isEqualTo(contactType);
        assertThat(contact.getValue()).isEqualTo(createContactCommand.getAddress());
        assertThat(p1.getPrimaryContact()).isNull();
        assertThat(p1.getContacts().size()).isEqualTo(1);
        verify(contactRepository).save(any());
        verify(personRepository).save(any());
    }

    @Test
    void ensureAddContactToPersonWorksProperlyForUnknownContactType() {
        Person p1 = personFixtures.getPersonFixture1();
        CreateContactCommand createContactCommand = personFixtures.getCreateContactCommandFixture();
        ContactType contactType = personFixtures.getContactTypeFixture();
        when(contactTypeService.getContactTypeOptional(createContactCommand.getContactType())).thenReturn(Optional.empty());
        String contactToken = "CONTACT-TOKEN-1";
        when(tokenService.createNanoIdWithType(any())).thenReturn(contactToken);
        LocalDateTime ts = LocalDateTime.now();
        when(temporalValueFactory.now()).thenReturn(ts);
        when(contactTypeService.createContactType(any())).thenReturn(contactType);
        Contact contact = personService.attachContactToPerson(p1, createContactCommand, false);
        assertThat(contact).isNotNull();
        assertThat(contact.getToken()).isEqualTo(contactToken);
        assertThat(contact.getCreatedAt()).isEqualTo(ts);
        assertThat(contact.getContactType()).isEqualTo(contactType);
        assertThat(contact.getValue()).isEqualTo(createContactCommand.getAddress());
        assertThat(p1.getPrimaryContact()).isNull();
        assertThat(p1.getContacts().size()).isEqualTo(1);
        verify(contactRepository).save(any());
        verify(personRepository).save(any());
        verify(contactTypeService).createContactType(any());
    }

    @Test
    void ensureGetContactsForPersonWorksProperly() {
        String token = PersonFixtures.DEFAULT_TOKEN;
        Person p1 = personFixtures.getPersonFixture1();
        Contact contact = personFixtures.getContactFixture();
        p1.addContact(contact);
        when(personRepository.findByToken(token)).thenReturn(Optional.of(p1));
        Set<Contact> conacts = personService.getContactsForPerson(token);
        assertThat(conacts.size()).isEqualTo(1);
    }

   @Test
   void ensureFindContactByTokenWorksProperly() {
        Contact contact = personFixtures.getContactFixture();
        String token = contact.getToken();
        when(contactRepository.findByToken(token)).thenReturn(Optional.of(contact));
        Contact foundContact = personService.findContactByToken(token);
        assertThat(foundContact).isEqualTo(contact);
   }

    @Test
    void ensureFindContactByTokenThrowsErrorForUnknownContact() {
        when(contactRepository.findByToken(any())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> personService.findContactByToken("unknown"));
    }

    @Test
    void ensureDeletePrimaryContactForPersonWorksProperly() {
        String token = PersonFixtures.DEFAULT_TOKEN;
        Person p1 = personFixtures.getPersonFixture1();
        Contact contact = personFixtures.getContactFixture();
        p1.setPrimaryContact(contact);
        p1.addContact(contact);
        when(personRepository.findByToken(token)).thenReturn(Optional.of(p1));
        assertThat(p1.getPrimaryContact()).isNotNull();
        assertThat(p1.getPrimaryContact()).isEqualTo(contact);
        personService.deletePrimaryContactForPerson(token);
        assertThat(p1.getPrimaryContact()).isNull();
    }

    @Test
    void ensureDeleteContactsForPersonWorks() {
        String token = PersonFixtures.DEFAULT_TOKEN;
        Person p1 = personFixtures.getPersonFixture1();
        Contact contact = personFixtures.getContactFixture();
        p1.setPrimaryContact(contact);
        p1.addContact(contact);
        when(personRepository.findByToken(token)).thenReturn(Optional.of(p1));
        assertThat(p1.getPrimaryContact()).isNotNull();
        assertThat(p1.getPrimaryContact()).isEqualTo(contact);
        assertThat(p1.getContacts().size()).isEqualTo(1);
        personService.deleteContactsForPerson(token);
        assertThat(p1.getPrimaryContact()).isNull();
        assertThat(p1.getContacts().size()).isEqualTo(0);
    }

    @Test
    void ensureDeleteContactForPersonWorksProperly() {
        String contactToken = "CONTACT-TOKEN";
        String personToken = PersonFixtures.DEFAULT_TOKEN;
        Person p1 = personFixtures.getPersonFixture1();
        Contact contact = personFixtures.getContactFixture();
        p1.setPrimaryContact(contact);
        p1.addContact(contact);
        when(personRepository.findByToken(personToken)).thenReturn(Optional.of(p1));
        when(contactRepository.findByToken(contactToken)).thenReturn(Optional.of(contact));
        assertThat(p1.getPrimaryContact()).isNotNull();
        assertThat(p1.getPrimaryContact()).isEqualTo(contact);
        assertThat(p1.getContacts().size()).isEqualTo(1);
        personService.deleteContactForPerson(personToken, contactToken);
        assertThat(p1.getPrimaryContact()).isNull();
        assertThat(p1.getContacts().size()).isEqualTo(0);
    }

    @Test
    void ensureEditContactAddressForPersonWorksProperly() {
        Contact contact = personFixtures.getContactFixture();
        String contactToken = contact.getToken();
        String newAddressName = "mitterdorfer.moritz@gmail.com";
        when(contactRepository.findByToken(contactToken)).thenReturn(Optional.of(contact));
        Contact newContact = personService.editContactAddressForPerson(contactToken, newAddressName);
        assertThat(newContact.getValue()).isEqualTo(newAddressName);
        verify(contactRepository).save(any());
    }

}
