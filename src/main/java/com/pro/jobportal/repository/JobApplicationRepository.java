package com.pro.jobportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pro.jobportal.entity.JobApplication;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    boolean existsByUserIdAndJob_JobId(Long userId, Long jobId);

    List<JobApplication> findByUserId(Long userId);
    
    List<JobApplication> findByJob_JobId(Long jobId);
    
    long countByJob_JobId(Long jobId);
}