package com.f2cm.eventmanager.service.types.events;

import com.f2cm.eventmanager.domain.events.Tag;
import com.f2cm.eventmanager.foundation.logging.CrudLogger;
import com.f2cm.eventmanager.persistence.types.events.TagRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final CrudLogger crudLogger = new CrudLogger(this.getClass(), Tag.class);

    public List<Tag> getTags() {
        crudLogger.readingAll();
        return tagRepository.findAll();
    }
}
