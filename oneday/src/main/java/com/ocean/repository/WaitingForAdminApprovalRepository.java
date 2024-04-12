package com.ocean.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ocean.model.entity.User;
import com.ocean.model.entity.WaitingForAdminApproval;

public interface WaitingForAdminApprovalRepository extends JpaRepository<WaitingForAdminApproval, Integer> {
	Optional<WaitingForAdminApproval> findByUser(User user);
}
