package com.ocean.model.entity;
  
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
  
@Getter 
@Setter
@NoArgsConstructor
@Entity
@Table(name = "question")
public class Question {
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
  
	@Column(length=100, name="question_title", nullable = false)
	private String question_title;
  
	@Column(length=100, name="question_content", nullable = true)
	private String question_content;
  
	@CreationTimestamp
	@Column(name = "question_date", nullable = false, updatable = false)
	private LocalDateTime question_date;

	@CreationTimestamp
	@Column(name = "question_mdate", nullable = true)
	private LocalDateTime question_mdate;
	
	@Column(length=2000)
	private String answer;
  
	@ManyToOne(fetch = FetchType.EAGER)
	private User user;

	@ManyToOne(fetch = FetchType.EAGER)
	private Oneday oneday;
	
	public String getOnedayName() {
        return (oneday != null) ? oneday.getName() : null;
    }

}
 