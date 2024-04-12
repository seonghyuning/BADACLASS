package com.ocean.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ocean.model.entity.Oneday;
import com.ocean.model.entity.Question;
import com.ocean.model.entity.User;

public interface QuestionRepository extends JpaRepository<Question, Integer>{
	Question findByUser(User user);
	List<Question> findByUserId(int id);
	// 질문 ID로 질문 조회
    Optional<Question> findById(int id);
    // Oneday로 Question 리스트 조회
    List<Question> findByOneday(Oneday oneday);
}
