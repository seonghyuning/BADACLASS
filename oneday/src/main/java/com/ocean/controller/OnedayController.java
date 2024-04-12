package com.ocean.controller;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.i18n.phonenumbers.NumberParseException;
import com.ocean.model.entity.MyLocation;
import com.ocean.model.entity.Oneday;
import com.ocean.model.entity.OnedayCategory;
import com.ocean.model.entity.Reservation;
import com.ocean.model.entity.Review;
import com.ocean.model.entity.User;
import com.ocean.model.entity.WishList;
import com.ocean.service.GeocodingService;
import com.ocean.service.GeocodingService.LatLng;
import com.ocean.service.ImageService;
import com.ocean.service.OnedayCategoryService;
import com.ocean.service.OnedayService;
import com.ocean.service.ReservationService;
import com.ocean.service.ReviewService;
import com.ocean.service.UserService;
import com.ocean.service.WishListService;

import jakarta.servlet.http.HttpSession;

@Controller
public class OnedayController {
	@Autowired
	private OnedayService onedayService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ReviewService reviewService;
	
	@Autowired
	private ReservationService reservationService;
	
	@Autowired
	private ImageService imageService;
	
	@Autowired
	private WishListService wishlistService;
	
	@Autowired
	private OnedayCategoryService onedayCategoryService;
	
	
	@GetMapping({"/main", "/"})
	public String getMainPage(Model model, Principal principal) {
	    // 사용자가 인증되어 있지 않은 경우
	    if (principal == null || "anonymousUser".equals(principal.getName())) {
	        // 로그인 페이지로 리디렉션
	        return "redirect:/auth/login";
	    }

	    // 전체 Oneday 리스트 가져오기
	    List<Oneday> allOnedayList = onedayService.getAllOnedayList();
	    model.addAttribute("onedayList", allOnedayList);

	    // 카테고리 리스트 가져오기
	    List<OnedayCategory> categoryList = onedayService.getCategoryList();
	    model.addAttribute("categoryList", categoryList);

	    // Principal을 이용하여 사용자 정보 가져오기
	    User user = userService.getUser(principal.getName());
	    model.addAttribute("user", user);

	    // 사용자가 찜한 Oneday들의 위시리스트 상태 가져오기
	    List<Boolean> wishedStatusList = new ArrayList<>();
	    for (Oneday oneday : allOnedayList) {
	        boolean isWished = wishlistService.isOnWishlist(user, oneday);
	        wishedStatusList.add(isWished);
	    }
	    model.addAttribute("wishedStatusList", wishedStatusList);    
	    
	    List<String> cities = Arrays.asList("서울", "부산", "인천", "대구", "광주", "대전", "울산", "세종", "경기", "강원", "충북", "충남", "전북", "전남", "경북", "경남", "제주");
        model.addAttribute("cities", cities);


	    // 로그인된 사용자의 경우 메인 페이지로 이동
	    return "main";
	}
	
	
	
	@PostMapping("/mylocationupdate")
	public String updateMylocation(@RequestBody Map<String, Object> requestData, Principal principal, HttpSession session) {
	    double latitude = Double.parseDouble(requestData.get("latitude").toString()); 
	    double longitude = Double.parseDouble(requestData.get("longitude").toString());
	    
	    //MyLocation 객체 생성 및 값 설정
	    MyLocation mylocation = new MyLocation();
	    mylocation.setLatitude(latitude);
	    mylocation.setLongitude(longitude);
	 
	    // 세션에 MyLocation 저장
	    session.setAttribute("mylocation", mylocation);

	    // 리다이렉트할 GET 매핑 주소를 리턴
	    return "redirect:/mylocation";
	}
	
