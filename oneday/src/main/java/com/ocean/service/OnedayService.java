package com.ocean.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.ocean.model.entity.Oneday;
import com.ocean.model.entity.OnedayCategory;
import com.ocean.model.entity.Reservation;
import com.ocean.model.entity.Review;
import com.ocean.model.entity.User;
import com.ocean.repository.OnedayCategoryRepository;
import com.ocean.repository.OnedayRepository;
import com.ocean.repository.ReservationRepository;
import com.ocean.repository.ReviewRepository;

import jakarta.transaction.Transactional;

@Service
public class OnedayService {
	@Autowired
	private OnedayRepository onedayRepository;
	
	@Autowired
	private OnedayCategoryRepository onedayCategoryRepository;
	
	@Autowired
	private ReservationRepository reservationRepository;
	
	@Autowired
	private ReviewRepository reviewRepository;
	
	@Autowired
	private GeocodingService geocodingService;
	
	
	
	public Oneday getOneday(int id) throws DataAccessException {
		Optional<Oneday> optionalOneday = onedayRepository.findById(id);
		Oneday oneday;
		if (optionalOneday.isPresent()) {
			oneday = optionalOneday.get();
		} else {
			oneday = null;
		}
		return oneday;
	}
	
	public Oneday getOneday(String name) throws DataAccessException {
		Optional<Oneday> optionalOneday = onedayRepository.findByName(name);
		Oneday oneday;
		if (optionalOneday.isPresent()) {
			oneday = optionalOneday.get();
		} else {
			oneday = null;
		}
		return oneday;
	}
	
	public List<Oneday> getAllOnedayList() throws DataAccessException {
		List<Oneday> allOnedayList = onedayRepository.findAll();
		return allOnedayList;
	}
	
	public List<Oneday> getOnedayList(int categoryCode) throws DataAccessException {
		OnedayCategory category = getCategory(categoryCode);
		List<Oneday> onedayList = onedayRepository.findByCategory(category);
		return onedayList;
	}
	
	// 카테고리 코드로 카테고리 얻어오기
	public OnedayCategory getCategory(int categoryCode) throws DataAccessException {
		Optional<OnedayCategory> optionalCategory = onedayCategoryRepository.findById(categoryCode);
		OnedayCategory category = null;
		if (optionalCategory.isPresent()) {
			category = optionalCategory.get();
		} 
		return category;
	}
	
	// 카테고리 이름으로 카테고리 얻어오기
	public OnedayCategory getCategory(String name) throws DataAccessException {
		Optional<OnedayCategory> optionalCategory = onedayCategoryRepository.findByName(name);
		OnedayCategory category = null;
		if (optionalCategory.isPresent()) {
			category = optionalCategory.get();
		} 
		return category;
	}
	
	public List<OnedayCategory> getCategoryList() throws DataAccessException {
		List<OnedayCategory> categoryList = onedayCategoryRepository.findAll();
		return categoryList;
	}
	
	public List<Oneday> getmyOnedayList(User user) throws DataAccessException {
		List<Oneday> myOnedayList = onedayRepository.findByUser(user);
		return myOnedayList;
	}
	
	//키워드 검색으로 oneday리스트 가져오기
	public List<Oneday> searchOneday(String keyword) {
		List<Oneday> searchList = onedayRepository.findByNameContainingIgnoreCase(keyword);
	    return searchList;
	}
	
	public void addOneday(Oneday oneday) throws DataAccessException {
		onedayRepository.save(oneday);
	}
	
	public void updateOneday(Oneday inOneday) throws DataAccessException {
		Optional<Oneday> optionalOneday = onedayRepository.findById(inOneday.getId());
		Oneday oneday = null;
		if (optionalOneday.isPresent()) {
			oneday = optionalOneday.get();
			oneday.setName(inOneday.getName());
			oneday.setLocation(inOneday.getLocation());
			oneday.setCategory(inOneday.getCategory());
			// 이미지 나중에 수정
			oneday.setContent(inOneday.getContent());
			oneday.setCurri(inOneday.getCurri());
			onedayRepository.save(oneday);
		}
	}
	
	public void removeOneday(int id) throws DataAccessException {
		Optional<Oneday> optionalOneday = onedayRepository.findById(id);
		Oneday oneday = null;
		if (optionalOneday.isPresent()) {
			oneday = optionalOneday.get();
			onedayRepository.delete(oneday);
		}
	}

    
    public int getAvailableSlots(int onedayId) {
        // Oneday 엔티티를 찾습니다.
        Oneday oneday = onedayRepository.findById(onedayId).orElse(null);

        // Oneday가 null이 아니면 가능한 자리 수를 반환합니다.
        return (oneday != null) ? oneday.getAvailableSlots() : 0;
    }
    
