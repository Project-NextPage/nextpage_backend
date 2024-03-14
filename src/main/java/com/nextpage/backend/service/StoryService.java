package com.nextpage.backend.service;

import com.nextpage.backend.entity.Story;
import com.nextpage.backend.repository.StoryRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StoryService {

    private final StoryRepository storyRepository;

    public StoryService(StoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    public List<Story> getStoriesByUserNickname(String userNickname) {
        return storyRepository.findByUserNickname(userNickname);
    }
}
