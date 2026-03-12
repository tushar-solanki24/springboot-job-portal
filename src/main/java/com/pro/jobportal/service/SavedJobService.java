package com.pro.jobportal.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pro.jobportal.entity.Job;
import com.pro.jobportal.entity.SavedJob;
import com.pro.jobportal.repository.JobRepository;
import com.pro.jobportal.repository.SavedJobRepository;

@Service
public class SavedJobService {

    @Autowired
    private SavedJobRepository repository;

    @Autowired
    private JobRepository jobRepository;

    public void saveJob(Long userId, Long jobId){

        if(repository.existsByUserIdAndJob_JobId(userId, jobId)){
            return;
        }

        Job job = jobRepository.findById(jobId).orElse(null);

        SavedJob savedJob = new SavedJob();

        savedJob.setUserId(userId);
        savedJob.setJob(job);

        repository.save(savedJob);
    }

    public List<SavedJob> getSavedJobs(Long userId){
        return repository.findByUserId(userId);
    }
    
    @Transactional
    public void removeSavedJob(Long userId, Long jobId){
        repository.deleteByUserIdAndJob_JobId(userId, jobId);
    }

}