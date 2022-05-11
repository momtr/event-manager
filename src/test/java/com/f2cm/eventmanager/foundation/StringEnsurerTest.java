package com.f2cm.eventmanager.foundation;

import org.junit.jupiter.api.Test;

import static com.f2cm.eventmanager.foundation.ensurer.EnsurerFactory.ensureThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StringEnsurerTest {

    @Test
    void and() {
        assertThrows(IllegalArgumentException.class, () -> {
            ensureThat("bacon").meets(s -> s.equals("bacon")).and().isNotNull().and().hasLength(4);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            ensureThat("bacon").hasLength(4).and().isNotBlank();
        });

        ensureThat("bacon").meets(s -> s.equals("bacon")).and().isNotNull().and().hasLength(5);
    }

    @Test
    void meets() {
        assertThrows(IllegalArgumentException.class, () -> {
            ensureThat("bacon").meets(s -> s.equals("eggs"));
        });

        ensureThat("bacon").meets(s -> s.equals("bacon"));
    }

    @Test
    void isNotNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            ensureThat((String) null).isNotNull();
        });

        ensureThat("bacon").isNotNull();
    }

    @Test
    void thenAssign() {
        assertThrows(IllegalArgumentException.class, () -> {
            var result = ensureThat((String) null).isNotNull().thenAssign();
        });

        var result = ensureThat("bacon").isNotNull().thenAssign();
        assertThat(result).isEqualTo("bacon");
    }

    @Test
    void isNotEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            ensureThat("").isNotEmpty();
        });

        ensureThat("bacon").isNotEmpty();
        ensureThat("    ").isNotEmpty();
    }

    @Test
    void isNotBlank() {
        assertThrows(IllegalArgumentException.class, () -> {
            ensureThat("").isNotBlank();
            ensureThat("    ").isNotBlank();
        });

        ensureThat("bacon").isNotBlank();
    }

    @Test
    void hasLength() {
        assertThrows(IllegalArgumentException.class, () -> {
            ensureThat("bacon").hasLength(0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            ensureThat("bacon").hasLength(4);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            ensureThat("bacon").hasLength(6);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            ensureThat((String) null).hasLength(6);
        });

        ensureThat("bacon").hasLength(5);
    }

    @Test
    void hasLengthBetweenInclusive() {
        assertThrows(IllegalArgumentException.class, () -> {
            ensureThat("bacon").hasLengthBetweenInclusive(6, 10);
        });

        ensureThat("bacon").hasLengthBetweenInclusive(5, 10);
    }

    @Test
    void hasLengthBetweenExclusive() {
        assertThrows(IllegalArgumentException.class, () -> {
            ensureThat("bacon").hasLengthBetweenExclusive(6, 10);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            ensureThat("bacon").hasLengthBetweenExclusive(5, 10);
        });

        ensureThat("bacon").hasLengthBetweenExclusive(4, 10);
    }
}
