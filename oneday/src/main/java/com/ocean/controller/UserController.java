package com.ocean.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ocean.model.UserCreateForm;
import com.ocean.model.UserRole;
import com.ocean.model.entity.User;
import com.ocean.model.entity.WaitingForAdminApproval;
import com.ocean.service.EmailService;
import com.ocean.service.ImageService;
import com.ocean.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/auth")
public class UserController {
	@Autowired
	private UserService userService;
	
	@Autowired
	private ImageService imageService;
	
	
	// GET 방식으로 넘기면 다시 join_form 으로 돌아감 (잘못된 접근)
	@GetMapping("/join")
	public String join(UserCreateForm userCreateForm) {
		return "join_form";
	}
	
	@PostMapping("/join")
	// 작성한 내용을 validation 하는 것
	public String join(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
		// 작성 결과가 에러나면 다시 join_form 으로 돌아감
		if (bindingResult.hasErrors()) {
			return "join_form";
		}
		
		// 비밀번호와 비밀번호 확인이 같지 않으면 다시 join_form 으로 돌아감
		if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
			// 어디서 틀렸는지 알려주어야 함
			// bindingResult.rejectValue(필드명, 오류코드, 에러메시지)
			// 오류코드는 마음대로 정해도 됨
			bindingResult.rejectValue("password2", "passwordIncorrect", "비밀번호가 일치하지 않습니다.");
			return "join_form";
		}
		
		try {
			userService.addUser(userCreateForm.getNickname(), userCreateForm.getUsername(), 
								userCreateForm.getPassword1(), userCreateForm.getGender(),
								userCreateForm.getYear(), userCreateForm.getPhoneNm(), 
								userCreateForm.getEmail(), userCreateForm.getRole());
			
		} catch (DataIntegrityViolationException e) {	// 중복객체 들어오면 다시 join_form 으로 돌아감
			e.printStackTrace();
			// bindingResult.reject(오류코드, 에러메시지)
			bindingResult.reject("JoinFailed", "이미 존재하는 ID 입니다.");
			return "join_form";
		} catch (Exception e) {
			e.printStackTrace();
			bindingResult.reject("joinFailed", e.getMessage());
			return "join_form";
		}
		
