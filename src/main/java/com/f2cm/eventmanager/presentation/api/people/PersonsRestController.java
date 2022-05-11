package com.f2cm.eventmanager.presentation.api.people;

import com.f2cm.eventmanager.domain.people.Person;
import com.f2cm.eventmanager.foundation.rest.UriFactory;
import com.f2cm.eventmanager.service.dtos.commands.CreatePersonCommand;
import com.f2cm.eventmanager.service.dtos.objs.people.PersonDto;
import com.f2cm.eventmanager.service.types.people.PersonService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(PersonsRestController.BASE_URL)
@AllArgsConstructor
public class PersonsRestController {

    public static final String BASE_URL = "/api/v1/persons";

    private final PersonService personService;

    @GetMapping({ "", "/" })
    public HttpEntity<List<PersonDto>> getPersons() {
        return ResponseEntity.ok(personService.getPersons()
                .stream()
                .map(PersonDto::of)
                .toList());
    }

    @GetMapping("/{token}")
    public HttpEntity<PersonDto> getPerson(@PathVariable String token) {
        return personService.getOptionalPerson(token)
                .map(PersonDto::of)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping({ "", "/" })
    public HttpEntity<PersonDto> createPerson(@RequestBody @Valid CreatePersonCommand createPersonCommand) {
        Person person = personService.createPerson(createPersonCommand);
        URI uri = UriFactory.createUriWithSelfeReference(BASE_URL, person.getToken());
        return ResponseEntity.created(uri).body(PersonDto.of(person));
    }

    @PutMapping("/{token}")
    public HttpEntity<PersonDto> replacePerson(@PathVariable String token, @RequestBody @Valid CreatePersonCommand createPersonCommand) {
        Person person = personService.replacePerson(token, createPersonCommand);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping({ "", "/" })
    public HttpEntity<PersonDto> deletePersons() {
        personService.deletePersons();
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{token}")
    public HttpEntity<PersonDto> deletePerson(@PathVariable String token) {
        personService.deletePerson(token);
        return ResponseEntity.ok().build();
    }

}
