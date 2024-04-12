package com.ocean.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ocean.service.OnedayService;
import com.ocean.model.entity.Oneday;
import com.ocean.model.entity.Question;
import com.ocean.model.entity.User;
import com.ocean.service.QuestionService;
import com.ocean.service.UserService;

@Controller
@RequestMapping("/question")
public class QuestionController {

	@Autowired
    private QuestionService questionService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private OnedayService onedayService;

	//문의글 리스트 출력
	@GetMapping("/list")
    public String showQuestionList(Model model, Principal principal) {
		User user = userService.getUser(principal.getName());
    	model.addAttribute("user", user);  	
        List<Question> questions = questionService.getQuestionsByCurrentUser();
        model.addAttribute("questions", questions);
        return "/question/list";
    }
	
	//문의글 작성폼으로 이동
	@GetMapping("/form")
    public String showQuestionForm(Model model, Principal principal) {
		User user = userService.getUser(principal.getName());
    	model.addAttribute("user", user);  	
        List<Oneday> onedayList = onedayService.getAllOnedayList();
        model.addAttribute("question", new Question());
        model.addAttribute("onedayList", onedayList);
        return "/question/form";
    }
	
	// 문의글 작성폼2으로 이동
	// 접속 경로: My Page -> 내가 신청한 클래스 -> 문의 글 등록
	@GetMapping("/form2")
    public String showQuestionForm2(@RequestParam(value="onedayId") String onedayId, Model model, Principal principal) {
		User user = userService.getUser(principal.getName());
    	model.addAttribute("user", user);  	
        Oneday oneday = onedayService.getOneday(Integer.parseInt(onedayId));
        model.addAttribute("question", new Question());
        model.addAttribute("oneday", oneday);
        return "/question/form2";
    }
	
	// 문의글 작성 처리
    @PostMapping("/add")
    public String addQuestion(@ModelAttribute Question question, @RequestParam(value="onedayId", required = false) String onedayId) {
		if (onedayId != null) {
    		Oneday oneday = onedayService.getOneday(Integer.parseInt(onedayId));
    		question.setOneday(oneday);
		}
		questionService.addQuestion(question);
        return "redirect:/question/list";
    }
    
    // 질문 수정 폼으로 이동
    @GetMapping("/update")
    public String showUpdateForm(@RequestParam int questionId, Model model, Principal principal) {
    	User user = userService.getUser(principal.getName());
    	model.addAttribute("user", user);  
        Question question = questionService.getQuestionById(questionId);
        model.addAttribute("question", question);
        return "/question/update_form";
    }

    // 질문 수정 처리
    @PostMapping("/update")
    public String updateQuestion(@ModelAttribute Question updatedQuestion) {
        // Question 객체를 통째로 받아서 처리
        questionService.updateQuestion(
                updatedQuestion.getId(),
                updatedQuestion.getQuestion_title(),
                updatedQuestion.getQuestion_content()
        );

        // 수정된 질문 상세 화면으로 리다이렉트
        return "redirect:/question/list";
    }
    
    //작성된 문의글 삭제
    @PostMapping("/delete")
    public String deleteQuestion(@RequestParam int questionId) {
        questionService.getQuestionById(questionId);
        questionService.deleteQuestion(questionId);
        return "redirect:/question/list";
    }
    
    // 문의 글 관리 페이지 이동
    @GetMapping("/register")
    public String showRegisterForm(Model model, Principal principal) {
    	// 사용자 정보를 Principal에서 가져오도록 변경
	    User user = userService.getUser(principal.getName());
	    model.addAttribute("user", user);
    	List<Oneday> myOnedayList = onedayService.getmyOnedayList(user);
    	model.addAttribute("myOnedayList", myOnedayList);
    	return "question/register";
    }
    
    // 각 클래스 별 문의 글 리스트 관리 페이지로 이동
    @GetMapping("/registerList")
    public String showRegisterList(@RequestParam(value="onedayId") String onedayId, Model model, Principal principal) {
    	// 사용자 정보를 Principal에서 가져옴
	    User user = userService.getUser(principal.getName());
	    model.addAttribute("user", user);
    	Oneday oneday = onedayService.getOneday(Integer.parseInt(onedayId));
    	List<Question> questionList = questionService.getQuestionsByOneday(oneday);
    	model.addAttribute("questionList", questionList);
    	model.addAttribute("oneday", oneday);
    	return "question/registerList";
    }
    
    // 문의 내용 상세보기 페이지 이동
    @GetMapping("/view")
    public String showQuestion(@RequestParam(value="questionId") String questionId, Model model, Principal principal) {
    	// 사용자 정보를 Principal에서 가져옴
	    User user = userService.getUser(principal.getName());
	    model.addAttribute("user", user);
    	Question question = questionService.getQuestionById(Integer.parseInt(questionId));
    	model.addAttribute("question", question);
    	return "question/view";
    }
    
    // 답변 작성 페이지로 이동
    @GetMapping("/writeAnswer")
    public String showWriteAnswerForm(@RequestParam(value="questionId") String questionId, Model model, Principal principal) {
    	// 사용자 정보를 Principal에서 가져옴
	    User user = userService.getUser(principal.getName());
	    model.addAttribute("user", user);
    	Question question = questionService.getQuestionById(Integer.parseInt(questionId));
    	model.addAttribute("question", question);
    	return "question/writeAnswer";
    }
    
    // 답변 작성 기능 처리
    @PostMapping("/writeAnswer")
    public String writeAnswer(@RequestParam(value="questionId") String questionId,
    						  @RequestParam(value="answer") String answer) {
    	Question question = questionService.getQuestionById(Integer.parseInt(questionId));
    	question.setAnswer(answer);
    	questionService.updateAnswer(question);
    	// 답변 작성을 마친 후 각 클래스 별 문의글 리스트 관리 페이지로 이동
    	return "redirect:/question/registerList?onedayId=" + question.getOneday().getId();
    }
    
    // 답변 보기 페이지로 이동
    @GetMapping("/viewAnswer")
    public String viewAnswer(@RequestParam(value="questionId") String questionId, Model model, Principal principal) {
    	// 사용자 정보를 Principal에서 가져옴
	    User user = userService.getUser(principal.getName());
	    model.addAttribute("user", user);
    	Question question = questionService.getQuestionById(Integer.parseInt(questionId));
    	model.addAttribute("question", question);
    	return "question/viewAnswer";
    }
    
}
