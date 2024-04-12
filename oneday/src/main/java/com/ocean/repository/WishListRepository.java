package com.ocean.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ocean.model.entity.Oneday;
import com.ocean.model.entity.User;
import com.ocean.model.entity.WishList;

public interface WishListRepository extends JpaRepository<WishList, Integer> {
	
	// 특정 사용자의 위시리스트를 불러오는 메서드
    List<WishList> findByUserId(int userId);
    
    Optional<WishList> findByUserIdAndOnedayId(int userId, int onedayId);
    
    // 사용자와 원데이를 기반으로 WishList 엔티티를 조회하는 메소드
    WishList findByUserAndOneday(User user, Oneday oneday);
}