package com.f2cm.eventmanager.service.types.people;

import com.f2cm.eventmanager.domain.people.Contact;
import com.f2cm.eventmanager.domain.people.ContactType;
import com.f2cm.eventmanager.domain.people.Person;
import com.f2cm.eventmanager.domain.places.Address;
import com.f2cm.eventmanager.foundation.logging.CrudLogger;
import com.f2cm.eventmanager.foundation.time.TemporalValueFactory;
import com.f2cm.eventmanager.persistence.types.people.ContactRepository;
import com.f2cm.eventmanager.persistence.types.people.PersonRepository;
import com.f2cm.eventmanager.service.TokenService;
import com.f2cm.eventmanager.service.dtos.commands.CreateContactCommand;
import com.f2cm.eventmanager.service.dtos.commands.CreateContactTypeCommand;
import com.f2cm.eventmanager.service.dtos.commands.CreatePersonCommand;
import com.f2cm.eventmanager.service.exceptions.NotFoundException;
import com.f2cm.eventmanager.service.exceptions.ServiceException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.f2cm.eventmanager.foundation.ensurer.EnsurerFactory.ensureThat;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class PersonService {

    private final PersonRepository personRepository;
    private final ContactRepository contactRepository;
    private final TemporalValueFactory temporalValueFactory;
    private final TokenService tokenService;
    private final ContactTypeService contactTypeService;

    private final String TYPE_PERSON = "person";
    private final String TYPE_CONTACT = "contact";

    private final CrudLogger crudLogger = new CrudLogger(this.getClass(), Person.class);
    private final CrudLogger contactCrudLogger = new CrudLogger(this.getClass(), Contact.class);

    public Optional<Person> getOptionalPerson(String token) {
        crudLogger.readingByToken(token);
        return personRepository.findByToken(token);
    }

    public Person getPerson(String token) {
        crudLogger.readingByToken(token);
        return _findPersonByToken(token);
    }

    public String getPersonAddressText(String token) {
        crudLogger.readingByToken(token);
        ensureThat(token).isNotBlank();
        Person person = _findPersonByToken(token);
        return person.getFullAddressText();
    }

    public List<Person> getPersons() {
        crudLogger.readingAll();
        return personRepository.findAll();
    }

    public Person createPerson(CreatePersonCommand createPersonCommand) {
        crudLogger.creating();
        ensureThat(createPersonCommand).isNotNull();
        return _createPerson(Optional.empty(), createPersonCommand);
    }

    public Person replacePerson(String token, CreatePersonCommand createPersonCommand) {
        crudLogger.updatingByToken(token);
        ensureThat(token).isNotBlank();
        ensureThat(createPersonCommand).isNotNull();
        if(personRepository.existsByToken(token)) {
            personRepository.deleteByToken(token);
            personRepository.flush();
            return _createPerson(Optional.of(token), createPersonCommand);
        }
        throw NotFoundException.cannotFindEntityByToken(Person.class, token);
    }

    public void deletePerson(String token) {
        crudLogger.deletingByToken(token);
        ensureThat(token).isNotBlank();
        personRepository.deleteByToken(token);
        deleteContactsForPerson(token);
    }

    public void deletePersons() {
        crudLogger.deletingAll();
        personRepository.deleteAll();
    }

    public Contact attachPrimaryContactToPerson(String personToken, CreateContactCommand createContactCommand) {
        log.trace("Attaching primary contact to person with token [{}]", personToken);
        return attachContactToPerson(personToken, createContactCommand, true);
    }

    public Contact attachContactToPerson(String personToken, CreateContactCommand createContactCommand, boolean isPrimary) {
        log.trace("Attaching contact to person with token [{}]", personToken);
        ensureThat(personToken).isNotBlank();
        ensureThat(createContactCommand).isNotNull();
        Person person = _findPersonByToken(personToken);
        return attachContactToPerson(person, createContactCommand, isPrimary);
    }

    @Transactional
    public Contact attachContactToPerson(Person person, CreateContactCommand createContactCommand, boolean isPrimary) {
        log.trace("Attaching contact to person with token [{}]", person.getToken());
        ensureThat(person).isNotNull();
        ensureThat(createContactCommand).isNotNull();
        Optional<ContactType> contactTypeOptional = contactTypeService.getContactTypeOptional(createContactCommand.getContactType());
        ContactType contactType = contactTypeOptional.isPresent() ? contactTypeOptional.get() : contactTypeService.createContactType(
                new CreateContactTypeCommand(createContactCommand.getContactType(), createContactCommand.isContactTypeIsSocialNetwork())
        );
        String token = tokenService.createNanoIdWithType(TYPE_CONTACT);
        contactCrudLogger.creating();
        Contact contact = Contact.builder()
                .createdAt(temporalValueFactory.now())
                .token(token)
                .contactType(contactType)
                .value(createContactCommand.getAddress())
                .business(createContactCommand.isBusiness())
                .build();
        try {
            contactRepository.save(contact);
            contactCrudLogger.persistedWithToken(contact.getToken());
        } catch (PersistenceException persistenceException) {
            var error = ServiceException.cannotCreateEntity(Contact.class, token, persistenceException);
            log.warn("Caught exception", error);
            throw error;
        } catch (Throwable t) {
            var error = ServiceException.cannotCreateEntityForUndeterminedReason(Contact.class, token, t);
            log.error("Caught unexpected exception", error);
            throw error;
        }
        _addContactToPerson(person, contact);
        if(isPrimary) {
            if(person.getPrimaryContact() != null) {
                log.info("Overwriting default contact {} of person with token [{}] with new contact {}", person.getPrimaryContact().getValue(), person.getToken(), contact.getValue());
            }
            person.setPrimaryContact(contact);
        }
        personRepository.save(person);
        return contact;
    }

    public Set<Contact> getContactsForPerson(String token) {
        log.trace("Removing contacts from Person with token [{}]", token);
        ensureThat(token).isNotBlank();
        Person person = _findPersonByToken(token);
        return person.getContacts();
    }

    public void deletePrimaryContactForPerson(String token) {
        log.trace("Removing primary contact from Person with token [{}]", token);
        ensureThat(token).isNotBlank();
        Person person = _findPersonByToken(token);
        if (person.getPrimaryContact() == null) {
            log.warn("Not removing primary contact of person with token [{}], because it is already null", token);
            return;
        }
        person.setPrimaryContact(null);
        personRepository.save(person);
    }

    public void deleteContactsForPerson(String token) {
        log.trace("Removing contacts from Person with token [{}]", token);
        ensureThat(token).isNotBlank();
        Person person = _findPersonByToken(token);
        person.clearContacts();
        personRepository.save(person);
    }

    public void deleteContactForPerson(String personToken, String contactToken) {
        log.trace("Removing contact with token [{}] from Person with token [{}]", personToken, contactToken);
        ensureThat(personToken).isNotBlank();
        ensureThat(contactToken).isNotBlank();
        Person person = _findPersonByToken(personToken);
        Contact contact = _findContactByToken(contactToken);
        person.removeContact(contact);
        personRepository.save(person);
        contactRepository.deleteContactByToken(contactToken);
    }

    public Contact editContactAddressForPerson(String contactToken, String newAddress) {
        contactCrudLogger.updatingByToken(contactToken);
        ensureThat(contactToken).isNotBlank();
        ensureThat(newAddress).isNotBlank();
        ensureThat(newAddress).isNotBlank();
        Contact contact = _findContactByToken(contactToken);
        contact.setValue(newAddress);
        contactRepository.save(contact);
        return contact;
    }

    public Contact findContactByToken(String token) {
        contactCrudLogger.readingByToken(token);
        ensureThat(token).isNotBlank();
        return _findContactByToken(token);
    }

    private Person _addContactToPerson(Person person, Contact contact) {
        ensureThat(person).isNotNull();
        ensureThat(contact).isNotNull();
        if(!person.addContact(contact)) {
            log.warn("Not adding contact with token [{}] to person with token [{}] because it is already present", contact.getToken(), person.getToken());
        }
        return person;
    }

    private Contact _findContactByToken(String token) {
        ensureThat(token).isNotBlank();
        Optional<Contact> contact = contactRepository.findByToken(token);
        contact.orElseThrow(() -> NotFoundException.cannotFindEntityByToken(Contact.class, token));
        return contact.get();
    }

    private Person _findPersonByToken(String token) {
        ensureThat(token).isNotBlank();
        Optional<Person> person = personRepository.findByToken(token);
        person.orElseThrow(() -> NotFoundException.cannotFindEntityByToken(Person.class, token));
        return person.get();
    }

    private Person _createPerson(Optional<String> token, CreatePersonCommand createPersonCommand) {
        ensureThat(token).isNotNull();
        ensureThat(createPersonCommand).isNotNull();
        Optional<Person> personOptional = personRepository.findByFirstNameAndLastName(createPersonCommand.getFirstName(), createPersonCommand.getLastName());
        if(personOptional.isPresent())
            return personOptional.get();
        LocalDateTime localDateTime = temporalValueFactory.now();
        String personToken = token.orElse(tokenService.createNanoIdWithType(TYPE_PERSON));
        Address address = Address.builder()
                .city(createPersonCommand.getCity())
                .country(createPersonCommand.getCountry())
                .streetNumber(createPersonCommand.getStreetNumber())
                .zipCode(createPersonCommand.getZipCode())
                .build();
        Person person = Person.builder()
                .token(personToken)
                .address(address)
                .birthday(createPersonCommand.getBirthday())
                .firstName(createPersonCommand.getFirstName())
                .lastName(createPersonCommand.getLastName())
                .titleBack(createPersonCommand.getTitleBack())
                .titleFront(createPersonCommand.getTitleFront())
                .gender(createPersonCommand.getGender())
                .mainJob(createPersonCommand.getMainJob())
                .createdAt(localDateTime)
                .build();
        try {
            personRepository.save(person);
            return person;
        } catch (PersistenceException persistenceException) {
            var error = ServiceException.cannotCreateEntity(Person.class, personToken, persistenceException);
            log.warn("Caught exception", error);
            throw error;
        } catch (Throwable t) {
            var error = ServiceException.cannotCreateEntityForUndeterminedReason(Person.class, personToken, t);
            log.error("Caught unexpected exception", error);
            throw error;
        }
    }

}
