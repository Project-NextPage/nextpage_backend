package com.nextpage.backend.dto;

import com.nextpage.backend.entity.Story;
import com.nextpage.backend.repository.StoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class DatabaseLoader {

//    @Bean
//    CommandLineRunner initDatabase(StoryRepository repository) {
//        return args -> {
//            // 부모 스토리 생성 및 저장
//            Story parentStory = new Story();
//            parentStory.setUserNickname("ParentUser");
//            parentStory.setContent("부모스토리의 콘텐츠");
//            parentStory.setImageUrl("http://image.url/parent");
//            parentStory.setCreatedAt(LocalDateTime.now());
//            parentStory.setUpdatedAt(LocalDateTime.now());
//            parentStory.setIsDeleted(false);
//            parentStory = repository.save(parentStory);
//
//            // 첫 번째 자식 스토리 생성 및 저장
//            Story childStory1 = new Story();
//            childStory1.setUserNickname("ChildUser1");
//            childStory1.setContent("자식스토리1의 콘텐츠");
//            childStory1.setImageUrl("http://image.url/child1");
//            childStory1.setParentId(parentStory); // 부모 설정
//            childStory1.setCreatedAt(LocalDateTime.now());
//            childStory1.setUpdatedAt(LocalDateTime.now());
//            childStory1.setIsDeleted(false);
//            childStory1 = repository.save(childStory1);
//
//            // 두 번째 자식 스토리 생성 및 저장
//            Story childStory2 = new Story();
//            childStory2.setUserNickname("ChildUser2");
//            childStory2.setContent("자식스토리2의 콘텐츠");
//            childStory2.setImageUrl("http://image.url/child2");
//            childStory2.setParentId(parentStory); // 부모 설정
//            childStory2.setCreatedAt(LocalDateTime.now());
//            childStory2.setUpdatedAt(LocalDateTime.now());
//            childStory2.setIsDeleted(false);
//            childStory2 = repository.save(childStory2);
//
//            // 첫번째 자식의 자식 스토리 생성 및 저장
//            Story childStory1_1 = new Story();
//            childStory1_1.setUserNickname("ChildUser1_1");
//            childStory1_1.setContent("자식스토리1의 자식 콘텐츠1");
//            childStory1_1.setImageUrl("http://image.url/child1_1");
//            childStory1_1.setParentId(childStory1); // 부모 설정
//            childStory1_1.setCreatedAt(LocalDateTime.now());
//            childStory1_1.setUpdatedAt(LocalDateTime.now());
//            childStory1_1.setIsDeleted(false);
//            childStory1_1 = repository.save(childStory1_1);
//
//            // 첫번째 자식의 자식 스토리 생성 및 저장
//            Story childStory1_2 = new Story();
//            childStory1_2.setUserNickname("ChildUser1_2");
//            childStory1_2.setContent("자식스토리1의 자식 콘텐츠2");
//            childStory1_2.setImageUrl("http://image.url/child1_2");
//            childStory1_2.setParentId(childStory1); // 부모 설정
//            childStory1_2.setCreatedAt(LocalDateTime.now());
//            childStory1_2.setUpdatedAt(LocalDateTime.now());
//            childStory1_2.setIsDeleted(false);
//            childStory1_2 = repository.save(childStory1_2);
//
//            // parentStory의 childId 리스트를 업데이트합니다.
//            List<Story> childStories = new ArrayList<>();
//            childStories.add(childStory1);
//            childStories.add(childStory2);
//            parentStory.setChildId(childStories);
//            repository.save(parentStory);
//
//            List<Story> grandchildStories = new ArrayList<>();
//            grandchildStories.add(childStory1_2);
//            grandchildStories.add(childStory1_1);
//            childStory1.setChildId(grandchildStories);
//            repository.save(childStory1);
//        };
//    }
}
