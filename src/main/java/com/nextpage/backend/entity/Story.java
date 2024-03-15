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

    // 해당 노드를 자식으로 가지는 관계 : parent
    @Relationship(type = "PARENT_OF", direction = Relationship.Direction.OUTGOING)
    private Story parentId;

    // 해당 노드를 부모로 가지는 관계 : child
    @Relationship(type = "PARENT_OF", direction = Relationship.Direction.INCOMING)
    private List<Story> childId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
}
