package com.f2cm.eventmanager.presentation.api.people;

import com.f2cm.eventmanager.domain.people.ContactType;
import com.f2cm.eventmanager.foundation.rest.UriFactory;
import com.f2cm.eventmanager.service.dtos.commands.CreateContactTypeCommand;
import com.f2cm.eventmanager.service.dtos.objs.people.ContactTypeDto;
import com.f2cm.eventmanager.service.dtos.objs.people.UpdateContactTypeSocialNetworkCommand;
import com.f2cm.eventmanager.service.types.people.ContactTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(ContactTypeRestController.BASE_URL)
@RequiredArgsConstructor
public class ContactTypeRestController {

    public static final String BASE_URL = "/api/v1/contact-types";

    private final ContactTypeService contactTypeService;

    @GetMapping({ "", "/" })
    public HttpEntity<List<ContactTypeDto>> getContactTypes() {
        return ResponseEntity.ok(contactTypeService.getContactTypes()
                .stream()
                .map(ContactTypeDto::of)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{means}")
    public HttpEntity<ContactTypeDto> getContactType(@PathVariable String means) {
        return contactTypeService.getContactTypeOptional(means)
                .map(ContactTypeDto::of)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping({  "", "/"})
    public HttpEntity<ContactTypeDto> createContactType(@RequestBody @Valid CreateContactTypeCommand createContactTypeCommand) {
        ContactType contactType = contactTypeService.createContactType(createContactTypeCommand);
        URI uri = UriFactory.createUriWithSelfeReference(BASE_URL, contactType.getMeans());
        return ResponseEntity.created(uri).body(ContactTypeDto.of(contactType));
    }

    @PatchMapping("/{means}")
    public HttpEntity<ContactTypeDto> patchContactTypeSocialNetwork(@PathVariable String means, @RequestBody @Valid UpdateContactTypeSocialNetworkCommand updateContactTypeSocialNetworkCommand) {
        contactTypeService.partiallyUpdateContactType(means, updateContactTypeSocialNetworkCommand);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{means}")
    public HttpEntity<ContactTypeDto> deleteContactType(@PathVariable String means) {
        contactTypeService.deleteContactType(means);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping({ "", "/" })
    public HttpEntity<List<ContactTypeDto>> deleteContactTypes() {
        contactTypeService.deleteContactTypes();
        return ResponseEntity.ok().build();
    }

}
