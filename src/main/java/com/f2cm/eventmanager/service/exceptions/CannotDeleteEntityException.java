package com.f2cm.eventmanager.service.exceptions;

import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class CannotDeleteEntityException extends RuntimeException {

    private static final String CANNOT_DELETE_ENTITY_WITH_IDENTIFIER = "Cannot delete entity of type %s by identifier/token [%s]. There may be cross-dependencies, such that the entity is referenced in another object.";

    private CannotDeleteEntityException(String msg) {
        super(msg);
    }

    public static <T extends AbstractPersistable<?>> CannotDeleteEntityException cannotDeleteEntityByIdentifier(Class<T> entity, String identifier) {
        String msg = CANNOT_DELETE_ENTITY_WITH_IDENTIFIER.formatted(entity.getSimpleName(), identifier);
        return new CannotDeleteEntityException(msg);
    }

}
