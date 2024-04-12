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
public class Review {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(nullable = false)
	private int id;
	
	@ManyToOne
	private User user;
	
	@ManyToOne
	private Oneday oneday;
	
	@OneToOne
    private Image image;
	
	@Column(length=2000, name="review_comment", nullable = false)
	private String comment;
	
	@Column(nullable = false)
	private double rate = 0.0;
	
	@CreationTimestamp
	@Column(name="review_date", nullable = false)
	private LocalDateTime date;
	
	@CreationTimestamp
	@Column(name="review_mdate")
	private LocalDateTime mdate;
}
