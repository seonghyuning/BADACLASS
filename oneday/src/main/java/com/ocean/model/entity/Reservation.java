package com.ocean.model.entity;
import java.time.LocalDateTime;

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
public class Reservation {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(nullable = false)
	private int id;
	
	@ManyToOne
	private User user;
	
	@ManyToOne
	private Oneday oneday;
	
	@Column(name = "reservation_date", nullable = false)
	private LocalDateTime date;
	
	@Column(nullable = false)
	private int count = 0;
	
}
