package com.f2cm.eventmanager.service.types.people;

import com.f2cm.eventmanager.domain.people.EventRole;
import com.f2cm.eventmanager.foundation.logging.CrudLogger;
import com.f2cm.eventmanager.foundation.time.TemporalValueFactory;
import com.f2cm.eventmanager.persistence.types.people.EventParticipantRepository;
import com.f2cm.eventmanager.persistence.types.people.EventRoleRepository;
import com.f2cm.eventmanager.service.SlugService;
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
public class EventRoleService {

    private final EventRoleRepository eventRoleRepository;
    private final EventParticipantRepository eventParticipantRepository;
    private TemporalValueFactory temporalValueFactory;
    private SlugService slugService;
    private final CrudLogger crudLogger = new CrudLogger(this.getClass(), EventRole.class);

    public EventRole createEventRole(String name) {
        crudLogger.creating();
        ensureThat(name).isNotBlank();
        Optional<EventRole> eventRoleToCheck = eventRoleRepository.findEventRoleByName(name);
        if(eventRoleToCheck.isPresent()) {
           // throw AlreadyExistsException.entityAlreadyExists(EventRole.class, "name", name);
            return eventRoleToCheck.get();
        } else {
            LocalDateTime ts = temporalValueFactory.now();
            String slug = slugService.generateSlugAsToken(name);
            EventRole eventRole = EventRole.builder()
                    .createdAt(ts)
                    .name(name)
                    .slug(slug)
                    .build();
            try {
                eventRoleRepository.save(eventRole);
            } catch (PersistenceException persistenceException) {
                var error = ServiceException.cannotCreateEntity(EventRole.class, slug, persistenceException);
                log.warn("Caught exception", error);
                throw error;
            } catch (Throwable t) {
                var error = ServiceException.cannotCreateEntityForUndeterminedReason(EventRole.class, slug, t);
                log.error("Caught unexpected exception", error);
                throw error;
            }
            crudLogger.persistedWithId(eventRole.getId());
            return eventRole;
        }
    }

    public Optional<EventRole> getEventRoleOptional(String slug) {
        log.trace("Reading event role by slug {}", slug);
        return eventRoleRepository.findBySlug(slug);
    }

    public EventRole getEventRole(String slug) {
        log.trace("Reading event role by slug {}", slug);
        Optional<EventRole> eventRoleOptional = eventRoleRepository.findBySlug(slug);
        eventRoleOptional.orElseThrow(() -> NotFoundException.cannotFindEntityByIdentifier(EventRole.class, slug));
        return eventRoleOptional.get();
    }

    @Transactional
    public void deleteEventRole(String slug) {
        log.trace("Reading event role by slug {}", slug);
        EventRole eventRole = getEventRole(slug);
        long numOfEventDependencies = eventParticipantRepository.countAllByEventRole(eventRole);
        if(numOfEventDependencies != 0) {
            log.info("Could not delete Event Role as there are {} cross dependencies to Event Participants", numOfEventDependencies);
            throw CannotDeleteEntityException.cannotDeleteEntityByIdentifier(EventRole.class, slug);
        } else {
            eventRoleRepository.deleteBySlug(slug);
        }
    }

    public void deleteEventRoles() {
        eventRoleRepository.deleteAll();
    }

    public List<EventRole> getEventRoles() {
        return eventRoleRepository.findAll();
    }

}
