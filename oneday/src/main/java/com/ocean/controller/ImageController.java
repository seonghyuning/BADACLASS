package com.ocean.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ocean.service.ImageService;

@Controller
public class ImageController {
	@Autowired
    private ImageService imageService;
    
    // Oneday ID를 받아와서 해당하는 이미지를 응답
    @GetMapping("/oneday/image")
    public ResponseEntity<byte[]> getImageFromOneday(@RequestParam int id) {	// id = oneday의 id
        byte[] imageBytes = imageService.getImageByOnedayId(id);

        if (imageBytes != null && imageBytes.length > 0) {
			return ResponseEntity.ok()
			.contentType(MediaType.IMAGE_PNG)  // 기본 확장자를 PNG로 지정
			.body(imageBytes);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Review ID를 받아와서 해당하는 이미지를 응답
    @GetMapping("/review/image")
    public ResponseEntity<byte[]> getImageFromReview(@RequestParam int id) {	// id = review의 id
        byte[] imageBytes = imageService.getImageByReviewId(id);

        if (imageBytes != null && imageBytes.length > 0) {
			return ResponseEntity.ok()
			.contentType(MediaType.IMAGE_PNG)  // 기본 확장자를 PNG로 지정
			.body(imageBytes);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // User ID를 받아와서 해당하는 이미지를 응답
    @GetMapping("/user/image")
    public ResponseEntity<byte[]> getImageFromUser(@RequestParam int id) {	// id = review의 id
        byte[] imageBytes = imageService.getImageByUserId(id);

        if (imageBytes != null && imageBytes.length > 0) {
			return ResponseEntity.ok()
			.contentType(MediaType.IMAGE_PNG)  // 기본 확장자를 PNG로 지정
			.body(imageBytes);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
