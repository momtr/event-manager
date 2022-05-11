package com.f2cm.eventmanager.service.dtos.objs.places;

import com.f2cm.eventmanager.domain.places.Location;
import com.f2cm.eventmanager.service.dtos.objs.Dto;
import com.f2cm.eventmanager.service.dtos.objs.people.SimplePersonDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

import static com.f2cm.eventmanager.foundation.ensurer.EnsurerFactory.ensureThat;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class LocationDto implements Dto {

    private String _token;
    private int squareMeters;
    private int maxPeople;
    private int numOfToilets;
    private int numOfBars;
    private int maxDezibel;
    private int numOfParkingPlaces;
    private LocalTime opensAt;
    private LocalTime endsAt;
    private String description;
    private String name;
    private SimplePersonDto owner;
    private AddressDto address;

    private LocationType type;
    private IndoorLocationDto indoorLocation;
    private OutdoorLocationDto outdoorLocation;

    public static LocationDto of(Location location) {
        if (location == null) {
            return null;
        }
        LocationDto locationDto = LocationDto.builder()
                ._token(location.getToken())
                .squareMeters(location.getSqaureMeters())
                .maxDezibel(location.getMaxPeople())
                .numOfToilets(location.getNumOfToilets())
                .numOfBars(location.getNumOfBars())
                .maxPeople(location.getMaxDezibel())
                .numOfParkingPlaces(location.getNumOfParkingPlaces())
                .opensAt(location.getOpensAt())
                .endsAt(location.getEndsAt())
                .description(location.getDescription())
                .name(location.getName())
                .owner(SimplePersonDto.of(location.getOwner()))
                .address(AddressDto.of(location.getAddress()))
                .type(location.getLocationType())
                .build();
        location.mapToDto(locationDto);
        return locationDto;
    }

}