		// 에러가 나지 않으면 페이지 전환
		return "redirect:/main";
	}
	
	
	@GetMapping("/login") 
	public String login(Model model, Principal principal) {
		return "login_form";
	}
	
	//아이디 찾기 화면으로 이동
	@GetMapping("/findId")
    public String showFindIdForm() {
        return "/user/find_id";
    }

	//이메일과 사용자 이름을 입력받아 아이디를 찾음
	@PostMapping("/findId")
	public ResponseEntity<Map<String, String>> findId(
	        @RequestParam("email") String email,
	        @RequestParam("nickname") String nickname,
	        HttpSession session) {

	    Map<String, String> response = new HashMap<>();

	    // Your logic to find the user ID
	    String username = userService.findUserId(email, nickname);

	    if (username != null) {
	        
	    	// 사용자 정보가 일치하면 세션에 아이디 저장
	        session.setAttribute("username", username);
	    	// 사용자 정보가 일치하면 resetPw 페이지로 이동
	        response.put("redirect", "/auth/find_id_result");
	    } else {
	        response.put("error", "일치하는 사용자 정보가 없습니다.");
	    }

	    return ResponseEntity.ok(response);
	}
	
	
	@GetMapping("/find_id_result")
	public String findIdResult(HttpSession session, Model model) {
	    // 세션에서 아이디 가져오기
	    String username = (String) session.getAttribute("username");
	    
	    // 모델에 아이디 추가
	    model.addAttribute("username", username);
	    return "/user/find_id_result";
	}
    
	//비밀번호 찾기 페이지로 이동
    @GetMapping("/findPw")
    public String showFindPwForm() {
        return "user/find_pw";
    }
    
    @PostMapping("/findPw")
    public ResponseEntity<Map<String, String>> findPw(
            @RequestParam("nickname") String nickname,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            HttpSession session) {

        Map<String, String> response = new HashMap<>();

        User user = userService.findByPw(nickname, username, email);
        if (user != null) {
            // 사용자 정보가 일치하면 세션에 username 저장
            session.setAttribute("username", username);
            response.put("redirect", "/auth/resetPw");
        } else {
            // 사용자 정보가 일치하지 않으면 오류 메시지 반환
            response.put("error", "입력한 정보와 일치하는 사용자가 없습니다.");
        }

        return ResponseEntity.ok(response);
    }


    
    @GetMapping("/resetPw")
    public String showResetPwForm() {
        return "user/reset_pw";
    }

    // resetPassword 메소드
    @PostMapping("/resetPw")
    public ResponseEntity<Map<String, String>> resetPassword(
            @RequestParam Map<String, String> params,
            @RequestParam("password1") String password1,
            @RequestParam("password2") String password2,
            HttpSession session, Principal principal) {

        Map<String, String> response = new HashMap<>();
        
        String username;
        if (principal == null || principal.getName().isEmpty()) {	// 로그인 되어있지 않은 상태 (로그인 화면 -> 비밀번호 찾기)
            // 세션에서 username 가져오기
            username = (String) session.getAttribute("username");
        } else {	// 로그인 되어있는 상태 (마이페이지 -> 비밀번호 변경)
            username = principal.getName();
        }

        User user = userService.getUser(username);

        if (!password1.equals(password2)) {
            // 새로운 비밀번호와 비밀번호 확인이 일치하지 않으면 오류 메시지 전달
            response.put("error", "비밀번호 확인이 일치하지 않습니다.");
        } else {
            userService.updatePassword(user, password1);
            response.put("redirect", "/auth/login");
        }

        return ResponseEntity.ok(response);
    }

    
    @GetMapping("/mypage")
    public String mypage(Model model, Principal principal) {
    	User user = userService.getUser(principal.getName());
    	model.addAttribute("user", user);  	
    	return "user/mypage";
    }
    
    @GetMapping("/updateProfile")
    public String showUpdateProfileForm(Principal principal, Model model) {
    	User user = userService.getUser(principal.getName());
    	model.addAttribute("user", user);
    	return "user/update_profile";
    }
    
    @PostMapping("/updateProfile")
    public String updateProfile(@RequestParam(value="nickname") String nickname, 
    							@RequestParam(value="phoneNm") String phoneNm, 
    							@RequestParam(value="email") String email, 
    							@RequestParam(value = "file", required = false) MultipartFile file, 
  	                          	@RequestParam(value = "imageName", required = false) String imageName,
  	                          	Principal principal, Model model) throws IOException {
		User user = userService.getUser(principal.getName());
    	
		user.setNickname(nickname);
    	user.setPhoneNm(phoneNm);
    	user.setEmail(email);
    	if (imageName != null) {
    		imageService.saveUserWithImage(user, file, imageName);	// 이미지 User에 저장
    	}
    	userService.updateUser(user);
    	
    	return "redirect:/auth/mypage";
    }
    
    @GetMapping("/delete")
    public String showDeleteForm(Model model, Principal principal) {
    	model.addAttribute("username", principal.getName());
    	return "user/delete";
    }
    
    @PostMapping("/delete")
    // 비밀번호를 가져오지 않는 이유: 비밀번호는 암호화되어있어서 비교가 불가능
    public String delete(@RequestParam(value="nickname") String nickname, @RequestParam(value="email") String email, 
    					 @RequestParam(value="phoneNm") String phoneNm, Principal principal, Model model) {
    	User user = userService.getUser(principal.getName());	
    	
    	if (user.getNickname().equals(nickname) && user.getEmail().equals(email) && user.getPhoneNm().equals(phoneNm)) {
    		userService.deleteUser(principal.getName());
    		model.addAttribute("msg", "회원 탈퇴가 완료되었습니다.");
			model.addAttribute("url", "/auth/login");
    		
    	} else {
    		model.addAttribute("msg", "일치하는 사용자 정보가 없습니다.");
			model.addAttribute("url", "/auth/delete");
    	}
    	return "alert";
    }
	
    @GetMapping("/approval")
    public String showApprovalList(Model model, Principal principal) {
    	User user = userService.getUser(principal.getName());
    	model.addAttribute("user", user);
    	List<WaitingForAdminApproval> waitingForAdminApprovalList = userService.getwaitingForAdminApprovalList();
    	model.addAttribute("waitingForAdminApprovalList", waitingForAdminApprovalList);
    	return "user/approval";
    }
    
    @PostMapping("/approval")
    public String approval(@RequestParam(value="username") String username) {
    	User user = userService.getUser(username);
    	WaitingForAdminApproval waitingUser = userService.getWaitingForAdminApprovalUser(user);
    	
    	// 자바 내장 클래스 GrantedAuthority 자료형으로 리스트를 만듦
    	// UserRole에서 권한(ADMIN or USER)를 얻어와서 넣기 위한 용도
    	List<GrantedAuthority> authorities = new ArrayList<>();
    		
		waitingUser.setApproval(true);	// 관리자 승인 대기 목록에서 승인을 했으므로 approval을 true로 바꾸기
		userService.updateWaitingForAdminApprovalUser(waitingUser);		// 관리자 승인 대기 목록 업데이트
		
		// role이 관리자이면 관리자(ADMIN) 권한을 얻어서 내장 권한 클래스 리스트에 넣음
		authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getValue()));
		
		// 현재 사용자의 Principal을 가져옴
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 특정 사용자의 Authentication 객체를 가져와서 권한을 갱신
        UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.getPassword(),
                authorities);

        // SecurityContextHolder에 특정 사용자의 Authentication 객체를 설정
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
        
    	return "redirect:/auth/approval";
    }
    
    // 전체 회원 관리 페이지 이동
    @GetMapping("/management") 
    public String management(Model model, Principal principal) {
    	User user = userService.getUser(principal.getName());
    	model.addAttribute("user", user);
    	List<User> userList = userService.listUsers();
    	model.addAttribute("userList", userList);
    	return "user/management";
    }
    
    // 회원 강제 탈퇴 기능
    @PostMapping("forcedDelete")
    public String forcedDelete(@RequestParam(value="username") String username) {
    	userService.deleteUser(username);
    	return "redirect:/auth/management";
    }
    
    // 회원 검색 기능
    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model, Principal principal) {
		// 사용자 정보를 Principal에서 가져오도록 변경
	    User user = userService.getUser(principal.getName());
	    model.addAttribute("user", user);
	    model.addAttribute("keyword", keyword);
        List<User> searchList = userService.searchUser(keyword);
        model.addAttribute("searchList", searchList); 
        return "user/search"; // 검색 결과를 보여줄 HTML 페이지 이름
    }
}
