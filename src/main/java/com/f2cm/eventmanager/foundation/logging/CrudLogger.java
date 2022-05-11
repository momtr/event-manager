package com.f2cm.eventmanager.foundation.logging;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CrudLogger {

    private final Logger log;
    private final Class<?> entity;

    public CrudLogger(Class<?> clazz, Class<?> entity) {
        this.log = LoggerFactory.getLogger(clazz.getName());
        this.entity = entity;
    }

    // general crud

    public void creating() {
        log.trace("Creating entity of type {} ", _getEntityName());
    }

    public void readingAll() {
        log.trace("Reading all entities of type {}", _getEntityName());
    }

    public void readingByMeans(String means, String ...token) {
        log.trace("Reading entity of type {} with {} [{}]", _getEntityName(), means, token);
    }

    public void updatingByMeans(String means, String token) {
        log.trace("Updating entity of type {} with {} [{}]", _getEntityName(), means, token);
    }

    public void deletingByMeans(String means, String token) {
        log.trace("Deleting entity of type {} with {} [{}]", _getEntityName(), means, token);
    }

    public void deletingAll() {
        log.trace("Deleting all entities of type {}", _getEntityName());
    }


    // token crud

    public void readingByToken(String token) {
        readingByMeans("token", token);
    }

    public void deletingByToken(String token) {
        deletingByMeans("token", token);
    }

    public void updatingByToken(String token) {
       updatingByMeans("token", token);
    }


    // results

    public void foundNumber(int number) {
        log.trace("Found {} entities of type {}", number, _getEntityName());
    }

    public void persistedWithToken(String token) {
        log.trace("Persisted entity of type {} with token [{}]", _getEntityName(), token);
    }

    public void persistedWithId(Long id) {
        log.trace("Persisted entity of type {} with id [{}]", _getEntityName(), id);
    }


    private String _getEntityName() {
        return entity.getSimpleName();
    }

}
