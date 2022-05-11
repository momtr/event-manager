package com.f2cm.eventmanager.domain.places;

import com.f2cm.eventmanager.domain.people.Person;
import com.f2cm.eventmanager.service.dtos.objs.places.LocationDto;
import com.f2cm.eventmanager.service.dtos.objs.places.LocationType;
import com.f2cm.eventmanager.service.dtos.objs.places.OutdoorLocationDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
@DiscriminatorValue("2")
@Data
@NoArgsConstructor
public class OutdoorLocation extends Location {

    public OutdoorLocation(Integer version, String token, Integer sqaureMeters, Integer maxPeople, Integer numOfToilets, Integer numOfBars, Integer maxDezibel, Integer numOfParkingPlaces, LocalTime opensAt, LocalTime endsAt, String description, String name, Person owner, Address address, boolean weatherSave, boolean hasPool, boolean hasCampFire, boolean hasOutdoorStage, Integer numOfPlaces) {
        super(version, token, sqaureMeters, maxPeople, numOfToilets, numOfBars, maxDezibel, numOfParkingPlaces, opensAt, endsAt, description, name, owner, address);
        this.weatherSafe = weatherSave;
        this.hasPool = hasPool;
        this.hasCampFire = hasCampFire;
        this.hasOutdoorStage = hasOutdoorStage;
        this.numOfPlaces = numOfPlaces;
    }

    @Column(name = "il_isweathersafe")
    private boolean weatherSafe;

    @Column(name = "ol_haspool")
    private boolean hasPool;

    @Column(name = "ol_hascampfire")
    private boolean hasCampFire;

    @Column(name = "ol_hasoutdoorstage")
    private boolean hasOutdoorStage;

    @Column(name = "ol_numofplaces")
    private Integer numOfPlaces;

    @Override
    public void mapToDto(LocationDto locationDto) {
        locationDto.setOutdoorLocation(OutdoorLocationDto.of(this));
    }

    @Override
    public LocationType getLocationType() {
        return LocationType.OUTDOOR_LOCATION;
    }
}
