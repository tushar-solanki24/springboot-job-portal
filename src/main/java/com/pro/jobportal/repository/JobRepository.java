package com.pro.jobportal.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pro.jobportal.entity.Job;

public interface JobRepository extends JpaRepository<Job, Long> {

	List<Job> findByJobTitleContainingIgnoreCaseOrLocationContainingIgnoreCase(String title, String location);

	List<Job> findByJobTitleContainingIgnoreCase(String title);

	List<Job> findByLocationContainingIgnoreCase(String location);

	List<Job> findByJobTitleContainingIgnoreCaseAndLocationContainingIgnoreCase(String title, String location);

	List<Job> findTop5ByJobTitleContainingIgnoreCase(String keyword);

	List<Job> findByRecruiterId(Long recruiterId);

	List<Job> findByJobTitleContainingIgnoreCaseAndExpiryDateAfter(String title, LocalDate date);

	List<Job> findByLocationContainingIgnoreCaseAndExpiryDateAfter(String location, LocalDate date);

	List<Job> findByExpiryDateAfter(LocalDate date);

	List<Job> findByExpiryDateBefore(LocalDate date);

	Page<Job> findByJobTitleContainingIgnoreCase(String jobTitle, Pageable pageable);

	Page<Job> findByLocationContainingIgnoreCase(String location, Pageable pageable);

}
