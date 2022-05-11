package com.f2cm.eventmanager.service.exceptions;

import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

    private static final String CANNOT_FIND_ENTITY_BY_TOKEN = "Cannot find entity of type %s by token [%s]";
    private static final String CANNOT_FIND_ENTITY_BY_IDENTIFIER = "Cannot find entity of type %s by identifier [%s]";

    private NotFoundException(String msg) {
        super(msg);
    }

    public static <T extends AbstractPersistable<?>> NotFoundException cannotFindEntityByToken(Class<T> entity, String token) {
        String msg = CANNOT_FIND_ENTITY_BY_TOKEN.formatted(entity.getSimpleName(), token);
        return new NotFoundException(msg);
    }

    public static <T extends AbstractPersistable<?>> NotFoundException cannotFindEntityByIdentifier(Class<T> entity, String indetifier) {
        String msg = CANNOT_FIND_ENTITY_BY_IDENTIFIER.formatted(entity.getSimpleName(), indetifier);
        return new NotFoundException(msg);
    }

}
