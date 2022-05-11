package com.f2cm.eventmanager.service;

import lombok.extern.slf4j.Slf4j;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

import static com.f2cm.eventmanager.foundation.ensurer.EnsurerFactory.ensureThat;

@Service
@Slf4j
@AllArgsConstructor
public class SlugService {

    private final TokenService tokenService;

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final int TOKEN_LENGTH = 4;

    public static final String GENERATED_SLUG_LOG_TEMPLATE = "Generated slug {}";
    public static final String GENERATING_SLUG_FOR_WITH_LENGTH_LOG_TEMPLATE = "Generating slug for '{}' with length {}";
    public static final String GENERATE_TOKEN_SLUG_FOR_LOG_TEMPLATE = "Generate token slug for '{}'";
    public static final String GENERATING_SLUG_FOR_LOG_TEMPLATE = "Generating slug for '{}'";

    public String generateSlug(String text) {
        log.trace(GENERATING_SLUG_FOR_LOG_TEMPLATE, text);
        return _generateSlug(text);
    }

    public String generateSlugWithLength(String text, int length) {
        log.trace(GENERATING_SLUG_FOR_WITH_LENGTH_LOG_TEMPLATE, text, length);
        var slug = _generateSlugWithLength(text, length);
        log.trace(GENERATED_SLUG_LOG_TEMPLATE, slug);
        return slug;
    }

    public String generateSlugAsToken(String text) {
        log.trace(GENERATE_TOKEN_SLUG_FOR_LOG_TEMPLATE, text);
        var slug = _generateSlugWithLength(text, 8);
        log.trace(GENERATED_SLUG_LOG_TEMPLATE, slug);
        return slug;
    }

    private String _generateSlugWithLength(String text, int length) {
        String slug = generateSlug(text);
        if(slug.length() > length)
            return _concatWithToken(slug.substring(0, length - (TOKEN_LENGTH + 1)));
        return slug;
    }

    private String _generateSlug(String text) {
        ensureThat(text).isNotBlank();
        String nowhitespace = WHITESPACE.matcher(text).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return _concatWithToken(slug.toLowerCase(Locale.ENGLISH));
    }

    private String _concatWithToken(String slug) {
        return slug
                .concat("-")
                .concat(tokenService.createNanoId(TOKEN_LENGTH));
    }

}
