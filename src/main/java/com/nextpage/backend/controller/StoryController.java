package com.nextpage.backend.controller;

import com.nextpage.backend.model.Story;
import com.nextpage.backend.service.StoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class StoryController {

    private final StoryService storyService;

    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

    @GetMapping("/stories")
    public List<Story> getStoriesByNickname(@RequestParam String userNickname) {
        return storyService.getStoriesByUserNickname(userNickname);
    }
}
