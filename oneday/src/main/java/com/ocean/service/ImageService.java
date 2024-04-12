package com.ocean.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ocean.model.entity.Image;
import com.ocean.model.entity.Oneday;
import com.ocean.model.entity.Review;
import com.ocean.model.entity.User;
import com.ocean.repository.ImageRepository;
import com.ocean.repository.OnedayRepository;
import com.ocean.repository.ReviewRepository;
import com.ocean.repository.UserRepository;

@Service
public class ImageService {
	@Autowired
    private ImageRepository imageRepository;
	
	@Autowired
	private OnedayRepository onedayRepository;
	
	@Autowired
	private ReviewRepository reviewRepository;
	
	@Autowired
	private UserRepository userRepository;
	
    // Oneday ID를 받아와서 해당하는 Oneday의 이미지를 가져오는 메서드
    public byte[] getImageByOnedayId(int onedayId) {
        // Oneday 엔터티에서 Image 가져오기
        Oneday oneday = onedayRepository.findById(onedayId).orElse(null);

        // 가져온 Image에서 데이터 추출
        if (oneday != null && oneday.getImage() != null) {
            return oneday.getImage().getCompressedImageData();	// 압축된 이미지 데이터를 가져오기
        }

        return new byte[0]; // 빈 배열 또는 예외 처리 로직 추가
    }
    
    // 이미지를 Oneday에 저장하는 메서드
    public void saveOnedayWithImage(Oneday oneday, MultipartFile imageFile, String imageName) throws IOException {
    	// 이미지 압축
        String compressedImageBase64 = compressAndEncodeImage(imageFile.getBytes(), imageName);
        // 압축된 이미지 저장 후 이미지 생성
        Image image = saveCompressedImage(compressedImageBase64);
        
        image.setImageName(imageName);

        // Oneday에 Image 설정
        oneday.setImage(image);

        // Oneday를 저장
        onedayRepository.save(oneday);
    }
    
    // Review ID를 받아와서 해당하는 Review의 이미지를 가져오는 메서드
    public byte[] getImageByReviewId(int reviewId) {
        // Review 엔터티에서 Image 가져오기
    	Review review = reviewRepository.findById(reviewId).orElse(null);

        // 가져온 Image에서 데이터 추출
        if (review != null && review.getImage() != null) {
            return review.getImage().getCompressedImageData();	// 압축된 이미지 데이터를 가져오기
        }

        return new byte[0]; // 빈 배열 또는 예외 처리 로직 추가
    }
    
    // 이미지를 Review에 저장하는 메서드
    public void saveReviewWithImage(Review review, MultipartFile imageFile, String imageName) throws IOException {
    	// 이미지 압축
        String compressedImageBase64 = compressAndEncodeImage(imageFile.getBytes(), imageName);
        // 압축된 이미지 저장 후 이미지 생성
        Image image = saveCompressedImage(compressedImageBase64);
        
        image.setImageName(imageName);

        // Review에 Image 설정
        review.setImage(image);

        // Review를 저장
        reviewRepository.save(review);
    }
    
    // User ID를 받아와서 해당하는 User의 이미지를 가져오는 메서드
    public byte[] getImageByUserId(int userId) {
        // User 엔터티에서 Image 가져오기
        User user = userRepository.findById(userId).orElse(null);

        // 가져온 Image에서 데이터 추출
        if (user != null && user.getImage() != null) {
            return user.getImage().getCompressedImageData();	// 압축된 이미지 데이터를 가져오기
        }

        return new byte[0]; // 빈 배열 또는 예외 처리 로직 추가
    }
    
    // 이미지를 User에 저장하는 메서드
    public void saveUserWithImage(User user, MultipartFile imageFile, String imageName) throws IOException {
    	// 이미지 압축
        String compressedImageBase64 = compressAndEncodeImage(imageFile.getBytes(), imageName);
        // 압축된 이미지 저장 후 이미지 생성
        Image image = saveCompressedImage(compressedImageBase64);
        
        image.setImageName(imageName);

        // User에 Image 설정
        user.setImage(image);

        // User를 저장
        userRepository.save(user);
    }
    
    // 이미지 압축, Base64로 인코딩 메서드
    public String compressAndEncodeImage(byte[] imageBytes, String imageName) throws IOException {    	
    	// 이미지를 BufferedImage로 읽어옴
        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
        BufferedImage image = ImageIO.read(bis);
        bis.close();

        // 이미지를 압축
        BufferedImage compressedImage = compressImage(image);

        // 압축된 이미지를 byte 배열로 변환
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        // 이미지의 형식을 추출 (확장자가 없는 경우 기본적으로 "png"로 가정)
        String imageFormat = "png";

        // 동적으로 결정된 형식으로 이미지를 인코딩
        ImageIO.write(compressedImage, imageFormat, bos);
        bos.close();
        byte[] compressedBytes = bos.toByteArray();

        // 이미지를 Base64로 인코딩
        return Base64.getEncoder().encodeToString(compressedBytes);
    }
    
    // 압축된 이미지 저장 메서드
    public Image saveCompressedImage(String compressedImageBase64) {
    	// Base64 문자열을 디코딩하여 byte[]로 변환
        byte[] compressedImageData = Base64.getDecoder().decode(compressedImageBase64);
        
        // 압축된 이미지를 데이터베이스에 저장
        Image image = new Image();
        image.setCompressedImageData(compressedImageData);
        imageRepository.save(image);
        return image;
    }
    
    public static BufferedImage compressImage(BufferedImage image) {
        // 이미지 압축 로직: 이미지 리사이징
        int newWidth = image.getWidth() / 2;
        int newHeight = image.getHeight() / 2;
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        resizedImage.getGraphics().drawImage(image, 0, 0, newWidth, newHeight, null);

        return resizedImage;
    }
    
//    // 이미지 형식을 동적으로 결정하는 메서드
//    public MediaType determineContentType(String format) {
//        // 클라이언트가 요청한 형식이 지정되지 않은 경우 또는 지원하지 않는 형식인 경우 기본값은 PNG로 설정
//        if (format == null || !Arrays.asList(ImageIO.getWriterFormatNames()).contains(format)) {
//            format = "png";
//        }
//
//        // 지정된 형식에 따라 MediaType 결정
//        switch (format.toLowerCase()) {
//            case "jpg":
//            case "jpeg":
//                return MediaType.IMAGE_JPEG;
//            case "png":
//                return MediaType.IMAGE_PNG;
//            // 추가적인 이미지 형식에 대한 처리를 원하면 여기에 계속해서 추가할 수 있습니다.
//            default:
//                // 알 수 없는 형식인 경우 기본값으로 PNG 사용
//                return MediaType.IMAGE_PNG;
//        }
//    }
    
//    // 이미지 객체 자체를 얻어오는 메서드
//    public Image getImageEntityById(int id) {
//    	Optional<Image> optionalImage = imageRepository.findById(id);
//    	Image image = null;
//    	if (optionalImage.isPresent()) {
//    		image = optionalImage.get();
//    	}
//    	return image;
//    }
}
