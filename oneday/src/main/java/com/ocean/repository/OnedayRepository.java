package com.ocean.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ocean.model.entity.Oneday;
import com.ocean.model.entity.OnedayCategory;
import com.ocean.model.entity.User;

public interface OnedayRepository extends JpaRepository<Oneday, Integer> {
	List<Oneday> findByCategory(OnedayCategory category);
	Optional<Oneday> findByName(String name);
	List<Oneday> findByUser(User user);
	// 검색을 위한 메서드 추가
    List<Oneday> findByNameContainingIgnoreCase(String keyword);
    // 지역 검색
    List<Oneday> findByLocationContaining(String region);
    
    //현재 위치 주변에 있는 원데이 클래스 검색
    @Query("SELECT o FROM Oneday o WHERE FUNCTION('ST_DISTANCE', FUNCTION('ST_GeomFromText', FUNCTION('CONCAT', 'POINT(', o.latitude, ' ', o.longitude, ')'), 4326), FUNCTION('ST_GeomFromText', FUNCTION('CONCAT', 'POINT(', ?1, ' ', ?2, ')'), 4326)) < 2000")
    List<Oneday> findNearbyClasses(Double latitude, Double longitude);
    
    // 15명 이상의 수강생을 수용할 수 있는 클래스 찾기
    @Query("SELECT o FROM Oneday o WHERE o.availableSlots >= 15")
    List<Oneday> findClassesWithAvailableSlotsForFifteenOrMore();
}

