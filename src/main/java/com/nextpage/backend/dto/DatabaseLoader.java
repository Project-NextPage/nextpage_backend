package com.nextpage.backend.dto;

import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseLoader {
//
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
//            // parentStory의 childId 리스트를 업데이트합니다.
//            List<Story> childStories = new ArrayList<>();
//            childStories.add(childStory1);
//            childStories.add(childStory2);
//            parentStory.setChildId(childStories);
//            repository.save(parentStory);
//        };
//    }
}
