package com.f2cm.eventmanager.service;

import com.f2cm.eventmanager.foundation.nanoid.NanoIdFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TokenService {

    public static final String GENERATING_NANO_ID_LOG_TEMPLATE = "Generating NanoId";
    public static final String GENERATING_NANO_ID_WITH_SIZE_LOG_TEMPLATE = "Generating NanoId with size {}";
    public final int DEFAULT_NANO_ID_LENGTH = 8;

    private final NanoIdFactory nanoIdFactory;

    public TokenService(NanoIdFactory nanoIdFactory) {
        this.nanoIdFactory = nanoIdFactory;
    }

    public String createNanoId() {
        log.trace(GENERATING_NANO_ID_LOG_TEMPLATE);
        return nanoIdFactory.createNanoId(DEFAULT_NANO_ID_LENGTH);
    }

    public String createNanoIdWithType(String type) {
        return type + "-" + createNanoId();
    }

    public String createNanoId(int size) {
        log.trace(GENERATING_NANO_ID_WITH_SIZE_LOG_TEMPLATE, size);
        return nanoIdFactory.createNanoId(size);
    }

}
