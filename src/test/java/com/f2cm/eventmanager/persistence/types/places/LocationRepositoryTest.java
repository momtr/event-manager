package com.f2cm.eventmanager.persistence.types.places;

import com.f2cm.eventmanager.domain.places.IndoorLocation;
import com.f2cm.eventmanager.domain.places.Location;
import com.f2cm.eventmanager.domain.places.OutdoorLocation;
import com.f2cm.eventmanager.persistence.fixtures.AddressTestFixture;
import com.f2cm.eventmanager.persistence.fixtures.LocationTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest()
public class LocationRepositoryTest {

    @Autowired
    private LocationRepository locationRepository;

    private AddressTestFixture addressTestFixture;
    private LocationTestFixture locationTestFixture;

    @BeforeEach
    void beforeEach() {
        locationTestFixture = new LocationTestFixture();
        addressTestFixture = locationTestFixture.getAddressTestFixture();
        locationRepository.saveAll(locationTestFixture.getAllLocations());
    }

    @Test
    void ensureThatLocationSpecificationsAreSavedWithTheirData() {
        List<IndoorLocation> indoorLocations = locationRepository.getAllIndoorLocations();
        List<OutdoorLocation> outdoorLocations = locationRepository.getAllOutdoorLocations();
        assertThat(indoorLocations.size()).isEqualTo(1);
        assertThat(outdoorLocations.size()).isEqualTo(1);
        assertThat(indoorLocations.get(0)).isEqualTo(locationTestFixture.getFloriansHome());
        assertThat(outdoorLocations.get(0)).isEqualTo(locationTestFixture.getOutdoorLounge());
    }

    @Test
    void ensureThatFindAddressByEmbeddedCityWorksProperly() {
        List<Location> locationsInVienna = locationRepository.findByAddress_City("Vienna");
        assertThat(locationsInVienna.size()).isEqualTo(2);
    }

    @Test
    void ensureThatFindingPartyLocationsQueryWorks() {
        List<Location> partyLocations = locationRepository.getAllPartyLocations();
        assertThat(partyLocations.size()).isEqualTo(2);
    }

    @Test
    void ensureThatFindingLocationsWithPoolWorks() {
        List<OutdoorLocation> locationsWithPool = locationRepository.getAllOutdoorLocationsWithPool();
        assertThat(locationsWithPool.size()).isEqualTo(1);
        assertThat(locationsWithPool.get(0)).isEqualTo(locationTestFixture.getOutdoorLounge());
    }

}
