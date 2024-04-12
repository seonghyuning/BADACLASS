package com.ocean.model.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.stereotype.Component;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Component
public class Oneday {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(nullable = false)
	private int id;
	
	@ManyToOne
	private OnedayCategory category;
	
	@ManyToOne
	private User user;
	
	@OneToOne
    private Image image;
	
	@Column(length=100, name="oneday_name", nullable = false)
	private String name;
	
	@Column(length=2000, name="oneday_content", nullable = false)
	private String content;
	
	@Column(length=2000, name="oneday_curri", nullable = false)
	private String curri;
	
	@Column(length=100, name="oneday_location", nullable=false)
	private String location;
	
	@Column(name="latitude", nullable=true)
	private Double latitude; // 위도

	@Column(name="longitude", nullable=true)
    private Double longitude; // 경도
	
	@Column(name="oneday_view", nullable = false)
	private int view = 0;
	
	@Column(nullable = false)
    private int availableSlots;		// 최대 예약 가능 인원 수
	
	@Column(nullable = false)
	private double averageRate = 0.0;	// 원데이클래스 리뷰 평점의 전체 평균
	
	@CreationTimestamp
	@Column(name="oneday_date", nullable = false)
	private LocalDateTime date;
	
	@CreationTimestamp
	@Column(name="oneday_mdate")
	private LocalDateTime mdate;
	
	// averageRate(리뷰 평점의 전체 평균) 값을 얻어올 때 소수점 첫째 자리에서 반올림
	public double getAverageRate() {
        return Math.round(this.averageRate * 10.0) / 10.0;
    }
}
