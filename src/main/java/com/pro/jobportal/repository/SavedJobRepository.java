package com.pro.jobportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pro.jobportal.entity.SavedJob;

public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {

    List<SavedJob> findByUserId(Long userId);

    boolean existsByUserIdAndJob_JobId(Long userId, Long jobId);

    void deleteByUserIdAndJob_JobId(Long userId, Long jobId);
}