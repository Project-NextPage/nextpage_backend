package com.nextpage.backend.entity;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDateTime;
import java.util.List;

@Node
@Getter
@Setter
public class Story {

    @Id @GeneratedValue
    private Long id;

    private String userNickname;
    private String content;
    private String imageUrl;

    // 부모 스토리와의 관계를 나타냅니다.
    @Relationship(type = "PARENT_OF", direction = Relationship.Direction.INCOMING)
    private Story parentId;

    // 자식 스토리와의 관계를 나타냅니다.
    @Relationship(type = "PARENT_OF", direction = Relationship.Direction.OUTGOING)
    private List<Story> childId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;

    public Story(final String content, final String imageUrl){
        this.content = content;
        this.imageUrl = imageUrl;
    }
}
