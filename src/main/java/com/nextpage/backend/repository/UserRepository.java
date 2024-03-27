package com.nextpage.backend.repository;

import com.nextpage.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 추가적인 사용자 관련 메소드가 필요한 경우 여기에 추가할 수 있습니다.
    Optional<User> findByEmail(String email); // 중복 가입 확h
}
