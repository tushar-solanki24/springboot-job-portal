package com.pro.jobportal.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.pro.jobportal.entity.Job;
import com.pro.jobportal.repository.JobRepository;

@Service
public class JobSchedulerService {

    @Autowired
    private JobRepository jobRepository;

    @Scheduled(cron = "0 0 0 * * ?")  // runs every midnight
    public void checkExpiredJobs(){

        List<Job> expiredJobs =
                jobRepository.findByExpiryDateBefore(LocalDate.now());

        for(Job job : expiredJobs){
            System.out.println("Expired job removed: " + job.getJobTitle());
        }

    }

}