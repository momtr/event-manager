package com.f2cm.eventmanager.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TokenServiceTest {
    @Autowired
    private TokenService tokenService;


    @Test
    public void ensureThatCreateNanoIdWorks() {
        var first = tokenService.createNanoId();
        var second = tokenService.createNanoId();

        assertThat(first).isNotBlank();
        assertThat(second).isNotBlank();
        assertThat(first).isNotEqualTo(second);

        var withSizeOf16 = tokenService.createNanoId(16);
        assertThat(withSizeOf16).hasSize(16);
    }
}
