package com.f2cm.eventmanager.presentation.api.events;

import com.f2cm.eventmanager.domain.events.TimeSlot;
import com.f2cm.eventmanager.foundation.rest.UriFactory;
import com.f2cm.eventmanager.service.dtos.commands.CreateEventCommand;
import com.f2cm.eventmanager.service.dtos.objs.events.EventDto;
import com.f2cm.eventmanager.service.dtos.objs.events.EventsOverviewDto;
import com.f2cm.eventmanager.service.dtos.objs.events.TagDto;
import com.f2cm.eventmanager.service.dtos.objs.events.TimeSlotDto;
import com.f2cm.eventmanager.service.types.events.EventService;
import com.f2cm.eventmanager.service.types.events.TimeSlotService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping(EventRestController.BASE_URL)

@AllArgsConstructor
public class EventRestController {
    public static final String BASE_URL = "/api/v1/events";

    public final EventService eventService;
    public final TimeSlotService timeSlotService;

    @GetMapping({"", "/"})
    public HttpEntity<List<EventDto>> getEvents(@RequestParam(required = false) String[] tags) {
        if (tags != null && tags.length > 0) {
            return ResponseEntity.ok(eventService.getEventsByTagNames(tags)
                    .stream()
                    .map(EventDto::of)
                    .toList()
            );
        }

        return ResponseEntity.ok(eventService.getEvents()
                .stream()
                .map(EventDto::of)
                .toList()
        );
    }

    @GetMapping("/overview")
    public HttpEntity<EventsOverviewDto> getEventsOverview(@RequestParam(name = "after_date", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate afterfDate) {
        EventsOverviewDto eventsOverview = eventService.getEventsOverviewAfter(afterfDate);
        return ResponseEntity.ok(eventsOverview);
    }

    @GetMapping("/{token}")
    public HttpEntity<EventDto> getEvent(@PathVariable String token) {
        return eventService.getOptionalEvent(token)
                .map(EventDto::of)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping({"", "/"})
    public HttpEntity<EventDto> createEvent(@RequestBody @Valid CreateEventCommand createEventCommand) {
        var event = eventService.createEvent(createEventCommand);
        var uri = UriFactory.createUriWithSelfeReference(BASE_URL, event.getToken());
        return ResponseEntity.created(uri).body(EventDto.of(event));
    }

    @PutMapping("/{token}")
    public HttpEntity<EventDto> updateEvent(@PathVariable String token, @RequestBody @Valid CreateEventCommand createEventCommand) {
        eventService.updateEvent(token, createEventCommand);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping({ "", "/" })
    public HttpEntity<EventDto> deleteEvents() {
        eventService.deleteEvents();
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{token}")
    public HttpEntity<EventDto> deleteEvent(@PathVariable String token) {
        eventService.deleteEvent(token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{token}/time-slots")
    public HttpEntity<List<TimeSlotDto>> getTimeSlotsForEvent(@PathVariable String token) {
        return ResponseEntity.ok(timeSlotService.getTimeSlotsByEventToken(token)
                .stream()
                .map(TimeSlotDto::of)
                .toList()
        );
    }

    @PostMapping("/{token}/tags/{tagName}")
    public HttpEntity<EventDto> addTagToEvent(@PathVariable String token, @PathVariable String tagName) {
        eventService.addTagToEvent(token, tagName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{token}/tags/{tagName}")
    public HttpEntity<EventDto> removeTagFromEvent(@PathVariable String tagName, @PathVariable String token) {
        eventService.removeTagFromEvent(token, tagName);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{token}/tags")
    public ResponseEntity<List<TagDto>> getTagsForEvent(@PathVariable String token) {
        var tags = eventService.getTagsForEvent(token)
                .stream()
                .map(TagDto::of)
                .toList();
        return ResponseEntity.ok(tags);
    }

}
