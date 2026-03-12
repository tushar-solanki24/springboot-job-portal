package com.pro.jobportal.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pro.jobportal.entity.Job;
import com.pro.jobportal.entity.JobApplication;
import com.pro.jobportal.repository.JobApplicationRepository;
import com.pro.jobportal.repository.JobRepository;

@Service
public class JobApplicationService {

	@Autowired
	private JobApplicationRepository repository;

	@Autowired
	private JobRepository jobRepository;

	public boolean applyJob(Long userId, Long jobId, MultipartFile resume) {

		if (repository.existsByUserIdAndJob_JobId(userId, jobId)) {
			return false;
		}

		Job job = jobRepository.findById(jobId).orElse(null);

		JobApplication application = new JobApplication();

		application.setUserId(userId);
		application.setJob(job);
		application.setAppliedDate(LocalDate.now());
		application.setResumePath(resume.getOriginalFilename());
		application.setStatus("Applied");
		
		repository.save(application);

		return true;
	}

	public List<JobApplication> getApplicationsByUser(Long userId) {
		return repository.findByUserId(userId);
	}
	
	public List<JobApplication> getApplicantsByJob(Long jobId){
		return repository.findByJob_JobId(jobId);
		}
	
	public JobApplication getApplicationById(Long id){
		return repository.findById(id).orElse(null);
		}

		public void save(JobApplication application){
		repository.save(application);
		}
		
		public long getApplicantCount(Long jobId){
		    return repository.countByJob_JobId(jobId);
		}
}