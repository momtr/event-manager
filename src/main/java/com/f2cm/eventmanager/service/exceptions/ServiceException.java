package com.f2cm.eventmanager.service.exceptions;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.PersistenceException;

public class ServiceException extends RuntimeException {

    private static final String CANNOT_CREATE_DUE_TO_DATABASE_PROBLEMS = "Cannot create entity of type %s [%s] due to database problems!";
    private static final String CANNOT_CREATE_DUE_TO_DATABASE_PROBLEMS_WITH_NULL_ENTITY = "Cannot create entity due to database problems!";
    private static final String CANNOT_CREATE_DUE_TO_UNDETERMINDED_REASON = "Cannot create entity of type %s [%s] due to undeterminded reason!";
    private static final String CANNOT_CREATE_DUE_TO_UNDETERMINDED_REASON_WITH_NULL_ENTITY = "Cannot create entity due to undeterminded reason!";

    private ServiceException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public static <T extends AbstractPersistable<?>> ServiceException cannotCreateEntity(Class<T> entity, String data, PersistenceException persistenceException) {
        String msg = (entity == null) ?
                CANNOT_CREATE_DUE_TO_DATABASE_PROBLEMS_WITH_NULL_ENTITY :
                CANNOT_CREATE_DUE_TO_DATABASE_PROBLEMS.formatted(entity.getSimpleName(), data);
        return new ServiceException(msg, persistenceException);
    }

    public static <T extends AbstractPersistable<?>> ServiceException cannotCreateEntityForUndeterminedReason(Class<T> entity, String data, Throwable throwable) {
        String msg = (entity == null) ?
                CANNOT_CREATE_DUE_TO_UNDETERMINDED_REASON_WITH_NULL_ENTITY :
                CANNOT_CREATE_DUE_TO_UNDETERMINDED_REASON.formatted(entity.getSimpleName(), data);
        return new ServiceException(msg, throwable);
    }

}
