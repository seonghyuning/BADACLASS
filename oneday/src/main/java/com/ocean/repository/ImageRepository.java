package com.ocean.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ocean.model.entity.Image;

public interface ImageRepository extends JpaRepository<Image, Integer> {

}
