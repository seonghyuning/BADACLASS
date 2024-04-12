package com.ocean.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ocean.model.entity.Oneday;
import com.ocean.model.entity.Reservation;
import com.ocean.model.entity.User;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
	List<Reservation> findByOneday(Oneday oneday, Sort sort);		// Oneday로 Reservation의 List 뽑아오기
	List<Reservation> findByUser(User user, Sort sort);			// User로 Reservation의 List 뽑아오기
    Optional<Reservation> findById(int reservationId);  // ID로 예약을 찾는 메서드 추가
	
    // Oneday 및 특정 날짜에 해당하는 예약을 찾는 메서드
    List<Reservation> findByOnedayAndDate(Oneday oneday, LocalDateTime date);
    
    List<Reservation> findByOnedayAndDateNotNull(Oneday oneday);
    
    List<Reservation> findByOnedayAndUser(Oneday oneday, User user);
}
