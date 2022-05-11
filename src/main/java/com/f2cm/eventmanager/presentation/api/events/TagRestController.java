package com.f2cm.eventmanager.presentation.api.events;

import com.f2cm.eventmanager.persistence.types.events.TagRepository;
import com.f2cm.eventmanager.service.dtos.objs.events.TagDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping(TagRestController.BASE_URL)

@AllArgsConstructor
public class TagRestController {

    public final static String BASE_URL = "/api/v1/tags";

    private final TagRepository tagRepository;

    @GetMapping({"", "/"})
    public HttpEntity<List<TagDto>> getTags() {
        return ok(tagRepository.findAll().stream().map(TagDto::of).toList());
    }
}

