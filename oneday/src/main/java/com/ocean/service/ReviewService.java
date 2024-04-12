package com.ocean.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.ocean.model.entity.Oneday;
import com.ocean.model.entity.Reservation;
import com.ocean.model.entity.Review;
import com.ocean.model.entity.User;
import com.ocean.repository.ReservationRepository;
import com.ocean.repository.ReviewRepository;

@Service
public class ReviewService {
	@Autowired
	private ReviewRepository reviewRepository;
	
	@Autowired
	private ReservationRepository reservationRepository;
	
	// 리뷰 리스트 얻어오기 (oneday를 매개변수로)
	public List<Review> getReviewList(Oneday oneday) throws DataAccessException {
		List<Review> reviewList = reviewRepository.findByOneday(oneday);
		return reviewList;
	}
	
	// 리뷰 리스트 얻어오기 (user를 매개변수로)
	public List<Review> getReviewList(User user) throws DataAccessException {
		List<Review> reviewList = reviewRepository.findByUser(user);
		return reviewList;
	}
	
	// 리뷰 1개 얻어오기
	public Review getReview(int id) throws DataAccessException {
		Optional<Review> optionalReview = reviewRepository.findById(id);
		Review review = null;
		if (optionalReview.isPresent()) {
			review = optionalReview.get();
		}
		return review;
	}
	
	// 리뷰 1개 추가
	public void addReview(Review review) throws DataAccessException {
		reviewRepository.save(review);	// repository의 내장 메서드 이용하여 추가
	}
	
	// 리뷰 수정
	public void updateReview(Review inReview) throws DataAccessException {
		Optional<Review> optionalReview = reviewRepository.findById(inReview.getId());
		Review review = null;
		if (optionalReview.isPresent()) {
			review = optionalReview.get();
			review.setComment(inReview.getComment());
			reviewRepository.save(review);
		}
	}
	
	// 리뷰 삭제
	public void removeReview(int id) throws DataAccessException {
		Optional<Review> optionalReview = reviewRepository.findById(id);
		Review review = null;
		if (optionalReview.isPresent()) {
			review = optionalReview.get();
			reviewRepository.delete(review);
		}
	}
	
	// 리뷰 별점 내림차순 (리뷰 별점 높은순)
	public List<Review> getReviewListOrderByRateDesc(Oneday oneday) {
		List<Review> reviewListOrderByRateDesc = null;
		reviewListOrderByRateDesc = reviewRepository.findByOnedayOrderByRateDesc(oneday);
		return reviewListOrderByRateDesc;
	}
	
	// 리뷰 별점 오름차순 (리뷰 별점 낮은순)
	public List<Review> getReviewListOrderByRateAsc(Oneday oneday) {
		List<Review> reviewListOrderByRateAsc = null;
		reviewListOrderByRateAsc = reviewRepository.findByOnedayOrderByRateAsc(oneday);
		return reviewListOrderByRateAsc;
	}

	// 리뷰 최신순 (리뷰 최신순)
	public List<Review> getReviewListOrderByDateDesc(Oneday oneday) {
		List<Review> reviewListOrderByDateDesc = null;
		reviewListOrderByDateDesc = reviewRepository.findByOnedayOrderByDateDesc(oneday);
		return reviewListOrderByDateDesc;
	}
	
	// 리뷰 오래된순 (리뷰 오래된순)
	public List<Review> getReviewListOrderByDateAsc(Oneday oneday) {
		List<Review> reviewListOrderByDateAsc = null;
		reviewListOrderByDateAsc = reviewRepository.findByOnedayOrderByDateAsc(oneday);
		return reviewListOrderByDateAsc;
	}
	
	// 클래스 예약한 날짜 이후에만 리뷰 작성 가능하게 하는 메서드
	public boolean isReviewable(Oneday oneday, User user) {
        List<Reservation> reservations = reservationRepository.findByOnedayAndUser(oneday, user);
        
        if (!reservations.isEmpty()) {
            // 예약 정보 중에서 최신 날짜의 예약 정보를 가져옴
            Reservation latestReservation = reservations.stream()
                .max(Comparator.comparing(Reservation::getDate))
                .orElseThrow(NoSuchElementException::new);

            LocalDateTime reservedDate = latestReservation.getDate();
            LocalDateTime currentDate = LocalDateTime.now();

            // 현재 날짜가 예약 날짜 이후인지 확인
            return currentDate.isAfter(reservedDate);
        }

        // 예약 정보가 없으면 리뷰 작성 불가
        return false;
    }
}