	@GetMapping("/mylocation")
	public String findNearbyClasses(Model model, HttpSession session, Principal principal) {
	    // 세션에서 MyLocation 가져오기
	    MyLocation mylocation = (MyLocation) session.getAttribute("mylocation");

	    if (mylocation == null) {
	        return "redirect:/main"; // 또는 다른 적절한 경로로 리다이렉트
	    }

	    double latitude = mylocation.getLatitude();
	    double longitude = mylocation.getLongitude();
		
	    // 여기서는 위도와 경도를 기반으로 주변 클래스를 조회하는 비즈니스 로직을 수행합니다.
	    List<Oneday> mylocationList = onedayService.findNearbyClasses(latitude, longitude);
	    model.addAttribute("mylocationList", mylocationList);

	    // 카테고리 리스트 가져오기
	    List<OnedayCategory> categoryList = onedayService.getCategoryList();
	    model.addAttribute("categoryList", categoryList);

	    // Principal을 이용하여 사용자 정보 가져오기
	    User user = userService.getUser(principal.getName());
	    model.addAttribute("user", user);

	    // 사용자가 찜한 Oneday들의 위시리스트 상태 가져오기
	    List<Boolean> wishedStatusList = new ArrayList<>();
	    for (Oneday oneday : mylocationList) {
	        boolean isWished = wishlistService.isOnWishlist(user, oneday);
	        wishedStatusList.add(isWished);
	    }
	    model.addAttribute("wishedStatusList", wishedStatusList);
	    
	    List<String> cities = Arrays.asList("서울", "부산", "인천", "대구", "광주", "대전", "울산", "세종", "경기", "강원", "충북", "충남", "전북", "전남", "경북", "경남", "제주");
        model.addAttribute("cities", cities);
        
	    // 조회된 주변 클래스 리스트를 반환합니다.
	    return "board/mylocationList";
	}
	
	@GetMapping("/group")
	public String group_Reservation(Model model, Principal principal) {
		//전체 수강 가능한 인원수가 15명 이상인 클래스들 담기
		List<Oneday> groupList = onedayService.findClassesWithAvailableSlotsForFifteenOrMore();
		model.addAttribute("groupList", groupList);
		 // 카테고리 리스트 가져오기
	    List<OnedayCategory> categoryList = onedayService.getCategoryList();
	    model.addAttribute("categoryList", categoryList);

	    // Principal을 이용하여 사용자 정보 가져오기
	    User user = userService.getUser(principal.getName());
	    model.addAttribute("user", user);

	    // 사용자가 찜한 Oneday들의 위시리스트 상태 가져오기
	    List<Boolean> wishedStatusList = new ArrayList<>();
	    for (Oneday oneday : groupList) {
	        boolean isWished = wishlistService.isOnWishlist(user, oneday);
	        wishedStatusList.add(isWished);
	    }
	    model.addAttribute("wishedStatusList", wishedStatusList);  
	    
	    List<String> cities = Arrays.asList("서울", "부산", "인천", "대구", "광주", "대전", "울산", "세종", "경기", "강원", "충북", "충남", "전북", "전남", "경북", "경남", "제주");
        model.addAttribute("cities", cities);

	    // 결과를 보여줄 템플릿으로 이동
	    return "board/group";
	}
	
	
	@GetMapping("/searchLocation/city")
	public String searchOnedayClassesByRegion(@RequestParam(value = "city", required = false) String city, Model model, Principal principal) {

	    // 선택한 지역에 해당하는 원데이 클래스 검색
	    List<Oneday> locationList = onedayService.searchOnedayClassesByRegion(city);
	    model.addAttribute("locationList", locationList);
	    
	    // 카테고리 리스트 가져오기
	    List<OnedayCategory> categoryList = onedayService.getCategoryList();
	    model.addAttribute("categoryList", categoryList);

	    // Principal을 이용하여 사용자 정보 가져오기
	    User user = userService.getUser(principal.getName());
	    model.addAttribute("user", user);

	    // 사용자가 찜한 Oneday들의 위시리스트 상태 가져오기
	    List<Boolean> wishedStatusList = new ArrayList<>();
	    for (Oneday oneday : locationList) {
	        boolean isWished = wishlistService.isOnWishlist(user, oneday);
	        wishedStatusList.add(isWished);
	    }
	    model.addAttribute("wishedStatusList", wishedStatusList);  
	    
	    List<String> cities = Arrays.asList("서울", "부산", "인천", "대구", "광주", "대전", "울산", "세종", "경기", "강원", "충북", "충남", "전북", "전남", "경북", "경남", "제주");
        model.addAttribute("cities", cities);

	    // 결과를 보여줄 템플릿으로 이동
	    return "board/locationList";
	}
	
	
	