    public void addCategory(String newCategoryName) {
    	OnedayCategory category = new OnedayCategory();
    	category.setName(newCategoryName);
    	onedayCategoryRepository.save(category);
    }
    
    // 조회수 증가 처리 메서드
    @Transactional
    public Oneday increaseViewCount(int onedayId) {
        Oneday oneday = onedayRepository.findById(onedayId).orElse(null);

        if (oneday != null) {
            // 조회수 증가
            oneday.setView(oneday.getView() + 1);
            return onedayRepository.save(oneday);
        }

        return null;
    }
    
    // 원데이클래스별 리뷰 평점 전체 평균 계산
 	public void calculateAverageRate(int onedayId) {
 		Oneday oneday = getOneday(onedayId);
 		List<Review> reviewList = reviewRepository.findByOneday(oneday);	// 원데이클래스별 리뷰 리스트 가져오기

 		if (reviewList == null || reviewList.isEmpty()) {
 			oneday.setAverageRate(0.0);
 		} else {
 			double totalRate = 0.0;
	        for (Review review : reviewList) {
	            totalRate += review.getRate();	// 리뷰 평점을 모두 더하기
	        }
	        double averageRate = totalRate / reviewList.size();		// 리뷰 평점의 평균 구하기
	        oneday.setAverageRate(averageRate);		// 평균 평점 set
 		}
 		onedayRepository.save(oneday);	
     }
 	
 	// 해당 메서드는 onedayService에 추가하여 사용합니다.
    public List<LocalDate> getFullyBookedDates(int onedayId) {
        Oneday oneday = onedayRepository.findById(onedayId).orElse(null);

        if (oneday == null) {
            // 해당 아이디의 원데이 클래스가 존재하지 않으면 빈 리스트 반환
            return Collections.emptyList();
        }

        // 예약 다 찬 날짜를 담을 리스트
        List<LocalDate> fullyBookedDates = new ArrayList<>();

        // 해당 원데이 클래스의 예약 리스트 가져오기
        List<Reservation> reservations = reservationRepository.findByOnedayAndDateNotNull(oneday);

        // 날짜별로 예약 인원 수 계산
        Map<LocalDate, Integer> reservationCountByDate = new HashMap<>();
        for (Reservation reservation : reservations) {
            LocalDate reservationDate = reservation.getDate().toLocalDate();
            reservationCountByDate.put(reservationDate, reservationCountByDate.getOrDefault(reservationDate, 0) + reservation.getCount());
        }

        // 날짜 별로 예약 다 찬 경우 fullyBookedDates에 추가
        for (Map.Entry<LocalDate, Integer> entry : reservationCountByDate.entrySet()) {
            if (entry.getValue() >= oneday.getAvailableSlots()) {
                fullyBookedDates.add(entry.getKey());
            }
        }

        return fullyBookedDates;
    }
    
    //선택한 지역에 있는 원데이 클래스 리스트
    public List<Oneday> searchOnedayClassesByRegion(String region) {
        // 해당 메서드를 호출하여 지역에 해당하는 클래스를 찾아옴
        return onedayRepository.findByLocationContaining(region);
    }
    
    // 위도와 경도를 기반으로 주변 클래스를 조회하는 메소드
    public List<Oneday> findNearbyClasses(Double latitude, Double longitude) {
        // 여기에 적절한 JPA 쿼리를 작성하여 주변 클래스를 조회하는 로직을 구현합니다.
        return onedayRepository.findNearbyClasses(latitude, longitude);
    }
    
    public List<Oneday> findClassesWithAvailableSlotsForFifteenOrMore() {
    	
    	return onedayRepository.findClassesWithAvailableSlotsForFifteenOrMore();
    }
    
    //내가 등록한 클래스 검색 기능
    public List<Oneday> searchMyOnedayList(User user, String classSearch) {
        List<Oneday> myOnedayList = getmyOnedayList(user);

        // Filtering classes based on the search term (case-insensitive)
        String searchLower = classSearch.toLowerCase();
        return myOnedayList.stream()
                .filter(oneday -> oneday.getName().toLowerCase().contains(searchLower))
                .collect(Collectors.toList());
    }

}









