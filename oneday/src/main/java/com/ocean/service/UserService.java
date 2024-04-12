package com.ocean.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ocean.model.entity.User;
import com.ocean.model.entity.WaitingForAdminApproval;
import com.ocean.repository.UserRepository;
import com.ocean.repository.WaitingForAdminApprovalRepository;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private WaitingForAdminApprovalRepository waitingForAdminApprovalRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public User addUser(String nickname, String username, String password,
						String gender, String year, String phoneNm,
						String email, String role) {
		User user = new User();
		user.setNickname(nickname);
		user.setUsername(username);
		user.setGender(gender);
		user.setYear(year);
		user.setPhoneNm(phoneNm);
		user.setEmail(email);
		user.setRole(role);
		// password를 그냥 넣지 말고 암호화 시켜서 넣어줘야 함
		user.setPassword(passwordEncoder.encode(password));
		userRepository.save(user);
		return user;
	}
	
	public List<User> listUsers() throws DataAccessException {
		// 이름(nickname)으로 오름차순(가나다순) 정렬
		List<User> userList = userRepository.findAll(Sort.by(Sort.Direction.ASC, "nickname"));
		return userList;
	}
	
	public User getUser(String username) throws DataAccessException {
		Optional<User> optionalUser = userRepository.findByUsername(username);
		User user = null;
		if (optionalUser.isPresent()) {
			user = optionalUser.get();
		} else {
			throw new DataNotFoundException("user not found");
		}
		return user;
	}
	
	public String findUserId(String email, String nickname) {
        User user = userRepository.findByEmailAndNickname(email, nickname);
        return user != null ? user.getUsername() : null;
	}
	
	// 사용자의 예전 비밀번호 조회
    public String getOldPassword(String username) {
        // 예시로 고정된 값 반환, 실제로는 데이터베이스에서 조회해야 함
        // 여기에서는 간단한 구현을 위해 예전 비밀번호를 "oldPassword"로 가정합니다.
        return "oldPassword";
    }
	
	// 사용자 정보를 확인하여 유효성을 검사하는 메서드
    public User findByPw(String nickname, String username, String email) {
        return userRepository.findByNicknameAndUsernameAndEmail(nickname, username, email);
    }
	
 
    //사용자 비밀번호 업데이트
    public void updatePassword(User user, String newpassword){
    	Optional<User> optionalUser = userRepository.findByUsername(user.getUsername());
    	
        // 사용자 정보가 존재하면 비밀번호 업데이트
        if(optionalUser.isPresent()) {
        	user.setPassword(passwordEncoder.encode(newpassword));
            userRepository.save(user);
        }else {
			throw new DataNotFoundException("user not found");
		}
    }
    
	public void deleteUser(String username) throws DataAccessException {
		Optional<User> optionalUser = userRepository.findByUsername(username);
		User user = null;
		if (optionalUser.isPresent()) {
			user = optionalUser.get();
			userRepository.delete(user);
		} else {
			throw new DataNotFoundException("user not found");
		}
	}
	
	 public Integer getUserId() {
	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        if (authentication != null && authentication.isAuthenticated()) {
	            String username = authentication.getName();
	            return userRepository.findByUsername(username).map(User::getId).orElse(null);
	        }
	        return null;
	 }
	 
	 public void updateUser(User inUser) throws DataAccessException {
		 Optional<User> optionalUser = userRepository.findByUsername(inUser.getUsername());
		 User user = null;
		 if (optionalUser.isPresent()) {
			 user = optionalUser.get();
			 user.setNickname(inUser.getNickname());
			 user.setPhoneNm(inUser.getPhoneNm());
			 user.setEmail(inUser.getEmail());
			 userRepository.save(user);
		 } else {
			throw new DataNotFoundException("user not found");
		 }
	 }
	 
	// 관리자 승인 대기 회원 리스트 가져오기
	 public List<WaitingForAdminApproval> getwaitingForAdminApprovalList() throws DataAccessException {		 
		 List<WaitingForAdminApproval> watingForAdminApprovalList = waitingForAdminApprovalRepository.findAll();
		 return watingForAdminApprovalList;
	 }
	 
	 // 관리자 승인 대기 회원 1명 가져오기
	 public WaitingForAdminApproval getWaitingForAdminApprovalUser(User user) throws DataAccessException {
		 Optional<WaitingForAdminApproval> optionalWaitingUser = waitingForAdminApprovalRepository.findByUser(user);
		 WaitingForAdminApproval waitingUser = null;
		 if (optionalWaitingUser.isPresent()) {
			 waitingUser = optionalWaitingUser.get();
		 }
		 return waitingUser;
	 }
	 
	 // 관리자 승인 대기 회원 추가하기
	 public void addWaitingForAdminApprovalUser(WaitingForAdminApproval waitingForAdminApproval) throws DataAccessException {
		 waitingForAdminApprovalRepository.save(waitingForAdminApproval);
	 }
	 
	 // 관리자 승인 대기 회원 수정하기
	 public void updateWaitingForAdminApprovalUser(WaitingForAdminApproval inWaitingForAdminApproval) throws DataAccessException {
		 Optional<WaitingForAdminApproval> optionalWaitingUser = waitingForAdminApprovalRepository.findByUser(inWaitingForAdminApproval.getUser());
		 WaitingForAdminApproval waitingForAdminApproval = null;
		 if (optionalWaitingUser.isPresent()) {
			 waitingForAdminApproval = optionalWaitingUser.get();
			 waitingForAdminApproval.setUser(inWaitingForAdminApproval.getUser());
			 waitingForAdminApproval.setApproval(inWaitingForAdminApproval.isApproval());
			 waitingForAdminApprovalRepository.save(waitingForAdminApproval);
		 }
	 }
	 
	// 키워드 검색으로 user 리스트 가져오기
	public List<User> searchUser(String keyword) {
		List<User> searchList = userRepository.findByNicknameContainingIgnoreCase(keyword);
	    return searchList;
	}
}
