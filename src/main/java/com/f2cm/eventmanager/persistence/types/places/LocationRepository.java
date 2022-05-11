package com.f2cm.eventmanager.persistence.types.places;

import com.f2cm.eventmanager.domain.people.Person;
import com.f2cm.eventmanager.domain.places.IndoorLocation;
import com.f2cm.eventmanager.domain.places.Location;
import com.f2cm.eventmanager.domain.places.OutdoorLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findByNumOfParkingPlacesGreaterThan(Integer numOfParkingPlaces);
    List<Location> findByMaxPeopleGreaterThan(Integer maxPeople);
    List<Location> findByName(String name);
    List<Location> findByAddress_City(String address_city);
    boolean existsByToken(String token);
    Optional<Location> findByToken(String token);
    void deleteByToken(String token);

    @Query("from IndoorLocation")
    List<IndoorLocation> getAllIndoorLocations();

    @Query("from OutdoorLocation ")
    List<OutdoorLocation> getAllOutdoorLocations();

    @Query("from OutdoorLocation l WHERE l.hasPool = true")
    List<OutdoorLocation> getAllOutdoorLocationsWithPool();

    @Query("from Location l WHERE l.maxDezibel > 80 AND l.numOfBars > 0 AND (l.maxPeople / l.numOfToilets) < 60 ORDER BY l.maxPeople DESC")
    List<Location> getAllPartyLocations();

}
