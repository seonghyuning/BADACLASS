package com.ocean.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ocean.model.entity.Oneday;
import com.ocean.model.entity.Review;
import com.ocean.model.entity.User;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
	List<Review> findByOneday(Oneday oneday);
	List<Review> findByUser(User user);
	List<Review> findByOnedayOrderByRateDesc(Oneday oneday);	// 별점 내림차순 (별점 높은순)
	List<Review> findByOnedayOrderByRateAsc(Oneday oneday);		// 별점 오름차순 (별점 낮은순)
	List<Review> findByOnedayOrderByDateDesc(Oneday oneday);	// 날짜 내림차순 (최신순)
	List<Review> findByOnedayOrderByDateAsc(Oneday oneday);		// 날짜 오름차순 (오래된순)
}