	@GetMapping("/board/category")
    @ResponseBody
    public List<OnedayCategory> getCategoryList() {
        // CategoryService를 통해 카테고리 목록을 가져옴
        return onedayCategoryService.getAllCategories();
    }

	@GetMapping("/category")
	public String category(@RequestParam(value="page", required=false) String categoryCode, Model model, Principal principal) {
		// 사용자 정보를 Principal에서 가져오도록 변경
	    User user = userService.getUser(principal.getName());
	    model.addAttribute("user", user);
	    List<Oneday> onedayList;
	    if (categoryCode != null) {	// 특정 카테고리 코드의 리스트만 뽑아오기 
	    	onedayList = onedayService.getOnedayList(Integer.parseInt(categoryCode));
	    } else {	// 전체 카테고리 뽑아오기 (카테고리 코드를 선택하지 않았을 때)
	    	onedayList = onedayService.getAllOnedayList();
	    }
	    model.addAttribute("onedayList", onedayList);
	    List<OnedayCategory> categoryList = onedayService.getCategoryList();
	    model.addAttribute("categoryList", categoryList);
	    model.addAttribute("categoryCode", categoryCode);
	    
	    // 사용자가 찜한 Oneday들의 위시리스트 상태 가져오기
	    List<Boolean> wishedStatusList = new ArrayList<>();
	    for (Oneday oneday : onedayList) {
	        boolean isWished = wishlistService.isOnWishlist(user, oneday);
	        wishedStatusList.add(isWished);
	    }
	    model.addAttribute("wishedStatusList", wishedStatusList);
	    
	    List<String> cities = Arrays.asList("서울", "부산", "인천", "대구", "광주", "대전", "울산", "세종", "경기", "강원", "충북", "충남", "전북", "전남", "경북", "경남", "제주");
        model.addAttribute("cities", cities);
	    
	    return "category";
	}
	
	@GetMapping("/board/search")
    public String search(@RequestParam String keyword, Model model, Principal principal) {
		// 사용자 정보를 Principal에서 가져오도록 변경
	    User user = userService.getUser(principal.getName());
	    model.addAttribute("user", user);
	    model.addAttribute("keyword", keyword);
        List<Oneday> searchList = onedayService.searchOneday(keyword);
        model.addAttribute("searchList", searchList);
        
        // 카테고리 리스트 가져오기
	    List<OnedayCategory> categoryList = onedayService.getCategoryList();
	    model.addAttribute("categoryList", categoryList);
        
        // 사용자가 찜한 Oneday들의 위시리스트 상태 가져오기
	    List<Boolean> wishedStatusList = new ArrayList<>();
	    for (Oneday oneday : searchList) {
	        boolean isWished = wishlistService.isOnWishlist(user, oneday);
	        wishedStatusList.add(isWished);
	    }
	    model.addAttribute("wishedStatusList", wishedStatusList);
	    
	    List<String> cities = Arrays.asList("서울", "부산", "인천", "대구", "광주", "대전", "울산", "세종", "경기", "강원", "충북", "충남", "전북", "전남", "경북", "경남", "제주");
        model.addAttribute("cities", cities);
	    
        return "board/search"; // 검색 결과를 보여줄 HTML 페이지 이름
    }
	
	@GetMapping("/board/write")
	@PreAuthorize("isAuthenticated()")
	public String writeOnedayClass(Model model, Principal principal) {
	    // 사용자 정보를 Principal에서 가져오도록 변경
	    User user = userService.getUser(principal.getName());
	    model.addAttribute("user", user);
	    List<OnedayCategory> categoryList = onedayService.getCategoryList();
	    model.addAttribute("categoryList", categoryList);
	    return "board/write";
	}

