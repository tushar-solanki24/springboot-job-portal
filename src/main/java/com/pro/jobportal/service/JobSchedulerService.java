package com.pro.jobportal.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pro.jobportal.entity.Job;
import com.pro.jobportal.repository.JobApplicationRepository;
import com.pro.jobportal.repository.JobRepository;
import com.pro.jobportal.repository.SavedJobRepository;

@Service
public class JobSchedulerService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private SavedJobRepository savedJobRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")  // runs every midnight
    public void checkExpiredJobs(){

        List<Job> expiredJobs =
                jobRepository.findByExpiryDateBefore(LocalDate.now());

        for(Job job : expiredJobs){
            savedJobRepository.deleteByJob_JobId(job.getJobId());
            jobApplicationRepository.deleteByJob_JobId(job.getJobId());
            jobRepository.delete(job);
        }

    }

}