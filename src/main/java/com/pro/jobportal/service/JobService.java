package com.pro.jobportal.service;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pro.jobportal.entity.Job;
import com.pro.jobportal.repository.JobRepository;

@Service
public class JobService {
	@Autowired
	private JobRepository jobRepository;

	public void saveJob(Job job) {
		jobRepository.save(job);
	}

	public List<Job> getAllJobs() {
		return jobRepository.findAll();
	}

	public List<Job> searchJobs(String keyword) {
		return jobRepository.findByJobTitleContainingIgnoreCaseOrLocationContainingIgnoreCase(keyword, keyword);
	}

	public Job getJobById(Long id) {
		return jobRepository.findById(id).orElse(null);
	}

	public List<Job> searchJobs(String title, String location){

	    List<Job> jobs = new ArrayList<>();

	    if(title != null && !title.isEmpty()){
	        jobs = jobRepository
	                .findByJobTitleContainingIgnoreCaseAndExpiryDateAfter(
	                        title, LocalDate.now());
	    }

	    if(jobs.isEmpty() && location != null && !location.isEmpty()){
	        jobs = jobRepository
	                .findByLocationContainingIgnoreCaseAndExpiryDateAfter(
	                        location, LocalDate.now());
	    }

	    if(jobs.isEmpty()){
	        jobs = jobRepository
	                .findByExpiryDateAfter(LocalDate.now());
	    }

	    return jobs;
	}

	public List<Job> getJobSuggestions(String keyword) {
		return jobRepository.findTop5ByJobTitleContainingIgnoreCase(keyword);
	}

	public List<Job> getJobsByRecruiter(Long recruiterId) {
		return jobRepository.findByRecruiterId(recruiterId);
	}
	
	public List<Job> getActiveJobs(){
	    return jobRepository.findByExpiryDateAfter(LocalDate.now());
	}
	
	public Page<Job> searchJobs(String title, String location, int page) {

	    Pageable pageable = PageRequest.of(page, 10);

	    if (title != null && !title.isEmpty()) {
	        return jobRepository.findByJobTitleContainingIgnoreCase(title, pageable);
	    }

	    if (location != null && !location.isEmpty()) {
	        return jobRepository.findByLocationContainingIgnoreCase(location, pageable);
	    }

	    return jobRepository.findAll(pageable);
	}
}
