package com.f2cm.eventmanager.foundation.rest;

import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UriFactoryTest {

    @Test
    void ensureCreateUriWorksProperly() {
        URI uri = UriFactory.createUriWithSelfeReference("/docs", "123");
        assertThat(uri.toString()).isEqualTo("/docs/123");
    }

    @Test
    void ensureCreateUriThrowsExceptionForNullValues() {
        assertThrows(NullPointerException.class, () -> UriFactory.createUriWithSelfeReference(null, "123"));
        assertThrows(NullPointerException.class, () -> UriFactory.createUriWithSelfeReference("/docs", null));
        assertThrows(NullPointerException.class, () -> UriFactory.createUriWithSelfeReference(null, null));
    }

}
