package com.ocean.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ocean.model.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	Optional<User> findByUsername(String username);
	User findByNicknameAndUsernameAndEmail(String nickname, String username, String email);
    User findByEmailAndNickname(String email, String nickname);
    List<User> findByNicknameContainingIgnoreCase(String keyword);	// 검색을 위한 메서드 추가
}
