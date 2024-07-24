package com.sittingspot.tagextractor.controller;

import com.sittingspot.tagextractor.models.Review;
import com.sittingspot.tagextractor.models.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("tag-extractor/api/v1")
public class TagExtactorController {

    @PostMapping
    public List<Tag> search(@RequestBody Review review) {
        //TODO
        return List.of();
    }
}