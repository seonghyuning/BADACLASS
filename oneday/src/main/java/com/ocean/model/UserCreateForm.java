package com.ocean.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateForm {	
	private String nickname;
	
	@Size(min=5, max=20, message="사용자 아이디를 5 ~ 20자 사이로 입력해주세요.") 
	private String username;	// 중복체크 -> UserController의 join 함수에서 함
	
	
	private String password1;
	
	private String password2;
	
	private String gender;
	
	private String year;
	
	private String phoneNm;
	
	private String email;
	
	private String role;
}
