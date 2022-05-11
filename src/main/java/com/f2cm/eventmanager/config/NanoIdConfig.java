package com.f2cm.eventmanager.config;

import com.f2cm.eventmanager.foundation.nanoid.NanoIdFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NanoIdConfig {

    @Bean
    public NanoIdFactory nanoIdFactory() {
        return new NanoIdFactory();
    }

}