	@PostMapping("/board/add")
	@PreAuthorize("isAuthenticated()")
	public String addOnedayClass(@RequestParam(value = "name") String name,
								 @RequestParam(value = "sample6_address") String location,
								 @RequestParam(value = "sample6_detailAddress", required = false) String detailLocation,
	                             @RequestParam(value = "categoryCode", required = false) String categoryCode,
	                             @RequestParam(value = "content") String content,
	                             @RequestParam(value = "curri") String curri, 
	                             @RequestParam(value = "availableSlots") String availableSlots,
	                             @RequestParam(value = "newCategoryName", required = false) String newCategoryName,
	                             @RequestParam(value = "file") MultipartFile file, 
	                             @RequestParam(value = "imageName") String imageName, Principal principal) throws IOException {
	    // 사용자 정보를 Principal에서 가져오도록 변경
	    User user = userService.getUser(principal.getName());

	    Oneday oneday = new Oneday();
	    oneday.setName(name);
	    oneday.setLocation(location + " " + detailLocation);
	    oneday.setContent(content);
	    oneday.setCurri(curri);
	    oneday.setAvailableSlots(Integer.parseInt(availableSlots));
	    oneday.setUser(user);
	    
	    // 주소를 위도, 경도로 변환하여 업데이트
        LatLng coordinates = GeocodingService.geocodeAddress(location);
        oneday.setLatitude(coordinates.getLatitude());
        oneday.setLongitude(coordinates.getLongitude());
	    
	    
	    OnedayCategory category;
	    if (categoryCode != null) {	// 원래 있는 카테고리를 골랐을 때
		    category = onedayService.getCategory(Integer.parseInt(categoryCode));	// 선택한 카테고리의 코드로 카테고리 뽑아오기
	    } else {	// 카테고리를 직접 추가했을 때 (카테고리 선택 옵션에서 아무것도 선택을 하지 않았을 때. 즉, '카테고리' 옵션을 선택했을 떄)
	    	onedayService.addCategory(newCategoryName);				// 카테고리 이름으로 카테고리 추가
	    	category = onedayService.getCategory(newCategoryName);	// 추가한 카테고리 이름으로 카테고리 뽑아오기
	    }
	    oneday.setCategory(category);
	    
	    imageService.saveOnedayWithImage(oneday, file, imageName);	// 이미지 Oneday에 저장
	    onedayService.addOneday(oneday);
	    return "redirect:/main";
	}

	// 관리자가 등록한 클래스 리스트 페이지로 이동
	@GetMapping("/board/myclassAdmin")
	public String showMyclassAdminList(@RequestParam(name = "classSearch", required = false, defaultValue = "") String classSearch, Model model, Principal principal) {
	    User user = userService.getUser(principal.getName());
	    model.addAttribute("user", user);

	    List<Oneday> myOnedayList;

	    // 검색어가 있으면 필터링된 리스트를 사용하고, 없으면 전체 리스트를 가져옵니다.
	    if (!classSearch.isEmpty()) {
	        myOnedayList = onedayService.searchMyOnedayList(user, classSearch);
	    } else {
	        myOnedayList = onedayService.getmyOnedayList(user);
	    }

	    model.addAttribute("myOnedayList", myOnedayList);
	    model.addAttribute("classSearch", classSearch); // 검색어를 템플릿에 전달

	    return "board/myclassAdmin";
	}
	
	// 내가 예약한 클래스 리스트 페이지로 이동
	@GetMapping("/board/myclassUser")
	public String showMyclassUserList(
	    @RequestParam(name = "classSearch", required = false, defaultValue = "") String classSearch,
	    Model model,
	    Principal principal
	) {
	    User user = userService.getUser(principal.getName());
	    model.addAttribute("user", user);

	    List<Reservation> reservationList;

	    // 검색어가 있으면 필터링된 리스트를 사용하고, 없으면 전체 리스트를 가져옵니다.
	    if (!classSearch.isEmpty()) {
	        reservationList = reservationService.searchReservationList(user, classSearch);
	    } else {
	        reservationList = reservationService.getReservationList(user);
	    }

	    model.addAttribute("reservationList", reservationList);
	    model.addAttribute("classSearch", classSearch); // 검색어를 템플릿에 전달

	    return "board/myclassUser";
	}

