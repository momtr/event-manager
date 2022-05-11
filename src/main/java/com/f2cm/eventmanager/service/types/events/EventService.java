package com.f2cm.eventmanager.service.types.events;

import com.f2cm.eventmanager.domain.events.Event;
import com.f2cm.eventmanager.domain.events.EventType;
import com.f2cm.eventmanager.domain.events.Tag;
import com.f2cm.eventmanager.foundation.logging.CrudLogger;
import com.f2cm.eventmanager.persistence.types.events.EventRepository;
import com.f2cm.eventmanager.persistence.types.events.TagRepository;
import com.f2cm.eventmanager.service.TokenService;
import com.f2cm.eventmanager.service.dtos.commands.CreateEventCommand;
import com.f2cm.eventmanager.service.dtos.objs.events.EventsOverviewDto;
import com.f2cm.eventmanager.service.exceptions.NotFoundException;
import com.f2cm.eventmanager.service.exceptions.ServiceException;
import com.f2cm.eventmanager.service.types.people.PersonService;
import com.f2cm.eventmanager.service.types.places.LocationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.f2cm.eventmanager.foundation.ensurer.EnsurerFactory.ensureThat;

@Slf4j
@AllArgsConstructor
@Service
@Transactional
public class EventService {

    private final EventRepository eventRepository;
    private final TagRepository tagRepository;
    private final PersonService personService;
    private final LocationService locationService;
    private final TokenService tokenService;

    private final String TYPE = "event";

    private final CrudLogger crudLogger = new CrudLogger(this.getClass(), Event.class);

    public Event getEvent(String token) {
        crudLogger.readingByToken(token);
        ensureThat(token).isNotNull("token must not be null");
        return eventRepository.findByToken(token)
                .orElseThrow(() -> NotFoundException.cannotFindEntityByToken(Event.class, token));
    }

    public Optional<Event> getOptionalEvent(String token) {
        crudLogger.readingByToken(token);
        ensureThat(token).isNotNull("token must not be null");
        return eventRepository.findByToken(token);
    }

    public List<Event> getEvents() {
        crudLogger.readingAll();
        return eventRepository.findAll();
    }

    public Set<Event> getEventsByTagNames(String[] tags) {
        crudLogger.readingByMeans("tags", tags);
        return eventRepository.findEventsByTagNames(tags);
    }

    public List<Event> getEventsByType(EventType t) {
        log.trace("Reading all {} events", t);
        return eventRepository.findByEventType(t);
    }

    @Transactional
    public void deleteEvent(String token) {
        crudLogger.deletingByToken(token);
        ensureThat(token).isNotNull("token must not be null");
        eventRepository.deleteByToken(token);
    }

    @Transactional
    public void deleteEvents() {
        crudLogger.deletingAll();
        eventRepository.deleteAll();
    }

    public Set<Tag> getTagsForEvent(String token) {
        log.trace("Getting all tags from event with token {}", token);
        return getEvent(token).getTags();
    }

    public Event removeTagFromEvent(String token, String name) {
        log.trace("Removing tag '{}' from event with token {}", name, token);
        ensureThat(token).isNotNull("token must not be null");
        ensureThat(name).isNotNull("name must not be null");

        var event = getEvent(token);
        var tag = _getOrCreateTagByName(name);

        event.removeTag(tag);

        return eventRepository.save(event);
    }

    public Event addTagToEvent(String token, String name) {
        log.trace("Adding tag '{}' to event with token {}", name, token);
        ensureThat(token).isNotNull("token must not be null");
        ensureThat(name).isNotNull("name must not be null");

        var event = getEvent(token);
        var tag = _getOrCreateTagByName(name);

        event.addTag(tag);

        return eventRepository.save(event);
    }

    public Event updateEvent(String token, CreateEventCommand createEventCommand) {
        crudLogger.updatingByToken(token);
        ensureThat(token).isNotNull("token must not be null");
        ensureThat(createEventCommand).isNotNull("createEventCommand must not be null");

        var event = getEvent(token);

        var eventLocation = (createEventCommand.getLocationToken() != null) ? locationService.getLocation(createEventCommand.getLocationToken()) : null;
        var eventOrganizer = (createEventCommand.getOrganizerToken() != null) ? personService.getPerson(createEventCommand.getOrganizerToken()) : null;
        var eventTags = createEventCommand.getTagNames().stream().map(this::_getOrCreateTagByName).toList();

        event.setName(createEventCommand.getName());
        event.setStartDate(createEventCommand.getStartDate());
        event.setEndDate(createEventCommand.getEndDate());
        event.setLuxury(createEventCommand.isLuxury());
        event.setWasHold(createEventCommand.isWasHold());
        event.setEventType(createEventCommand.getEventType());
        event.setLocation(eventLocation);
        event.setOrganizer(eventOrganizer);

        event.clearTags().addTags(eventTags.toArray(new Tag[0]));

        try {
            eventRepository.save(event);
            return event;
        } catch (PersistenceException pe) {
            throw ServiceException.cannotCreateEntity(Event.class, token, pe);
        } catch (Exception e) {
            throw ServiceException.cannotCreateEntityForUndeterminedReason(Event.class, token, e);
        }
    }

    public Event createEvent(CreateEventCommand createEventCommand) {
        crudLogger.creating();
        ensureThat(createEventCommand).isNotNull("createEventCommand must not be null");

        var eventToken = tokenService.createNanoIdWithType(TYPE);
        var eventLocation = (createEventCommand.getLocationToken() != null) ? locationService.getLocation(createEventCommand.getLocationToken()) : null;
        var eventOrganizer = (createEventCommand.getOrganizerToken() != null) ? personService.getPerson(createEventCommand.getOrganizerToken()) : null;
        var eventTags = createEventCommand.getTagNames().stream().map(this::_getOrCreateTagByName).toList();


        var newEvent = Event.builder()
                .token(eventToken)
                .name(createEventCommand.getName())
                .startDate(createEventCommand.getStartDate())
                .endDate(createEventCommand.getEndDate())
                .wasHold(createEventCommand.isWasHold())
                .luxury(createEventCommand.isLuxury())
                .location(eventLocation)
                .organizer(eventOrganizer)
                .eventType(createEventCommand.getEventType())
                .build();

        newEvent.addTags(eventTags.toArray(new Tag[0]));

        try {
            eventRepository.save(newEvent);
            crudLogger.persistedWithToken(newEvent.getToken());
            return newEvent;
        } catch (PersistenceException pe) {
            var error = ServiceException.cannotCreateEntity(Event.class, eventToken, pe);
            log.warn("Caught exception", error);
            throw error;
        } catch (Exception e) {
            var error = ServiceException.cannotCreateEntityForUndeterminedReason(Event.class, eventToken, e);
            log.error("Caught unexpected exception", error);
            throw error;
        }
    }

    public EventsOverviewDto getEventsOverviewAfter(LocalDate after) {
        return EventsOverviewDto.of(eventRepository.findAllByStartDateAfter(after));
    }

    private Tag _getOrCreateTagByName(String name) {
        return tagRepository.findByName(name)
                .orElseGet(() -> {
                    log.info("Creating tag with name {} because it does not exist yet", name);
                    return tagRepository.save(Tag.builder().name(name).build());
                });
    }

}
