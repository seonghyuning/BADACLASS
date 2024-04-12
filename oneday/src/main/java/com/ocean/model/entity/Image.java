package com.ocean.model.entity;

import org.springframework.stereotype.Component;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Component
public class Image {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(nullable = false)
	private int id;
	
    private String imageName;

    // 원본 이미지 데이터
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] imageData;

    // Base64로 압축된 이미지 데이터
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] compressedImageData;
}
