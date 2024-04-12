package com.ocean.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ocean.model.entity.Oneday;
import com.ocean.model.entity.Review;
import com.ocean.model.entity.User;
import com.ocean.service.ImageService;
import com.ocean.service.OnedayService;
import com.ocean.service.ReviewService;
import com.ocean.service.UserService;

@Controller
@RequestMapping("/review")
public class ReviewController {
	@Autowired
	private ReviewService reviewService;
	
	@Autowired
	private OnedayService onedayService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ImageService imageService;
	
	// 리뷰 수정 폼으로 이동
	@GetMapping("/update")
	public String showUpdateReviewForm(@RequestParam int reviewId, Model model, Principal principal) {
		// 사용자 정보를 Principal에서 가져오도록 변경
	    User user = userService.getUser(principal.getName());
	    model.addAttribute("user", user);
	    
	    model.addAttribute("reviewId", reviewId);
	    
	    Review review = reviewService.getReview(reviewId);
	   
	    int onedayId = review.getOneday().getId();
	    model.addAttribute("onedayId", onedayId);
	    
	    Oneday oneday = onedayService.getOneday(onedayId);
	    model.addAttribute("oneday", oneday);

	    List<Review> reviewList = reviewService.getReviewListOrderByDateDesc(oneday);
	    model.addAttribute("reviewList", reviewList);
	    
	    boolean reviewable = reviewService.isReviewable(oneday, user);
	    model.addAttribute("reviewable", reviewable);
	     
		return "board/updateReview";
	}
	
	// 리뷰 수정 처리
	@PostMapping("/update")
	public String updateReview(@RequestParam(value="reviewId") String reviewId,
							   @RequestParam(value="updateComment") String comment,
							   @RequestParam(value="updateRate") String rate,
							   @RequestParam(value = "file", required = false) MultipartFile file, 
		                       @RequestParam(value = "imageName", required = false) String imageName) throws IOException {
		Review review = reviewService.getReview(Integer.parseInt(reviewId));
		int onedayId = review.getOneday().getId();
		review.setComment(comment);
		review.setRate(Double.parseDouble(rate));
		if (imageName != null) {
			imageService.saveReviewWithImage(review, file, imageName);	// 이미지 Review에 저장
		}
		reviewService.updateReview(review);
		onedayService.calculateAverageRate(onedayId);
		return "redirect:/board/detail/" + onedayId;
	}
	
	// 리뷰 삭제 처리
	@GetMapping("/remove")
	public String removeReview(@RequestParam int reviewId) {
		Review review = reviewService.getReview(reviewId);
		int onedayId = review.getOneday().getId();
		reviewService.removeReview(reviewId);
		onedayService.calculateAverageRate(onedayId);
		return "redirect:/board/detail/" + onedayId; 
	}
	
	// 나의 리뷰 보기 폼에서 리뷰 삭제 처리 
	@GetMapping("/removeFromMyreview")
	public String removeReview2(@RequestParam int reviewId, Principal principal) {
		Review review = reviewService.getReview(reviewId);
		int onedayId = review.getOneday().getId();
		reviewService.removeReview(reviewId);
		onedayService.calculateAverageRate(onedayId);
		return "redirect:/board/myreview?username=" + principal.getName(); 
	}
		
	// 리뷰 별점 높은순 보여주기
	@GetMapping("/sort/rateDesc")
	public String rateDesc(@RequestParam int onedayId, Model model, Principal principal) {
		Oneday oneday = onedayService.getOneday(onedayId);
		List<Review> reviewListOrderByRateDesc = reviewService.getReviewListOrderByRateDesc(oneday);
		model.addAttribute("reviewListOrderByRateDesc", reviewListOrderByRateDesc);
		
	    User user = userService.getUser(principal.getName());
	    model.addAttribute("user", user);
	    model.addAttribute("onedayId", onedayId);
	    model.addAttribute("oneday", oneday);
	    
	    boolean reviewable = reviewService.isReviewable(oneday, user);
	    model.addAttribute("reviewable", reviewable);
	    
		return "board/rateDesc";
	}
	
	// 리뷰 별점 낮은순 보여주기
	@GetMapping("/sort/rateAsc")
	public String rateAsc(@RequestParam int onedayId, Model model, Principal principal) {
		Oneday oneday = onedayService.getOneday(onedayId);
		List<Review> reviewListOrderByRateAsc = reviewService.getReviewListOrderByRateAsc(oneday);
		model.addAttribute("reviewListOrderByRateAsc", reviewListOrderByRateAsc);
		
		User user = userService.getUser(principal.getName());
	    model.addAttribute("user", user);
	    model.addAttribute("onedayId", onedayId);
	    model.addAttribute("oneday", oneday);
	    
	    boolean reviewable = reviewService.isReviewable(oneday, user);
	    model.addAttribute("reviewable", reviewable);
	    
		return "board/rateAsc";
	}
	
	// 리뷰 최신순 보여주기
	@GetMapping("/sort/dateDesc")
	public String dateDesc(@RequestParam int onedayId, Model model, Principal principal) {
		Oneday oneday = onedayService.getOneday(onedayId);
		List<Review> reviewListOrderByDateDesc = reviewService.getReviewListOrderByDateDesc(oneday);
		System.out.println("reviewListOrderByDateDesc: " + reviewListOrderByDateDesc); // 디버깅 로그
		model.addAttribute("reviewListOrderByDateDesc", reviewListOrderByDateDesc);
		
		User user = userService.getUser(principal.getName());
	    model.addAttribute("user", user);
	    model.addAttribute("onedayId", onedayId);
	    model.addAttribute("oneday", oneday);
	    
	    boolean reviewable = reviewService.isReviewable(oneday, user);
	    model.addAttribute("reviewable", reviewable);
	    
		return "board/dateDesc";
	}
	
	// 리뷰 오래된순 보여주기
	@GetMapping("/sort/dateAsc")
	public String dateAsc(@RequestParam int onedayId, Model model, Principal principal) {
		Oneday oneday = onedayService.getOneday(onedayId);
		List<Review> reviewListOrderByDateAsc = reviewService.getReviewListOrderByDateAsc(oneday);
		model.addAttribute("reviewListOrderByDateAsc", reviewListOrderByDateAsc);
		
		User user = userService.getUser(principal.getName());
	    model.addAttribute("user", user);
	    model.addAttribute("onedayId", onedayId);
	    model.addAttribute("oneday", oneday);
	    
	    boolean reviewable = reviewService.isReviewable(oneday, user);
	    model.addAttribute("reviewable", reviewable);
	    
		return "board/dateAsc";
	}
}
