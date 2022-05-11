package com.f2cm.eventmanager.service.types.places;

import com.f2cm.eventmanager.domain.people.Person;
import com.f2cm.eventmanager.domain.places.Address;
import com.f2cm.eventmanager.domain.places.IndoorLocation;
import com.f2cm.eventmanager.domain.places.Location;
import com.f2cm.eventmanager.domain.places.OutdoorLocation;
import com.f2cm.eventmanager.foundation.logging.CrudLogger;
import com.f2cm.eventmanager.foundation.time.TemporalValueFactory;
import com.f2cm.eventmanager.persistence.types.places.LocationRepository;
import com.f2cm.eventmanager.service.TokenService;
import com.f2cm.eventmanager.service.dtos.commands.CreateLocationCommand;
import com.f2cm.eventmanager.service.exceptions.NotFoundException;
import com.f2cm.eventmanager.service.exceptions.ServiceException;
import com.f2cm.eventmanager.service.types.people.PersonService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.f2cm.eventmanager.foundation.ensurer.EnsurerFactory.ensureThat;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class LocationService {
    private final LocationRepository locationRepository;
    private final TemporalValueFactory temporalValueFactory;

    private final TokenService tokenService;
    private final PersonService personService;

    private final String TYPE = "location";

    private static final CrudLogger crudLogger = new CrudLogger(LocationService.class, Location.class);

    public Optional<Location> getOptionalLocation(String token) {
        crudLogger.readingByToken(token);
        ensureThat(token).isNotNull("token must not be null");
        return locationRepository.findByToken(token);
    }

    public Location getLocation(String token) {
        crudLogger.readingByToken(token);
        ensureThat(token).isNotNull("token must not be null");
        return _findLocationByToken(token);
    }

    public List<Location> getLocations() {
        crudLogger.readingAll();
        return locationRepository.findAll();
    }

    public Location createLocation(CreateLocationCommand createLocationCommand) {
        crudLogger.creating();
        ensureThat(createLocationCommand).isNotNull("createLocationCommand must not be null");
        Location location = _createLocation(Optional.empty(), createLocationCommand);
        crudLogger.persistedWithToken(location.getToken());
        return location;
    }

    public Location updateLocation(String token, CreateLocationCommand createLocationCommand) {
        crudLogger.updatingByToken(token);
        ensureThat(token).isNotNull("token must not be null");
        ensureThat(createLocationCommand).isNotNull("createLocationCommand must not be null");
        var location = getLocation(token);
        Person locationOwner = (createLocationCommand.getOwnerToken() != null) ? personService.getPerson(createLocationCommand.getOwnerToken()) : null;
        
        location.setSqaureMeters(createLocationCommand.getSquareMeters());
        location.setMaxPeople(createLocationCommand.getMaxPeople());
        location.setNumOfToilets(createLocationCommand.getNumOfToilets());
        location.setNumOfBars(createLocationCommand.getNumOfBars());
        location.setMaxDezibel(createLocationCommand.getMaxDezibel());
        location.setNumOfParkingPlaces(createLocationCommand.getNumOfParkingPlaces());
        location.setOpensAt(createLocationCommand.getOpensAt());
        location.setEndsAt(createLocationCommand.getEndsAt());
        location.setDescription(createLocationCommand.getDescription());
        location.setName(createLocationCommand.getName());

        location.getAddress().setCity(createLocationCommand.getCity());
        location.getAddress().setZipCode(createLocationCommand.getZipCode());
        location.getAddress().setCity(createLocationCommand.getCity());
        location.getAddress().setCountry(createLocationCommand.getCountry());

        location.setOwner(locationOwner);
        
        switch (location) {
            case IndoorLocation i:
                log.trace("updating indoor location fields");
                i.setNumOfFloors(createLocationCommand.getNumOfFloors());
                i.setNumOfEntrances(createLocationCommand.getNumOfEntrances());
                i.setNumOfReceptions(createLocationCommand.getNumOfReceptions());
                i.setNumOfRooms(createLocationCommand.getNumOfRooms());
                break;
            case OutdoorLocation o:
                log.trace("updating outdoor location fields");
                o.setWeatherSafe(createLocationCommand.isWeatherSave());
                o.setHasPool(createLocationCommand.isHasPool());
                o.setHasCampFire(createLocationCommand.isHasCampFire());
                o.setHasOutdoorStage(createLocationCommand.isHasOutdoorStage());
                o.setNumOfPlaces(createLocationCommand.getNumOfPlaces());
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + location);
        }

        return locationRepository.save(location);
    }

    @Transactional
    public void deleteLocation(String token) {
        crudLogger.deletingByToken(token);
        ensureThat(token).isNotNull("token must not be null");
        locationRepository.deleteByToken(token);
    }

    @Transactional
    public void deleteLocations() {
        crudLogger.deletingAll();
        locationRepository.deleteAll();
    }

    private Location _findLocationByToken(String token) {
        Optional<Location> location = locationRepository.findByToken(token);
        location.orElseThrow(() -> NotFoundException.cannotFindEntityByToken(Location.class, token));
        return location.get();
    }

    private Location _createLocation(Optional<String> token, CreateLocationCommand createLocationCommand) {
        String locationToken = token.orElse(tokenService.createNanoIdWithType(TYPE));
        Person locationOwner = personService.getOptionalPerson(createLocationCommand.getOwnerToken()).orElse(null);

        Address locationAddress = Address.builder()
                .city(createLocationCommand.getCity())
                .country(createLocationCommand.getCountry())
                .streetNumber(createLocationCommand.getStreetNumber())
                .zipCode(createLocationCommand.getZipCode())
                .build();

        Location location = null;

        switch (createLocationCommand.getLocationType()) {
            case INDOOR_LOCATION -> {
                location = new IndoorLocation(
                        0,
                        locationToken,
                        createLocationCommand.getSquareMeters(),
                        createLocationCommand.getMaxPeople(),
                        createLocationCommand.getNumOfToilets(),
                        createLocationCommand.getNumOfBars(),
                        createLocationCommand.getMaxDezibel(),
                        createLocationCommand.getNumOfParkingPlaces(),
                        createLocationCommand.getOpensAt(),
                        createLocationCommand.getEndsAt(),
                        createLocationCommand.getDescription(),
                        createLocationCommand.getName(),
                        locationOwner,
                        locationAddress,
                        // IndoorLocation
                        createLocationCommand.getNumOfFloors(),
                        createLocationCommand.getNumOfEntrances(),
                        createLocationCommand.getNumOfReceptions(),
                        createLocationCommand.getNumOfRooms()
                );
            }
            case OUTDOOR_LOCATION -> {
                location = new OutdoorLocation(
                        0,
                        locationToken,
                        createLocationCommand.getSquareMeters(),
                        createLocationCommand.getMaxPeople(),
                        createLocationCommand.getNumOfToilets(),
                        createLocationCommand.getNumOfBars(),
                        createLocationCommand.getMaxDezibel(),
                        createLocationCommand.getNumOfParkingPlaces(),
                        createLocationCommand.getOpensAt(),
                        createLocationCommand.getEndsAt(),
                        createLocationCommand.getDescription(),
                        createLocationCommand.getName(),
                        locationOwner,
                        locationAddress,
                        // OutdoorLocation
                        createLocationCommand.isWeatherSave(),
                        createLocationCommand.isHasPool(),
                        createLocationCommand.isHasCampFire(),
                        createLocationCommand.isHasOutdoorStage(),
                        createLocationCommand.getNumOfPlaces()
                );
            }
        }

        try {
            locationRepository.save(location);
            return location;
        } catch (PersistenceException persistenceException) {
            var exception = ServiceException.cannotCreateEntity(Location.class, locationToken, persistenceException);
            log.warn("Caught exception", exception);
            throw exception;
        } catch (Throwable t) {
            var exception = ServiceException.cannotCreateEntityForUndeterminedReason(Location.class, locationToken, t);
            log.error("Caught unexpected exception", exception);
            throw exception;
        }
    }
}
