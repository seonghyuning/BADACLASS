package com.ocean.model.entity;

import org.springframework.stereotype.Component;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Component
public class WaitingForAdminApproval {	// 관리자 승인 대기 회원 테이블
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(nullable = false)
	private int id;
	
	@ManyToOne
	private User user;
	
	@Column(nullable = false)
	private boolean approval = false;	// 회원 승인 여부 (관리자로 선택할 시 false. 대표 관리자가 승인을 해야 true로 바뀜)
}
