package com.f2cm.eventmanager.service.types.events;

import com.f2cm.eventmanager.domain.events.TimeSlot;
import com.f2cm.eventmanager.foundation.logging.CrudLogger;
import com.f2cm.eventmanager.persistence.types.events.TimeSlotRepository;
import com.f2cm.eventmanager.service.TokenService;
import com.f2cm.eventmanager.service.dtos.commands.CreateTimeSlotCommand;
import com.f2cm.eventmanager.service.exceptions.NotFoundException;
import com.f2cm.eventmanager.service.exceptions.ServiceException;
import com.f2cm.eventmanager.service.types.people.PersonService;
import com.f2cm.eventmanager.service.types.places.LocationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static com.f2cm.eventmanager.foundation.ensurer.EnsurerFactory.ensureThat;

@AllArgsConstructor
@Service
@Slf4j
public class TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    private final PersonService personService;
    private final LocationService locationService;
    private final TokenService tokenService;
    private final EventService eventService;

    private final String TYPE = "timeslot";

    private final CrudLogger crudLogger = new CrudLogger(this.getClass(), TimeSlot.class);

    public TimeSlot getTimeSlotByToken(String token) {
        crudLogger.readingByToken(token);
        ensureThat(token).isNotNull("token must not be null");
        return timeSlotRepository.findByToken(token)
                .orElseThrow(() -> NotFoundException.cannotFindEntityByToken(TimeSlot.class, token));
    }

    public Optional<TimeSlot> getOptionalTimeSlotByToken(String token) {
        crudLogger.readingByToken(token);
        ensureThat(token).isNotNull("token must not be null");
        return timeSlotRepository.findByToken(token);
    }

    public List<TimeSlot> getTimeSlots() {
        crudLogger.readingAll();
        return timeSlotRepository.findAll();
    }

    public List<TimeSlot> getTimeSlotsByEventToken(String eventToken) {
        crudLogger.readingByMeans("event token", eventToken);
        ensureThat(eventToken).isNotNull();
        return timeSlotRepository.findByEventToken(eventToken);
    }

    @Transactional
    public void deleteTimeSlot(String token) {
        crudLogger.deletingByToken(token);
        ensureThat(token).isNotNull("token must not be null");
        timeSlotRepository.deleteByToken(token);
    }

    @Transactional
    public void deleteTimeSlots() {
        crudLogger.deletingAll();
        timeSlotRepository.deleteAll();
    }


    public TimeSlot updateTimeSlot(String token, CreateTimeSlotCommand createTimeSlotCommand) {
        crudLogger.updatingByToken(token);
        ensureThat(token).isNotNull("token must not be null");
        ensureThat(createTimeSlotCommand).isNotNull("createTimeSlotCommand must not be null");

        var timeSlotContact = personService.getPerson(createTimeSlotCommand.getContactToken());
        var timeSlotEvent = createTimeSlotCommand.getEventToken() != null ? eventService.getEvent(createTimeSlotCommand.getEventToken()) : null;

        var timeSlot = getTimeSlotByToken(token);

        timeSlot.setName(timeSlot.getName());
        timeSlot.setDescription(timeSlot.getDescription());
        timeSlot.setFrom(createTimeSlotCommand.getFrom());
        timeSlot.setTo(createTimeSlotCommand.getTo());
        timeSlot.setContact(timeSlotContact);
        timeSlot.setEvent(timeSlotEvent);

        try {
            timeSlotRepository.save(timeSlot);
            return timeSlot;
        } catch (PersistenceException pe) {
            throw ServiceException.cannotCreateEntity(TimeSlot.class, token, pe);
        } catch (Exception e) {
            throw ServiceException.cannotCreateEntityForUndeterminedReason(TimeSlot.class, token, e);
        }
    }

    public TimeSlot createTimeSlot(CreateTimeSlotCommand createTimeSlotCommand) {
        crudLogger.creating();
        ensureThat(createTimeSlotCommand).isNotNull("createTimeSlotCommand must not be null");

        var timeSlotToken = tokenService.createNanoIdWithType(TYPE);
        var timeSlotContact = createTimeSlotCommand.getContactToken() != null ? personService.getPerson(createTimeSlotCommand.getContactToken()) : null;
        var timeSlotEvent = createTimeSlotCommand.getEventToken() != null ? eventService.getEvent(createTimeSlotCommand.getEventToken()) : null;

        var timeSlot = TimeSlot.builder()
                .token(timeSlotToken)
                .name(createTimeSlotCommand.getName())
                .description(createTimeSlotCommand.getDescription())
                .from(createTimeSlotCommand.getFrom())
                .to(createTimeSlotCommand.getTo())
                .contact(timeSlotContact)
                .event(timeSlotEvent)
                .build();

        try {
            timeSlotRepository.save(timeSlot);
            crudLogger.persistedWithToken(timeSlot.getToken());
            return timeSlot;
        } catch (PersistenceException pe) {
            var error = ServiceException.cannotCreateEntity(TimeSlot.class, timeSlotToken, pe);
            log.warn("Caught exception", error);
            throw error;
        } catch (Exception e) {
            var error = ServiceException.cannotCreateEntityForUndeterminedReason(TimeSlot.class, timeSlotToken, e);
            log.error("Caught unexpected exception", error);
            throw error;
        }
    }
}
