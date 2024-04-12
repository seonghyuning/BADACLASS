package com.ocean.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ocean.model.entity.OnedayCategory;
import com.ocean.model.entity.Reservation;
import com.ocean.model.entity.User;
import com.ocean.repository.OnedayCategoryRepository;
import com.ocean.repository.ReservationRepository;

@Service
public class OnedayCategoryService {
    @Autowired
    private OnedayCategoryRepository onedaycategoryRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    // 특정 사용자의 가장 많이 예약한 카테고리를 찾기 위한 메서드
    public OnedayCategory findMostReservedCategoryByUser(User user) {
        List<Reservation> userReservations = null;
        userReservations = reservationRepository.findByUser(user, Sort.by(Sort.Direction.ASC, "id"));
        
        if (userReservations == null || userReservations.isEmpty()) {
            return null;
        }

        // 카테고리별 예약 횟수를 저장하는 맵
        Map<OnedayCategory, Integer> categoryReservationCountMap = new HashMap<>();

        // 사용자의 예약 목록을 순회하면서 카테고리별 예약 횟수를 계산
        for (Reservation reservation : userReservations) {
            OnedayCategory category = reservation.getOneday().getCategory();
            categoryReservationCountMap.put(category, categoryReservationCountMap.getOrDefault(category, 0) + 1);
        }

        // 카테고리별 예약 횟수 중 가장 많은 카테고리를 찾기
        OnedayCategory mostReservedCategory = Collections.max(
                categoryReservationCountMap.entrySet(),
                Map.Entry.comparingByValue()
        ).getKey();

        return mostReservedCategory;
    }
    
    public List<OnedayCategory> getAllCategories() {
        // 데이터베이스에서 모든 카테고리를 가져오는 예시 코드
        return onedaycategoryRepository.findAll();
    }
}
