package com.ocean.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ocean.model.entity.Oneday;
import com.ocean.model.entity.Reservation;
import com.ocean.model.entity.User;
import com.ocean.service.OnedayService;
import com.ocean.service.ReservationService;
import com.ocean.service.UserService;

@Controller
@RequestMapping("/reservation")
public class ReservationController {
	@Autowired
	private ReservationService reservationService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private OnedayService onedayService;
	
	// 예약 관리(회원 관리) 폼으로 이동 (위치: 나의 오픈 클래스 -> 관리하기)
	@GetMapping("/register")
	public String showReservationRegisterForm(@RequestParam(value="onedayId") String onedayId, Model model, Principal principal) {
		// 사용자 정보를 Principal에서 가져옴
	    User user = userService.getUser(principal.getName());
	    model.addAttribute("user", user);
		Oneday oneday = onedayService.getOneday(Integer.parseInt(onedayId));
		List<Reservation> reservationList = reservationService.getReservationList(oneday);
		model.addAttribute("oneday", oneday);
		model.addAttribute("reservationList", reservationList);
		return "reservation/register";
	}
	
	// 예약 관리(회원 관리) 기능 처리 = 예약 취소
	// 관리자가 취소할 때
	@PostMapping("/delete")
	public String reservationRegister(@RequestParam(value="onedayId") String onedayId,
									  @RequestParam(value="reservationId") String reservationId) {
		reservationService.deleteReservation(Integer.parseInt(reservationId));
		// 다시 예약 관리 폼으로 이동하기. Get으로 이동해야 하므로 주소의 parameter에 onedayId 추가 해야 함
		return "redirect:/reservation/register?onedayId=" + onedayId;	
	}
	
	// 예약 관리(회원 관리) 기능 처리 = 예약 취소
	// 회원이 취소할 때
	@PostMapping("/deleteFromUser")
	public String reservationDelete(@RequestParam(value="onedayId") String onedayId,
									@RequestParam(value="reservationId") String reservationId) {
		reservationService.deleteReservation(Integer.parseInt(reservationId));
		// 다시 예약 관리 폼으로 이동하기. Get으로 이동해야 하므로 주소의 parameter에 onedayId 추가 해야 함
		return "redirect:/board/myclassUser";	
	}
}
