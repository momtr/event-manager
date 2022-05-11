package com.f2cm.eventmanager.presentation.api.people;

import com.f2cm.eventmanager.domain.people.EventParticipant;
import com.f2cm.eventmanager.foundation.rest.UriFactory;
import com.f2cm.eventmanager.service.dtos.commands.CreateEventParticipantCommand;
import com.f2cm.eventmanager.service.dtos.commands.UpdateEventParticipantCommand;
import com.f2cm.eventmanager.service.dtos.objs.people.EventParticipantDto;
import com.f2cm.eventmanager.service.types.people.EventParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(EventParticipationRestController.BASE_URL)
@RequiredArgsConstructor
public class EventParticipationRestController {

    public static final String BASE_URL = "/api/v1/participants";

    private final EventParticipantService eventParticipantService;

    @GetMapping({ "", "/" })
    public HttpEntity<List<EventParticipantDto>> getEventParticipantsForEventOrPerson(
            @RequestParam(name = "event_token", required = false) String eventToken,
            @RequestParam(name = "person_token", required = false) String personToken) {
        if(eventToken != null && personToken != null) {
            return ResponseEntity.badRequest().build();
        }
        if(personToken == null && eventToken == null) {
            return ResponseEntity.ok(eventParticipantService.getEventParticipants()
                    .stream()
                    .map(EventParticipantDto::of)
                    .toList());
        }
        if(eventToken != null) {
            return ResponseEntity.ok(eventParticipantService.getEventParticipantsForEvent(eventToken)
                    .stream()
                    .map(EventParticipantDto::of)
                    .toList());
        } else {
            return ResponseEntity.ok(eventParticipantService.getEventParticipantsForPerson(personToken)
                    .stream()
                    .map(EventParticipantDto::of)
                    .toList());
        }
    }

    @GetMapping("/{token}")
    public HttpEntity<EventParticipantDto> getEventParticipant(@PathVariable String token) {
        return eventParticipantService.getEventParticipantOptional(token)
                .map(EventParticipantDto::of)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping({ "", "/" })
    public HttpEntity<EventParticipantDto> createEventParticipant(@RequestBody @Valid CreateEventParticipantCommand createEventParticipantCommand) {
        EventParticipant eventParticipant = eventParticipantService.registerEventParticipantForEvent(createEventParticipantCommand);
        URI uri = UriFactory.createUriWithSelfeReference(BASE_URL, eventParticipant.getToken());
        return ResponseEntity.created(uri).body(EventParticipantDto.of(eventParticipant));
    }

    @PutMapping("/{token}")
    public HttpEntity<EventParticipantDto> updateEventParticipant(@PathVariable String token, @RequestBody @Valid UpdateEventParticipantCommand updateEventParticipantCommand) {
        eventParticipantService.updateEventParticipant(token, updateEventParticipantCommand);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping({ "", "/" })
    public HttpEntity<EventParticipantDto> deleteEventParticipantsForEvent(@RequestParam(name = "event_token") String eventToken) {
        eventParticipantService.deleteEventParticipantsForEvent(eventToken);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{token}")
    public HttpEntity<EventParticipantDto> deleteEventParticipant(@PathVariable String token) {
        eventParticipantService.deleteEventParticipant(token);
        return ResponseEntity.ok().build();
    }

}
