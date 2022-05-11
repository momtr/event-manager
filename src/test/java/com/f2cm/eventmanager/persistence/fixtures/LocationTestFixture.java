package com.f2cm.eventmanager.persistence.fixtures;

import com.f2cm.eventmanager.domain.places.IndoorLocation;
import com.f2cm.eventmanager.domain.places.OutdoorLocation;
import com.f2cm.eventmanager.domain.places.Location;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;

@Getter
public class LocationTestFixture {
    private final AddressTestFixture addressTestFixture;

    private final Location floriansHome;
    private final Location outdoorLounge;

    public LocationTestFixture() {
        addressTestFixture = new AddressTestFixture();

        this.floriansHome = new IndoorLocation(
                0,
                "123",
                200,
                100,
                2,
                1,
                100,
                2,
                null,
                null,
                "Description Florian's Home",
                "Flotian's Home",
                null,
                addressTestFixture.getFlorianHome(),
                2,
                1,
                0,
                5
        );

        this.outdoorLounge = new OutdoorLocation(
                0,
                "999",
                1500,
                500,
                10,
                5,
                300,
                20,
                LocalTime.of(8, 0),
                LocalTime.of(3, 0),
                "Party/Conference Lounge",
                "Lounge 1",
                null,
                addressTestFixture.getMoritzHome(),
                false,
                true,
                true,
                true,
                10
        );

    }

    public List<Location> getAllLocations() {
        return List.of(floriansHome, outdoorLounge);
    }
}