	@GetMapping("/board/update")
	@PreAuthorize("isAuthenticated()")
	public String showUpdateForm(@RequestParam(value = "onedayId") String onedayId, Model model, Principal principal) {
		User user = userService.getUser(principal.getName());
		model.addAttribute("user", user);
	    Oneday oneday = onedayService.getOneday(Integer.parseInt(onedayId));
	    List<OnedayCategory> categoryList = onedayService.getCategoryList();
	    model.addAttribute("oneday", oneday);
	    model.addAttribute("categoryList", categoryList);
	    return "board/update";
	}

	@PostMapping("/board/update")
	@PreAuthorize("isAuthenticated()")
	public String updateOnedayClass(@RequestParam(value = "onedayId") String onedayId,
	                                @RequestParam(value = "name") String name,
	                                @RequestParam(value = "sample6_address") String location,
									@RequestParam(value = "sample6_detailAddress", required = false) String detailLocation,
	                                @RequestParam(value = "categoryCode", required = false) String categoryCode,
	                                @RequestParam(value = "content") String content,
	                                @RequestParam(value = "curri") String curri,
	                                @RequestParam(value = "availableSlots") String availableSlots,
	                                @RequestParam(value = "newCategoryName", required = false) String newCategoryName,
	                                @RequestParam(value = "file", required = false) MultipartFile file,
	                                @RequestParam(value = "imageName", required = false) String imageName) throws IOException {
	    Oneday oneday = onedayService.getOneday(Integer.parseInt(onedayId));
	    oneday.setName(name);
	    oneday.setLocation(location + " " + detailLocation);
	    oneday.setContent(content);
	    oneday.setCurri(curri);
	    oneday.setAvailableSlots(Integer.parseInt(availableSlots));
	    
	    // 주소를 위도, 경도로 변환하여 업데이트
        LatLng coordinates = GeocodingService.geocodeAddress(location);
        oneday.setLatitude(coordinates.getLatitude());
        oneday.setLongitude(coordinates.getLongitude());
	    
	    OnedayCategory category;
	    if (categoryCode != null) {	// 원래 있는 카테고리를 골랐을 때
		    category = onedayService.getCategory(Integer.parseInt(categoryCode));	// 선택한 카테고리의 코드로 카테고리 뽑아오기
	    } else {	// 카테고리를 직접 추가했을 때 (카테고리 선택 옵션에서 아무것도 선택을 하지 않았을 때. 즉, '카테고리' 옵션을 선택했을 떄)
	    	onedayService.addCategory(newCategoryName);				// 카테고리 이름으로 카테고리 추가
	    	category = onedayService.getCategory(newCategoryName);	// 추가한 카테고리 이름으로 카테고리 뽑아오기
	    }
	    oneday.setCategory(category);
	    
	    if (imageName != null) {
	    	imageService.saveOnedayWithImage(oneday, file, imageName);	// 이미지 Oneday에 저장
	    }
	    
	    onedayService.updateOneday(oneday);
	    return "redirect:/board/myclassAdmin";
	}

	@PostMapping("/board/remove")
	public String deleteQuestion(@RequestParam(value = "onedayId") String onedayId) {
	    onedayService.removeOneday(Integer.parseInt(onedayId));
	    return "redirect:/board/myclassAdmin";
	}

	//해당 원데이 클래스 상세 정보 페이지
	@GetMapping("/board/detail/{onedayId}")
	public String onedayDetail(@PathVariable int onedayId, Model model, Principal principal) {
		// 조회수 증가 처리
	    onedayService.increaseViewCount(onedayId);
		
		// 사용자 정보를 Principal에서 가져오도록 변경
	    User user = userService.getUser(principal.getName());
	    model.addAttribute("user", user);

	    model.addAttribute("onedayId", onedayId);

	    Oneday oneday = onedayService.getOneday(onedayId);
	    model.addAttribute("oneday", oneday);

	    List<Review> reviewList = reviewService.getReviewListOrderByDateDesc(oneday);
	    model.addAttribute("reviewList", reviewList);
	    
	    boolean reviewable = reviewService.isReviewable(oneday, user);
	    model.addAttribute("reviewable", reviewable);

	    return "board/detail";
	}
	
