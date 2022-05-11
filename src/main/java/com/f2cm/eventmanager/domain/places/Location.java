package com.f2cm.eventmanager.domain.places;

import com.f2cm.eventmanager.domain.people.Person;
import com.f2cm.eventmanager.service.dtos.objs.places.LocationDto;
import com.f2cm.eventmanager.service.dtos.objs.places.LocationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "l_locations")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "l_locationtype", discriminatorType = DiscriminatorType.INTEGER)
@NoArgsConstructor
@AllArgsConstructor
@Data
public abstract class Location extends AbstractPersistable<Long> {

    @Version
    private Integer version;

    @Column(
            name = "l_token",
            nullable = false,
            unique = true
    )
    private String token;

    @Column(name = "l_sqauremeters")
    private Integer sqaureMeters;

    @Column(name = "l_maxpeople")
    private Integer maxPeople;

    @Column(name = "l_numoftoilets")
    private Integer numOfToilets;

    @Column(name = "l_numofbars")
    private Integer numOfBars;

    @Column(name = "l_maxdezibel")
    private Integer maxDezibel;

    @Column(name = "l_numofparkingplaces")
    private Integer numOfParkingPlaces;

    @Column(name = "l_opensat")
    private LocalTime opensAt;

    @Column(name = "l_closesat")
    private LocalTime endsAt;

    @Column(name = "l_description")
    private String description;

    @Column(name = "l_name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private Person owner;

    @Embedded
    private Address address;

    public abstract void mapToDto(LocationDto locationDto);

    public abstract LocationType getLocationType();

    public boolean suitableForParties() {
        return maxDezibel > 80 && numOfBars >= 1 && (maxPeople / (double) numOfToilets) < 60;
    }

    public boolean hasOwner() {
        return owner != null;
    }

    public boolean usuallyClosed() {
        return endsAt == null && opensAt == null;
    }

    public double peopleDensity() {
        return sqaureMeters / (double) maxPeople;
    }

    public boolean isCrowdy() {
        return peopleDensity() <= 1.5;
    }

}
