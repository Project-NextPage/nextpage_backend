package com.nextpage.backend.repository;

import com.nextpage.backend.entity.Story;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface StoryRepository extends Neo4jRepository<Story,Long> {

    List<Story> findByUserNickname(String userNickname);
}
