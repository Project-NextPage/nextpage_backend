package com.nextpage.backend.repository;

import com.nextpage.backend.entity.Story;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

public interface StoryRepository extends Neo4jRepository<Story,Long> {

    // 부모 관계가 없는 스토리(루트 스토리) 찾기
    @Query("MATCH (s:Story) WHERE NOT (s)<-[:PARENT_OF]-() RETURN s")
    List<Story> findRootStories();
}
