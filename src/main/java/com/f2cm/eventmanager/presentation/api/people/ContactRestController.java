package com.f2cm.eventmanager.presentation.api.people;

import com.f2cm.eventmanager.domain.people.Contact;
import com.f2cm.eventmanager.foundation.rest.UriFactory;
import com.f2cm.eventmanager.service.dtos.commands.CreateContactCommand;
import com.f2cm.eventmanager.service.dtos.commands.UpdateAddressCommand;
import com.f2cm.eventmanager.service.dtos.objs.people.ContactDto;
import com.f2cm.eventmanager.service.types.people.PersonService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static com.f2cm.eventmanager.foundation.ensurer.EnsurerFactory.ensureThat;

@RestController
@AllArgsConstructor
@RequestMapping(ContactRestController.BASE_URL)
public class ContactRestController {

    public static final String BASE_URL = "/api/v1/contacts";

    private final PersonService personService;

    @GetMapping({ "", "/" })
    public HttpEntity<List<ContactDto>> getContactsForPerson(@RequestParam("person_token") String personToken) {
        System.out.println("mooo1");
        return ResponseEntity.ok(personService.getContactsForPerson(personToken)
                .stream()
                .map(ContactDto::of)
                .toList());
    }

    @GetMapping("/{token}")
    public HttpEntity<ContactDto> getContact(@PathVariable String token) {
        return ResponseEntity.ok(ContactDto.of(personService.findContactByToken(token)));
    }

    @PostMapping({ "/primary" })
    public HttpEntity<ContactDto> setPrimaryContactForPerson(@RequestParam("person_token") String personToken, @RequestBody @Valid CreateContactCommand createContactCommand) {
        Contact contact = personService.attachPrimaryContactToPerson(personToken, createContactCommand);
        return _getCreatedResponseEntity(contact);
    }

    @PostMapping({ "", "/" })
    public HttpEntity<ContactDto> createContactForPerson(@RequestParam("person_token") String personToken, @RequestBody @Valid CreateContactCommand createContactCommand) {
        Contact contact = personService.attachContactToPerson(personToken, createContactCommand, false);
        return _getCreatedResponseEntity(contact);
    }

    @PatchMapping("/{token}")
    public HttpEntity<ContactDto> patchAddressForContact(@PathVariable String token, @RequestBody @Valid UpdateAddressCommand updateAddressCommand) {
        Contact contact = personService.editContactAddressForPerson(token, updateAddressCommand.getAddress());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/primary")
    public HttpEntity<ContactDto> deletePrimaryContactForPerson(@RequestParam("person_token") String personToken) {
        personService.deletePrimaryContactForPerson(personToken);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping({ "", "/" })
    public HttpEntity<ContactDto> deleteContactsForPerson(@RequestParam("person_token") String personToken) {
        personService.deleteContactsForPerson(personToken);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{token}")
    public HttpEntity<ContactDto> deleteContactForPerson(@PathVariable String token, @RequestParam("person_token") String personToken) {
        personService.deleteContactForPerson(personToken, token);
        return ResponseEntity.ok().build();
    }

    private ResponseEntity<ContactDto> _getCreatedResponseEntity(Contact contact) {
        ensureThat(contact).isNotNull();
        URI uri = UriFactory.createUriWithSelfeReference(BASE_URL, contact.getToken());
        return ResponseEntity.created(uri).body(ContactDto.of(contact));
    }

}
