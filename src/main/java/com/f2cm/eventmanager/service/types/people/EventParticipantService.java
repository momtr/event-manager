package com.f2cm.eventmanager.service.types.people;

import com.f2cm.eventmanager.domain.events.Event;
import com.f2cm.eventmanager.domain.events.TimeSlot;
import com.f2cm.eventmanager.domain.people.EventParticipant;
import com.f2cm.eventmanager.domain.people.EventRole;
import com.f2cm.eventmanager.domain.people.Person;
import com.f2cm.eventmanager.foundation.logging.CrudLogger;
import com.f2cm.eventmanager.foundation.time.TemporalValueFactory;
import com.f2cm.eventmanager.persistence.types.people.EventParticipantRepository;
import com.f2cm.eventmanager.service.TokenService;
import com.f2cm.eventmanager.service.dtos.commands.CreateEventParticipantCommand;
import com.f2cm.eventmanager.service.dtos.commands.UpdateEventParticipantCommand;
import com.f2cm.eventmanager.service.exceptions.NotFoundException;
import com.f2cm.eventmanager.service.exceptions.ServiceException;
import com.f2cm.eventmanager.service.types.events.EventService;
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
public class EventParticipantService {

    private final EventRoleService eventRoleService;
    private final PersonService personService;
    private final EventService eventService;
    private final EventParticipantRepository eventParticipantRepository;
    private final TokenService tokenService;
    private final TemporalValueFactory temporalValueFactory;

    private final String TYPE = "participant";

    private final CrudLogger crudLogger = new CrudLogger(this.getClass(), EventParticipant.class);

    public EventParticipant getEventParticipant(String eventParticipantToken) {
        crudLogger.readingByToken(eventParticipantToken);
        ensureThat(eventParticipantToken).isNotBlank();
        Optional<EventParticipant> eventParticipantOptional = eventParticipantRepository.findByToken(eventParticipantToken);
        eventParticipantOptional.orElseThrow(() -> NotFoundException.cannotFindEntityByToken(EventParticipant.class, eventParticipantToken));
        return eventParticipantOptional.get();
    }

    public Optional<EventParticipant> getEventParticipantOptional(String eventParticipantToken) {
        crudLogger.readingByToken(eventParticipantToken);
        ensureThat(eventParticipantToken).isNotBlank();
        return eventParticipantRepository.findByToken(eventParticipantToken);
    }

    public List<EventParticipant> getEventParticipantsForEvent(String eventToken) {
        crudLogger.readingByMeans("event token", eventToken);
        ensureThat(eventToken).isNotBlank();
        return eventParticipantRepository.findByEventToken(eventToken);
    }

    public List<EventParticipant> getEventParticipantsForPerson(String personToken) {
        crudLogger.readingByMeans("person token", personToken);
        ensureThat(personToken).isNotBlank();
        return eventParticipantRepository.findEventParticipantByPersonToken(personToken);
    }

    public List<EventParticipant> getEventParticipants() {
        crudLogger.readingAll();
        return eventParticipantRepository.findAll();
    }

    public void deleteEventParticipantsForEvent(String eventToken) {
        crudLogger.deletingAll();
        ensureThat(eventToken).isNotBlank();
        eventParticipantRepository.deleteEventParticipantsByEventToken(eventToken);
    }

    public void deleteEventParticipant(String eventParticipantToken) {
        crudLogger.deletingByToken(eventParticipantToken);
        ensureThat(eventParticipantToken).isNotBlank();
        eventParticipantRepository.deleteEventParticipantByToken(eventParticipantToken);
    }

    public EventParticipant registerEventParticipantForEvent(CreateEventParticipantCommand createEventParticipantCommand) {
        crudLogger.creating();
        ensureThat(createEventParticipantCommand).isNotNull();
        Event event = eventService.getEvent(createEventParticipantCommand.getEventToken());
        Person person = personService.getPerson(createEventParticipantCommand.getPersonToken());
        EventRole eventRole = eventRoleService.getEventRoleOptional(createEventParticipantCommand.getEventRoleSlug())
                .orElseGet(() -> eventRoleService.createEventRole(createEventParticipantCommand.getEventRoleSlug()));
        var eventParticipant = eventParticipantRepository.findByPersonAndEventAndEventRole(person, event, eventRole)
                .orElseGet(() -> _createEventParticipant(person, event, eventRole, createEventParticipantCommand));
        crudLogger.persistedWithToken(eventParticipant.getToken());
        return eventParticipant;
    }

    public EventParticipant updateEventParticipant(String eventParticipantToken, UpdateEventParticipantCommand updateEventParticipantCommand) {
        crudLogger.updatingByToken(eventParticipantToken);
        ensureThat(eventParticipantToken).isNotBlank();
        ensureThat(updateEventParticipantCommand).isNotNull();
        EventParticipant eventParticipant = getEventParticipant(eventParticipantToken);
        EventRole eventRole = eventRoleService.getEventRoleOptional(updateEventParticipantCommand.getEventRoleSlug())
                .orElseGet(() -> eventRoleService.createEventRole(updateEventParticipantCommand.getEventRoleSlug()));
        eventParticipant.setPaid(updateEventParticipantCommand.isPaid());
        eventParticipant.setInternal(updateEventParticipantCommand.isInternal());
        eventParticipant.setEventRole(eventRole);
        return eventParticipantRepository.save(eventParticipant);
    }

    private EventParticipant _createEventParticipant(Person person, Event event, EventRole eventRole, CreateEventParticipantCommand createEventParticipantCommand) {
        ensureThat(person).isNotNull();
        ensureThat(event).isNotNull();
        ensureThat(eventRole).isNotNull();
        ensureThat(createEventParticipantCommand).isNotNull();
        String token = tokenService.createNanoIdWithType(TYPE);
        LocalDateTime ts = temporalValueFactory.now();
        EventParticipant eventParticipant = EventParticipant.builder()
                .createdAt(ts)
                .token(token)
                .event(event)
                .eventRole(eventRole)
                .person(person)
                .internal(createEventParticipantCommand.isInternal())
                .paid(createEventParticipantCommand.isPaid())
                .build();
        try {
            eventParticipantRepository.save(eventParticipant);
        } catch (PersistenceException pe) {
            var error = ServiceException.cannotCreateEntity(TimeSlot.class, token, pe);
            log.warn("Caught exception", error);
            throw error;
        } catch (Exception e) {
            var error = ServiceException.cannotCreateEntityForUndeterminedReason(TimeSlot.class, token, e);
            log.error("Caught unexpected exception", error);
            throw error;
        }
        return eventParticipant;
    }

}
