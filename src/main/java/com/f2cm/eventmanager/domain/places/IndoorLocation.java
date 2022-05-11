package com.f2cm.eventmanager.domain.places;

import com.f2cm.eventmanager.domain.people.Person;
import com.f2cm.eventmanager.service.dtos.objs.places.IndoorLocationDto;
import com.f2cm.eventmanager.service.dtos.objs.places.LocationDto;
import com.f2cm.eventmanager.service.dtos.objs.places.LocationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
@DiscriminatorValue("1")
@Data
@NoArgsConstructor
public class IndoorLocation extends Location {

    public IndoorLocation(Integer version, String token, Integer sqaureMeters, Integer maxPeople, Integer numOfToilets, Integer numOfBars, Integer maxDezibel, Integer numOfParkingPlaces, LocalTime opensAt, LocalTime endsAt, String description, String name, Person owner, Address address, Integer numOfFloors, Integer numOfEntrances, Integer numOfReceptions, Integer numOfRooms) {
        super(version, token, sqaureMeters, maxPeople, numOfToilets, numOfBars, maxDezibel, numOfParkingPlaces, opensAt, endsAt, description, name, owner, address);
        this.numOfFloors = numOfFloors;
        this.numOfEntrances = numOfEntrances;
        this.numOfReceptions = numOfReceptions;
        this.numOfRooms = numOfRooms;
    }

    public IndoorLocation(Integer numOfFloors, Integer numOfEntrances, Integer numOfReceptions, Integer numOfRooms) {
        this.numOfFloors = numOfFloors;
        this.numOfEntrances = numOfEntrances;
        this.numOfReceptions = numOfReceptions;
        this.numOfRooms = numOfRooms;
    }

    @Column(name = "il_numoffloors")
    private Integer numOfFloors;

    @Column(name = "il_numofentrances")
    private Integer numOfEntrances;

    @Column(name = "il_numofreceptions")
    private Integer numOfReceptions;

    @Column(name = "il_numofrooms")
    private Integer numOfRooms;

    @Override
    public void mapToDto(LocationDto locationDto) {
        locationDto.setIndoorLocation(IndoorLocationDto.of(this));
    }

    @Override
    public LocationType getLocationType() {
        return LocationType.INDOOR_LOCATION;
    }
}
