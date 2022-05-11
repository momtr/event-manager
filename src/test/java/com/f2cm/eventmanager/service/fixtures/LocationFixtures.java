package com.f2cm.eventmanager.service.fixtures;

import com.f2cm.eventmanager.domain.people.Person;
import com.f2cm.eventmanager.domain.places.Address;
import com.f2cm.eventmanager.domain.places.Location;
import com.f2cm.eventmanager.domain.places.OutdoorLocation;
import com.f2cm.eventmanager.service.dtos.commands.CreateLocationCommand;
import com.f2cm.eventmanager.service.dtos.objs.places.LocationType;

import java.time.LocalTime;

public class LocationFixtures {

    public static final CreateLocationCommand.CreateLocationCommandBuilder CREATE_OUTDOOR_LOCATION_COMMAND_BUILDER = CreateLocationCommand.builder()
            .locationType(LocationType.OUTDOOR_LOCATION)
            .squareMeters(100)
            .maxPeople(12)
            .numOfToilets(1)
            .numOfBars(2)
            .maxDezibel(123)
            .numOfParkingPlaces(123)
            .opensAt(LocalTime.of(16, 0))
            .endsAt(LocalTime.of(23, 59))
            .description("a very cool location")
            .name("Basic Location")
            .streetNumber("Spengergasse 15")
            .zipCode("1050")
            .city("Vienna")
            .country("Austria")
            .ownerToken(PersonFixtures.DEFAULT_TOKEN)
            .weatherSave(false)
            .hasPool(true)
            .hasCampFire(true)
            .hasOutdoorStage(false)
            .numOfPlaces(2);

    public static final CreateLocationCommand.CreateLocationCommandBuilder CREATE_INDOOR_LOCATION_COMMAND_BUILDER = CreateLocationCommand.builder()
            .locationType(LocationType.INDOOR_LOCATION)
            .squareMeters(100)
            .maxPeople(12)
            .numOfToilets(1)
            .numOfBars(2)
            .maxDezibel(123)
            .numOfParkingPlaces(123)
            .opensAt(LocalTime.of(16, 0))
            .endsAt(LocalTime.of(23, 59))
            .description("a very cool location")
            .name("Basic Location")
            .streetNumber("Spengergasse 15")
            .zipCode("1050")
            .city("Vienna")
            .country("Austria")
            .ownerToken(PersonFixtures.DEFAULT_TOKEN)
            .numOfFloors(2)
            .numOfEntrances(2)
            .numOfReceptions(2)
            .numOfRooms(2);

    public static final Location getOutdoorLocation1(Person owner) {
        return new OutdoorLocation(
                0,
                "outdoorLocationT",
                123,
                22,
                1,
                5,
                500,
                10,
                LocalTime.of(16, 0),
                LocalTime.of(20, 0),
                "very cool location",
                "Some outdoor location",
                owner,
                new Address(
                        "Spengergasse 15",
                        "1050",
                        "Vienna",
                        "Austria"
                ),
                true,
                true,
                false,
                true,
                12
        );
    }
}
