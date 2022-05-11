package com.f2cm.eventmanager.service.dtos.commands;

import com.f2cm.eventmanager.service.dtos.objs.places.LocationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateLocationCommand {
    @NotNull private LocationType locationType;

    // Location
    private int squareMeters;
    private int maxPeople;
    private int numOfToilets;
    private int numOfBars;
    private int maxDezibel;
    private int numOfParkingPlaces;
    private LocalTime opensAt;
    private LocalTime endsAt;
    private String description;
    @NotNull private String name;

    // Address
    private String streetNumber;
    private String zipCode;
    private String city;
    private String country;

    // Owner
    private String ownerToken;

    // IndoorLocation
    private int numOfFloors;
    private int numOfEntrances;
    private int numOfReceptions;
    private int numOfRooms;

    //OutdoorLocation
    private boolean weatherSave;
    private boolean hasPool;
    private boolean hasCampFire;
    private boolean hasOutdoorStage;
    private int numOfPlaces;
}