	@GetMapping("/getFullyBookedDates/{onedayId}")
	@ResponseBody
	public List<LocalDate> getFullyBookedDates(@PathVariable int onedayId, Model model, Principal principal) {
	    // 사용자 정보를 Principal에서 가져오도록 변경
	    User user = userService.getUser(principal.getName());
	    model.addAttribute("user", user);

	    return onedayService.getFullyBookedDates(onedayId);
	}

		
	
	//리뷰 등록
	@PostMapping("/board/detail/{onedayId}")
	@PreAuthorize("isAuthenticated()")
	public String writeReview(@RequestParam(value = "comment") String comment,
	                          @RequestParam(value = "rate") String rate,
	                          @RequestParam(value = "file", required = false) MultipartFile file, 
	                          @RequestParam(value = "imageName", required = false) String imageName,
	                          @PathVariable int onedayId, Principal principal) throws IOException {
	    Review review = new Review();
	    review.setComment(comment);
	    review.setRate(Double.parseDouble(rate));
	    review.setOneday(onedayService.getOneday(onedayId));
	    review.setUser(userService.getUser(principal.getName()));
	    if (imageName != null) {
	    	imageService.saveReviewWithImage(review, file, imageName);	// 이미지 Review에 저장
	    }
	    reviewService.addReview(review);
	    onedayService.calculateAverageRate(onedayId);
	    return "redirect:/board/detail/{onedayId}";
	}
	
	//예약하기
	@GetMapping("/reservation")
    public String showReservationPage(@RequestParam("date") String date, Model model) {
        // 전달받은 날짜를 모델에 추가
        model.addAttribute("selectedDate", date);
        return "reservation/reservation"; // reservation.html 템플릿으로 이동
    }
	
	//예약하기 버튼 누르면 실행
	@PostMapping("/performReservation")
	public ResponseEntity<String> performReservation(@RequestParam int onedayId, @RequestParam int count,
	                                                 @RequestParam String selectedDate, Model model, Principal principal) throws NumberParseException {
       // 인증된 사용자 정보 가져오기
       User user = userService.getUser(principal.getName());

       // 선택한 수업(Oneday) 객체 가져오기
       Oneday oneday = onedayService.getOneday(onedayId);

       // Oneday 객체의 최대 참가자 수 및 현재 예약 인원 수 확인
       if (oneday != null && oneday.getAvailableSlots() >= count) {
           int totalReservedCount = reservationService.getTotalReservedCountForDate(oneday, selectedDate);

           // 최대 참가자 수와 현재까지 예약된 총 인원 수 비교
           if (totalReservedCount + count <= oneday.getAvailableSlots()) {
            	// 새로운 예약 객체 생성
               Reservation reservation = new Reservation();
               reservation.setUser(user);
               reservation.setOneday(oneday);
               reservation.setCount(count);

               // 예약 저장 및 Oneday 객체 업데이트
               LocalDateTime selectedDateTime = LocalDateTime.parse(selectedDate, DateTimeFormatter.ISO_DATE_TIME);
               reservation.setDate(selectedDateTime);

               // 예약 저장 및 Oneday 객체 업데이트
               reservationService.saveReservation(reservation);
               onedayService.updateOneday(oneday);

               // 모델에 추가적인 로직이나 메시지를 넣을 수 있습니다.
               model.addAttribute("reservationResult", "예약이 완료되었습니다.\n" + reservation.getUser().getEmail() + "로 예약 확인 메일이 전송되었습니다.");              
               
               // 예약 완료 문자 메시지를 보내기 위해 호출 => 이후 수정할 예정
               //reservationService.completeReservationAndSendConfirmation(reservation);
               
               // 예약 완료되면 이메일을 보내기 위해 호출
               reservationService.completeReservation(reservation);

               return ResponseEntity.ok("예약이 완료되었습니다.\n" + reservation.getUser().getEmail() + "로 예약 확인 메일이 전송되었습니다.");
           } else {
        	// 실패 시
               model.addAttribute("reservationResult", "예약이 실패했습니다. 인원이 다 찼습니다.");
               return ResponseEntity.badRequest().body("예약이 실패했습니다. 인원이 다 찼습니다.");
           }
    
       } else {
    	// 실패 시
           model.addAttribute("reservationResult", "선택한 인원 수 보다 적은 인원수만 가능합니다.");
           return ResponseEntity.badRequest().body("선택한 인원 수 보다 적은 인원수만 가능합니다.");
       }
   }
 
