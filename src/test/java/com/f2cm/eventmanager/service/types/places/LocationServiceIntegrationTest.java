package com.f2cm.eventmanager.service.types.places;

import com.f2cm.eventmanager.domain.people.Person;
import com.f2cm.eventmanager.domain.places.IndoorLocation;
import com.f2cm.eventmanager.domain.places.Location;
import com.f2cm.eventmanager.domain.places.OutdoorLocation;
import com.f2cm.eventmanager.service.exceptions.NotFoundException;
import com.f2cm.eventmanager.service.fixtures.LocationFixtures;
import com.f2cm.eventmanager.service.fixtures.PersonFixtures;
import com.f2cm.eventmanager.service.types.people.PersonService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class LocationServiceIntegrationTest {

    @Autowired
    private LocationService locationService;

    @Autowired
    private PersonService personService;

    private Person person;


    @BeforeEach
    void beforeEach() {
        person = personService.createPerson(new PersonFixtures().getCreatePersonCommandFixture());
    }

    @AfterEach
    void afterEach() {
        locationService.deleteLocations();
        personService.deletePersons();
    }

    @Test
    void getOptionalLocation() {
        Location location = locationService.createLocation(LocationFixtures.CREATE_INDOOR_LOCATION_COMMAND_BUILDER
                .ownerToken(person.getToken())
                .build()
        );

        assertThat(locationService.getOptionalLocation(location.getToken()).isPresent()).isTrue();
        assertThat(locationService.getOptionalLocation("01298347091283409127934081").isPresent()).isFalse();
    }

    @Test
    void getLocation() {
        Location location = locationService.createLocation(LocationFixtures.CREATE_INDOOR_LOCATION_COMMAND_BUILDER
                .ownerToken(person.getToken())
                .build()
        );
        assertThat(locationService.getLocation(location.getToken())).isNotNull();
        assertThrows(NotFoundException.class, () -> locationService.getLocation("01298347091283409127934081"));
    }

    @Test
    void getLocations() {
        assertThat(locationService.getLocations()).isEmpty();

        Location location = locationService.createLocation(LocationFixtures.CREATE_INDOOR_LOCATION_COMMAND_BUILDER
                .ownerToken(person.getToken())
                .build()
        );

        assertThat(locationService.getLocations()).hasSize(1);

        Location location2 = locationService.createLocation(LocationFixtures.CREATE_OUTDOOR_LOCATION_COMMAND_BUILDER
                .ownerToken(person.getToken())
                .build()
        );

        assertThat(locationService.getLocations()).hasSize(2);

    }

    @Test
    void createLocation() {

        Location location = locationService.createLocation(LocationFixtures.CREATE_INDOOR_LOCATION_COMMAND_BUILDER
                .ownerToken(person.getToken())
                .build()
        );
        assertThat(location.getToken()).isNotBlank();
        assertThat(location).isInstanceOf(Location.class);
        assertThat(location).isInstanceOf(IndoorLocation.class);
        assertThat(location).isNotInstanceOf(OutdoorLocation.class);

        Location location2 = locationService.createLocation(LocationFixtures.CREATE_OUTDOOR_LOCATION_COMMAND_BUILDER
                .ownerToken(person.getToken())
                .build()
        );
        assertThat(location2.getToken()).isNotBlank();
        assertThat(location2).isInstanceOf(Location.class);
        assertThat(location2).isInstanceOf(OutdoorLocation.class);
        assertThat(location2).isNotInstanceOf(IndoorLocation.class);

    }

    @Test
    @Transactional
    void replaceLocation() {

        assertThat(locationService.getLocations()).isEmpty();

        Location location = locationService.createLocation(LocationFixtures.CREATE_INDOOR_LOCATION_COMMAND_BUILDER
                .ownerToken(person.getToken())
                .build()
        );

        assertThat(locationService.getLocation(location.getToken()).getNumOfBars()).isEqualTo(2);

        location = locationService.updateLocation(location.getToken(), LocationFixtures.CREATE_INDOOR_LOCATION_COMMAND_BUILDER
                .ownerToken(person.getToken())
                .numOfBars(60)
                .build()
        );
        assertThat(locationService.getLocation(location.getToken()).getNumOfBars()).isEqualTo(60);

    }

    @Test
    @Transactional
    void deleteLocation() {
        assertThat(locationService.getLocations()).isEmpty();

        Location location = locationService.createLocation(LocationFixtures.CREATE_INDOOR_LOCATION_COMMAND_BUILDER
                .ownerToken(person.getToken())
                .build()
        );

        assertThat(locationService.getLocations()).hasSize(1);

        Location location2 = locationService.createLocation(LocationFixtures.CREATE_OUTDOOR_LOCATION_COMMAND_BUILDER
                .ownerToken(person.getToken())
                .build()
        );

        assertThat(locationService.getLocations()).hasSize(2);
        locationService.deleteLocation(location2.getToken());
        assertThat(locationService.getLocations()).hasSize(1);
    }

    @Test
    @Transactional
    void deleteLocations() {

        assertThat(locationService.getLocations()).isEmpty();

        Location location = locationService.createLocation(LocationFixtures.CREATE_INDOOR_LOCATION_COMMAND_BUILDER
                .ownerToken(person.getToken())
                .build()
        );

        assertThat(locationService.getLocations()).hasSize(1);

        Location location2 = locationService.createLocation(LocationFixtures.CREATE_OUTDOOR_LOCATION_COMMAND_BUILDER
                .ownerToken(person.getToken())
                .build()
        );

        assertThat(locationService.getLocations()).hasSize(2);
        locationService.deleteLocations();
        assertThat(locationService.getLocations()).isEmpty();
    }
}
