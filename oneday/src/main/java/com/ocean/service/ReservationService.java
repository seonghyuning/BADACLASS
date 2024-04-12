package com.ocean.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber;
import com.ocean.model.entity.Oneday;
import com.ocean.model.entity.Reservation;
import com.ocean.model.entity.User;
import com.ocean.repository.ReservationRepository;

@Service
public class ReservationService {
	@Autowired
	private ReservationRepository reservationRepository;
	
	@Autowired
    private TwilioService twilioService;
	
	private final EmailService emailService;
	
	@Autowired
    public ReservationService(EmailService emailService) {
        this.emailService = emailService;
    }
	
	// 예약 정보를 저장하는 메서드
    public Reservation saveReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }
	
    // Oneday로 Reservation의 List 뽑아오기
	public List<Reservation> getReservationList(Oneday oneday) throws DataAccessException {
		// repository에서 내가 설정한 메서드로 Reservation의 List 뽑아오기
		List<Reservation> reservationList = reservationRepository.findByOneday(oneday, Sort.by(Sort.Direction.DESC, "id"));	
		return reservationList;
		
	}
	
	// User로 Reservation의 List 뽑아오기
	public List<Reservation> getReservationList(User user) throws DataAccessException {
		// repository에서 내가 설정한 메서드(user를 매개변수로 설정한 메서드)로 Reservation의 List 뽑아오기
		List<Reservation> reservationList = null;
		reservationList = reservationRepository.findByUser(user, Sort.by(Sort.Direction.DESC, "id"));	
		return reservationList;
	}
	
	public Reservation getReservationById(int reservationId) {
	    // findById를 통해 Optional로 감싸진 Reservation 객체를 얻습니다.
	    Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);

	    // 만약 Reservation 객체가 존재한다면 반환하고, 없다면 예외를 던집니다.
	    return optionalReservation.orElseThrow(() -> new IllegalArgumentException("해당 ID의 예약을 찾을 수 없습니다: " + reservationId));
	}
	
	public void deleteReservation(int id) throws DataAccessException {
		Optional<Reservation> optionalReservation = reservationRepository.findById(id);
		Reservation reservation = null;
		if (optionalReservation.isPresent()) {
			reservation = optionalReservation.get();
			reservationRepository.delete(reservation);
		}
	}
	
	public int getTotalReservedCountForDate(Oneday oneday, String selectedDate) {
        // 해당 날짜에 예약된 총 인원 수를 가져오는 로직
        LocalDateTime selectedDateTime = LocalDateTime.parse(selectedDate, DateTimeFormatter.ISO_DATE_TIME);
        List<Reservation> reservationsForDate = reservationRepository.findByOnedayAndDate(oneday, selectedDateTime);

        return reservationsForDate.stream().mapToInt(Reservation::getCount).sum();
    }
	
	// 예약 완료되면 실행되는 메서드 (문자 메시지를 보내기 위해 호출)
	public void completeReservationAndSendConfirmation(Reservation reservation) throws NumberParseException {
        // 예약 완료 및 기타 로직 수행
		PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
		Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(reservation.getUser().getPhoneNm(), "KR");

		// E.164 형식으로 변환
		String e164PhoneNumber = phoneNumberUtil.format(phoneNumber, PhoneNumberFormat.E164);

        // 사용자에게 예약 확인 문자 메시지 전송 (Twilio에 전달)
        sendConfirmationMessage(reservation, e164PhoneNumber);
    }

	// 사용자에게 예약 확인 문자 메시지 전송하는 메서드
    private void sendConfirmationMessage(Reservation reservation, String e164PhoneNumber) {
        // 예약 정보를 문자 메시지로 변환 (예: 날짜, 장소 등)
        String reservationDetails = buildReservationDetails(reservation);

        // 사용자에게 예약 확인 문자 메시지 전송
        //String userPhoneNumber = reservation.getUser().getPhoneNm();
        String userPhoneNumber = e164PhoneNumber;
        twilioService.sendReservationConfirmation(userPhoneNumber, reservationDetails);
    }

    // 예약 정보를 문자열로 빌드하는 로직을 구현하는 메서드. 이 문자열이 문자로 전송됨
    private String buildReservationDetails(Reservation reservation) {
        // 예약 정보를 문자열로 빌드하는 로직을 구현
        // 예: "Date: 2023-12-25, Location: Some Location"
        //return "Date: " + reservation.getDate() + ", Location: " + reservation.getOneday().getLocation();
    	return "[바다 클래스] \'" + reservation.getOneday().getName() + "\' 예약이 완료되었습니다.\n" + "신청날짜: " + reservation.getDate() + "\n위치: " + reservation.getOneday().getLocation() + "\n예약자: " + reservation.getUser().getNickname() + "\n인원수: " + reservation.getCount() + "명";
    }
    
    // 예약 완료되면 이메일을 어떻게 보낼지 정하는 메서드
    public void completeReservation(Reservation reservation) {	
    	LocalDateTime reservationDate = reservation.getDate();

    	// DateTimeFormatter를 사용하여 포맷을 지정
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");

    	// LocalDateTime을 원하는 포맷으로 문자열로 변환
    	String formattedDate = reservationDate.format(formatter);

        // 이메일 보내기
        sendReservationConfirmationEmail(reservation.getUser().getEmail(), 
        		"[바다 클래스] \'" + reservation.getOneday().getName() + "\' 예약이 완료되었습니다.", 
        		"신청날짜: " + formattedDate + "\n위치: " + reservation.getOneday().getLocation() + "\n예약자: " + reservation.getUser().getNickname() + "\n인원수: " + reservation.getCount() + "명");
    }

    // 정해진 이메일을 받아서 이메일을 보내주는 메서드
    private void sendReservationConfirmationEmail(String to, String subject, String message) {
        emailService.sendReservationConfirmation(to, subject, message);		// (수신자 이메일, 제목, 내용)
    }
    
    //내가 예약한 내역 검색 기능
    public List<Reservation> searchReservationList(User user, String classSearch) {
        List<Reservation> reservationList = getReservationList(user);

        // Filtering reservations based on the search term (class name)
        String searchLower = classSearch.toLowerCase();
        return reservationList.stream()
                .filter(reservation -> reservation.getOneday().getName().toLowerCase().contains(searchLower))
                .collect(Collectors.toList());
    }
}