	//사용자의 위시리스트 출력
   @GetMapping("/wishlist/list")
   public String getWishlist(Model model, Principal principal) {
       // 인증된 사용자 정보 가져오기
       User user = userService.getUser(principal.getName());
       model.addAttribute("user", user);

       // WishlistService를 사용하여 해당 사용자의 위시 리스트를 가져옵니다.
       List<WishList> wishList = wishlistService.getWishListByUser(user.getId());

       // 위시 리스트에 속한 Oneday 엔티티를 추출합니다.
       List<Oneday> onedayList = wishList.stream()
               .map(WishList::getOneday)
               .collect(Collectors.toList());

       // 위시 리스트에 속한 Oneday 엔티티의 위시 여부를 체크하여 프론트엔드에 전달합니다.
       List<Boolean> wishedStatusList = wishList.stream()
               .map(wish -> wishlistService.isOnWishlist(user, wish.getOneday()))
               .collect(Collectors.toList());

       // 모든 wishedStatus가 false인 경우에만 메시지 설정
       boolean allWishedStatusFalse = wishedStatusList.stream().allMatch(status -> !status);
       model.addAttribute("allWishedStatusFalse", allWishedStatusFalse);

       // 프론트엔드에 위시 리스트 및 위시 여부를 전달합니다.
       model.addAttribute("onedayList", onedayList);
       model.addAttribute("wishedStatusList", wishedStatusList);

       return "user/wishlist";
   }

   //위시리스트에 추가 삭제
   @PostMapping("/wishlist/update")
   public ResponseEntity<String> updateWishListStatus(
       @RequestBody Map<String, Object> requestData,
       Principal principal
   ) {
       try {
    	   int onedayId = Integer.parseInt(requestData.get("onedayId").toString());
           boolean isWishlist = (boolean) requestData.get("isWishlist");

           User user = userService.getUser(principal.getName());
           int userId = user.getId();
           
           wishlistService.updateWishListStatus(onedayId, userId, isWishlist);
           
           return ResponseEntity.ok("Wishlist updated successfully");
       } catch (Exception e) {
           // 예외 메시지를 클라이언트로 전송
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
       }
   }
   
   // 나의 취향 정보 페이지 접속
   @GetMapping("/board/myPreferenceInfo")
   public String showMyPreferenceInfoList(Principal principal, Model model) {
	   User user = userService.getUser(principal.getName());
	   model.addAttribute("user", user);
	   
	   // 특정 사용자가 가장 많이 예약한 카테고리 뽑아오기
	   OnedayCategory mostReservedCategory = onedayCategoryService.findMostReservedCategoryByUser(user);
	   model.addAttribute("mostReservedCategory", mostReservedCategory);
	   
	   // 특정 사용자가 가장 많이 예약한 카테고리에 속한 원데이 리스트를 모두 뽑아오기
	   List<Oneday> recommendationOnedayList = null;
	   if (mostReservedCategory != null) {
		   recommendationOnedayList = onedayService.getOnedayList(mostReservedCategory.getId());
		   model.addAttribute("recommendationOnedayList", recommendationOnedayList);
	   }
	   
	   // 사용자가 찜한 Oneday들의 위시리스트 상태 가져오기
	    List<Boolean> wishedStatusList = new ArrayList<>();
	    if (recommendationOnedayList != null) {
		    for (Oneday oneday : recommendationOnedayList) {
		        boolean isWished = wishlistService.isOnWishlist(user, oneday);
		        wishedStatusList.add(isWished);
		    }
	    }
	    model.addAttribute("wishedStatusList", wishedStatusList);
	   
	   return "board/myPreferenceInfo";
   }
   
    // 내가 작성한 리뷰 페이지 이동
	@GetMapping("/board/myreview")
	public String showMyReviewList(@RequestParam(value="username") String username, Model model, Principal principal) {
		User user = userService.getUser(username);
		model.addAttribute("user", user);
		List<Review> reviewList = reviewService.getReviewList(user);
		model.addAttribute("reviewList", reviewList);
		return "board/myreview";
	}
}