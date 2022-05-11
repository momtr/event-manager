package com.f2cm.eventmanager.service.exceptions;

import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.ALREADY_REPORTED)
public class AlreadyExistsException extends RuntimeException {

    private static final String ENTITY_ALREADY_EXISTS = "Entity of type %s already exists by attribute %s with value [%s]";

    private AlreadyExistsException(String msg) {
        super(msg);
    }

    public static <T extends AbstractPersistable<?>> AlreadyExistsException entityAlreadyExists(Class<T> entity, String attribute, String value) {
        String msg = ENTITY_ALREADY_EXISTS.formatted(entity.getSimpleName(), attribute, value);
        return new AlreadyExistsException(msg);
    }

}
