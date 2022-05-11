package com.f2cm.eventmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SlugServiceTest {

    @Mock
    private TokenService tokenService;

    private SlugService slugService;

    @BeforeEach
    void setup() {
        assertThat(tokenService).isNotNull();
        slugService = new SlugService(tokenService);
        when(tokenService.createNanoId(4)).thenReturn("1111");
    }

    @Test
    void ensureGeneratingSlugWorksProperly() {
        String text = "This is a // text/&& That works und ich DENKE !! auch daran";
        String slug = slugService.generateSlug(text);
        assertThat(slug).isNotBlank();
        assertThat(slug).isEqualTo("this-is-a--text-that-works-und-ich-denke--auch-daran-1111");
    }

    @Test
    void ensureGeneratingSlugWithFixedLengthWorksProperly() {
        String text = "This is a // text/&& That works und ich DENKE !! auch daran";
        String slug = slugService.generateSlugWithLength(text, 8);
        assertThat(slug).isNotBlank();
        assertThat(slug).isEqualTo("thi-1111");
        String text2 = "this%";
        String slug2 = slugService.generateSlugWithLength(text2, 8);
        assertThat(slug2).isNotBlank();
        assertThat(slug2).isEqualTo("thi-1111");
    }

}
