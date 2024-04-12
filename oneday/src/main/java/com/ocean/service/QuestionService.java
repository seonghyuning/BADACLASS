package com.ocean.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ocean.model.entity.Oneday;
import com.ocean.model.entity.Question;
import com.ocean.model.entity.User;
import com.ocean.repository.QuestionRepository;

@Service
public class QuestionService {
	@Autowired
    private QuestionRepository questionRepository;
	@Autowired
	private UserService userService;

	
	public Question addQuestion(Question question) {
	    // 사용자의 ID를 가져와서 question에 설정합니다.
	    Integer userId = userService.getUserId();
	    User user = new User();
	    user.setId(userId);
	    question.setUser(user);
	    // 현재 날짜 및 시간을 가져와서 포맷하지 않고 그대로 저장합니다.
	    LocalDateTime currentDateTime = LocalDateTime.now();

	    // 데이터베이스에 질문을 저장합니다.
	    question.setQuestion_date(currentDateTime);


	    // 데이터베이스에 질문을 저장합니다.
	    return questionRepository.save(question);
	}

	//해당 유저가 작성한 문의글 목록
	public List<Question> listQuestions() throws DataAccessException {
		List<Question> questionList = questionRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
		return questionList;
	}
	
	@Transactional(readOnly = true)
    public List<Question> getQuestionsByCurrentUser() {
        Integer userId = userService.getUserId();
        return questionRepository.findByUserId(userId);
    }
	
	public Question getQuestionById(int questionId) {
        // findById를 통해 Optional로 감싸진 Question 객체를 얻습니다.
        Optional<Question> optionalQuestion = questionRepository.findById(questionId);

        // 만약 Question 객체가 존재한다면 반환하고, 없다면 예외를 던집니다.
        return optionalQuestion.orElseThrow(() -> new IllegalArgumentException("해당 ID의 질문을 찾을 수 없습니다: " + questionId));
    }
	
	public List<Question> getQuestionsByOneday(Oneday oneday) throws DataAccessException {
		List<Question> questionList = null;
		questionList = questionRepository.findByOneday(oneday);
		return questionList;
	}
	
	//문의글 업데이트
	public Question updateQuestion(int questionId, String updatedTitle, String updatedContent) {
        // 기존의 질문을 조회
        Optional<Question> optionalQuestion = questionRepository.findById(questionId);
        
        if (optionalQuestion.isPresent()) {
            Question existingQuestion = optionalQuestion.get();
            
            // 수정할 내용 업데이트
            existingQuestion.setQuestion_title(updatedTitle);
            existingQuestion.setQuestion_content(updatedContent);
            
            // 수정된 질문을 저장하고 반환
            return questionRepository.save(existingQuestion);
        } else {
            // 질문을 찾을 수 없을 경우 예외 처리 또는 null 등을 반환
            throw new IllegalArgumentException("해당 ID의 질문을 찾을 수 없습니다: " + questionId);
        }
    }
	
	@Transactional
    public void deleteQuestion(int questionId) {
        // 질문이 존재하는지 확인합니다.
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 질문을 찾을 수 없습니다: " + questionId));

        // 현재 사용자가 질문을 삭제할 권한이 있는지 확인합니다.
        Integer currentUserId = userService.getUserId();
        if (!currentUserId.equals(question.getUser().getId())) {
            throw new IllegalStateException("이 질문을 삭제할 권한이 없습니다.");
        }

        // 삭제 작업을 수행합니다.
        questionRepository.delete(question);
    }
	
	// Question에 Answer를 등록하기 위한 메서드
	// 먼저 만들어진 Question에 Answer만 추가하여 Question 객체 수정
	public void updateAnswer(Question inQuestion) throws DataAccessException {
		Optional<Question> optionalQuestion = questionRepository.findById(inQuestion.getId());
		Question question = null;
		if (optionalQuestion.isPresent()) {
			question = optionalQuestion.get();
			question.setAnswer(inQuestion.getAnswer());
			questionRepository.save(question);
		}
	}
	
	
}
