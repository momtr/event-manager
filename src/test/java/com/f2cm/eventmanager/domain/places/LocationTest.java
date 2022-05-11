package com.f2cm.eventmanager.domain.places;

import com.f2cm.eventmanager.persistence.fixtures.LocationTestFixture;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class LocationTest {

    @Test
    void ensureSuitableForPartiesCalculationIsRight() {
        Location location1 = new LocationTestFixture().getOutdoorLounge();
        Location location2 = new IndoorLocation(
                0,
                "123",
                200,
                100,
                2,
                1,
                40,
                2,
                null,
                null,
                "Description Florian's Home",
                "Flotian's Home",
                null,
                null,
                2,
                1,
                0,
                5
        );
        assertThat(location1.suitableForParties()).isTrue();
        assertThat(location2.suitableForParties()).isFalse();
    }

    @Test
    void ensureHasOwnerCheckWorksProperly() {
        Location location1 = new LocationTestFixture().getOutdoorLounge();
        assertThat(location1.hasOwner()).isFalse();
    }

    @Test
    void ensureIsUsuallyClosedCheckWorksProperly() {
        Location location1 = new LocationTestFixture().getFloriansHome();
        Location location2 = new IndoorLocation(
                0,
                "123",
                200,
                100,
                2,
                1,
                40,
                2,
                LocalTime.of(12, 30),
                LocalTime.of(21, 30),
                "Description Florian's Home",
                "Flotian's Home",
                null,
                null,
                2,
                1,
                0,
                5
        );
        assertThat(location1.usuallyClosed()).isTrue();
        assertThat(location2.usuallyClosed()).isFalse();
    }

    @Test
    void ensureCalculatingPeopleDensityWorksProperly() {
        Location location1 = new LocationTestFixture().getOutdoorLounge();
        assertThat(location1.peopleDensity()).isEqualTo(3.0);
    }

}
