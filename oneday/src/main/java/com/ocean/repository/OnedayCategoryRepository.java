package com.ocean.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ocean.model.entity.OnedayCategory;

public interface OnedayCategoryRepository extends JpaRepository<OnedayCategory, Integer> {
	Optional<OnedayCategory> findByName(String name);
	
	List<OnedayCategory> findAll();
}
