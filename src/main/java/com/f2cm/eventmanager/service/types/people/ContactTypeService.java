package com.f2cm.eventmanager.service.types.people;

import com.f2cm.eventmanager.domain.people.ContactType;
import com.f2cm.eventmanager.domain.places.Location;
import com.f2cm.eventmanager.foundation.logging.CrudLogger;
import com.f2cm.eventmanager.foundation.time.TemporalValueFactory;
import com.f2cm.eventmanager.persistence.types.people.ContactRepository;
import com.f2cm.eventmanager.persistence.types.people.ContactTypeRepository;
import com.f2cm.eventmanager.service.dtos.commands.CreateContactTypeCommand;
import com.f2cm.eventmanager.service.dtos.objs.people.UpdateContactTypeSocialNetworkCommand;
import com.f2cm.eventmanager.service.exceptions.CannotDeleteEntityException;
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

import static com.f2cm.eventmanager.foundation.ensurer.EnsurerFactory.ensureThat;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class ContactTypeService {

    private final ContactTypeRepository contactTypeRepository;
    private final ContactRepository contactRepository;
    private final TemporalValueFactory temporalValueFactory;

    private final CrudLogger crudLogger = new CrudLogger(this.getClass(), ContactType.class);

    public Optional<ContactType> getContactTypeOptional(String means) {
        crudLogger.readingByMeans("means", means);
        ensureThat(means).isNotBlank();
        return contactTypeRepository.findByMeans(means);
    }

    public ContactType getContactType(String means) {
        crudLogger.readingByMeans("means", means);
        ensureThat(means).isNotBlank();
        Optional<ContactType> contactTypeOptional = contactTypeRepository.findByMeans(means);
        contactTypeOptional.orElseThrow(() -> NotFoundException.cannotFindEntityByIdentifier(ContactType.class, means));
        return contactTypeOptional.get();
    }

    public List<ContactType> getContactTypes() {
        return contactTypeRepository.findAll();
    }

    public void deleteContactTypes() {
        crudLogger.deletingAll();
        contactTypeRepository.deleteAll();
        log.info("Make sure cross-dependencies were not deleted!");
    }

    public void deleteContactType(String means) {
        crudLogger.deletingByMeans("means", means);
        ensureThat(means).isNotBlank();
        ContactType contactType = getContactType(means);
        long countContactsForContactType = contactRepository.countAllByContactType(contactType);
        if(countContactsForContactType != 0) {
            log.info("Cannot delete contact type as there are {} dependencies in Contacts", countContactsForContactType);
            throw CannotDeleteEntityException.cannotDeleteEntityByIdentifier(ContactType.class, means);
        } else {
            contactTypeRepository.deleteByMeans(means);
        }
    }

    public ContactType createContactType(CreateContactTypeCommand createContactTypeCommand) {
        crudLogger.creating();
        ensureThat(createContactTypeCommand).isNotNull();
        Optional<ContactType> existingContactType = contactTypeRepository.findByMeans(createContactTypeCommand.getMeans());
        if(existingContactType.isPresent())
            return existingContactType.get();
        LocalDateTime ts = temporalValueFactory.now();
        ContactType contactType = ContactType.builder()
                .means(createContactTypeCommand.getMeans())
                .socialNetwork(createContactTypeCommand.isSocialNetwork())
                .createdAt(ts)
                .build();
        try {
            contactTypeRepository.save(contactType);
            crudLogger.persistedWithId(contactType.getId());
            return contactType;
        } catch (PersistenceException persistenceException) {
            var error = ServiceException.cannotCreateEntity(ContactType.class, createContactTypeCommand.getMeans(), persistenceException);
            log.warn("Caught exception", error);
            throw error;
        } catch (Throwable t) {
            var error = ServiceException.cannotCreateEntityForUndeterminedReason(ContactType.class, createContactTypeCommand.getMeans(), t);
            log.error("Caught unexpected exception", error);
            throw error;
        }
    }

    public ContactType partiallyUpdateContactType(String means, UpdateContactTypeSocialNetworkCommand updateContactTypeSocialNetworkCommand) {
        ensureThat(updateContactTypeSocialNetworkCommand).isNotNull();
        ContactType contactType = getContactType(means);
        contactType.setSocialNetwork(updateContactTypeSocialNetworkCommand.isSocialNetwork());
        contactTypeRepository.save(contactType);
        return contactType;
    }

}
