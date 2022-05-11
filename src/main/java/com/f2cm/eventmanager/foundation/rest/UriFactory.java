package com.f2cm.eventmanager.foundation.rest;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.Objects;

public class UriFactory {

    public static URI createUriWithSelfeReference(String path, String token) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(token);
        return UriComponentsBuilder
                .fromPath(path + "/{token}")
                .uriVariables(Map.of("token", token))
                .build()
                .toUri();
    }

}
