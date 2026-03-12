package com.pro.jobportal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pro.jobportal.entity.JobSeeker;

public interface JobSeekerRepository extends JpaRepository<JobSeeker, Long>{
	Optional<JobSeeker> findByEmail(String email);
	boolean existsByMobile(String mobile);
}
