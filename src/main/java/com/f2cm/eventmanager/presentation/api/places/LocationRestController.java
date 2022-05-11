package com.f2cm.eventmanager.presentation.api.places;

import com.f2cm.eventmanager.foundation.rest.UriFactory;
import com.f2cm.eventmanager.service.dtos.commands.CreateLocationCommand;
import com.f2cm.eventmanager.service.dtos.objs.places.LocationDto;
import com.f2cm.eventmanager.service.types.places.LocationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(LocationRestController.BASE_URL)
@AllArgsConstructor
public class LocationRestController {

    public static final String BASE_URL = "/api/v1/locations";

    public final LocationService locationService;

    @GetMapping({"", "/"})
    public HttpEntity<List<LocationDto>> getLocations() {
        return ResponseEntity.ok(locationService.getLocations()
                .stream()
                .map(LocationDto::of)
                .toList()
        );
    }

    @GetMapping("/{token}")
    public HttpEntity<LocationDto> getLocation(@PathVariable String token) {
        return locationService.getOptionalLocation(token)
                .map(LocationDto::of)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping({"", "/"})
    public HttpEntity<LocationDto> createLocation(@RequestBody @Valid CreateLocationCommand createLocationCommand) {
        var location = locationService.createLocation(createLocationCommand);
        var uri = UriFactory.createUriWithSelfeReference(BASE_URL, location.getToken());
        return ResponseEntity.created(uri).body(LocationDto.of(location));
    }

    @PutMapping("/{token}")
    public HttpEntity<LocationDto> updateLocation(@PathVariable String token, @RequestBody @Valid CreateLocationCommand createLocationCommand) {
        locationService.updateLocation(token, createLocationCommand);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public HttpEntity<LocationDto> deleteLocations() {
        locationService.deleteLocations();
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{token}")
    public HttpEntity<LocationDto> deleteLocation(@PathVariable String token) {
        locationService.deleteLocation(token);
        return ResponseEntity.ok().build();
    }

}
