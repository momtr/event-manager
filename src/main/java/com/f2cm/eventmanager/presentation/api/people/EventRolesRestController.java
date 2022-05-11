package com.f2cm.eventmanager.presentation.api.people;

import com.f2cm.eventmanager.domain.people.EventRole;
import com.f2cm.eventmanager.foundation.rest.UriFactory;
import com.f2cm.eventmanager.service.dtos.commands.CreateEventRoleCommand;
import com.f2cm.eventmanager.service.dtos.objs.people.EventRoleDto;
import com.f2cm.eventmanager.service.types.people.EventRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(EventRolesRestController.BASE_URL)
@RequiredArgsConstructor
public class EventRolesRestController {

    public static final String BASE_URL = "/api/v1/roles";

    private final EventRoleService eventRoleService;

    @GetMapping({ "", "/" })
    public HttpEntity<List<EventRoleDto>> getEventRoles() {
        return ResponseEntity.ok(eventRoleService.getEventRoles()
                .stream()
                .map(EventRoleDto::of)
                .toList());
    }

    @GetMapping("/{slug}")
    public HttpEntity<EventRoleDto> getEventRole(@PathVariable String slug) {
        return eventRoleService.getEventRoleOptional(slug)
                .map(EventRoleDto::of)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping({ "", "/" })
    public HttpEntity<EventRoleDto> createEventRole(@RequestBody @Valid CreateEventRoleCommand createEventRoleCommand) {
        EventRole eventRole = eventRoleService.createEventRole(createEventRoleCommand.getName());
        URI uri = UriFactory.createUriWithSelfeReference(BASE_URL, eventRole.getSlug());
        return ResponseEntity.created(uri).body(EventRoleDto.of(eventRole));
    }

    @DeleteMapping({ "", "/" })
    public HttpEntity<EventRoleDto> deleteEventRoles() {
        eventRoleService.deleteEventRoles();
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{slug}")
    public HttpEntity<EventRoleDto> deleteEventRole(@PathVariable String slug) {
        eventRoleService.deleteEventRole(slug);
        return ResponseEntity.ok().build();
    }

}
