package com.pro.jobportal.config;

import java.security.Principal;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.pro.jobportal.entity.JobSeeker;
import com.pro.jobportal.service.JobSeekerService;

@ControllerAdvice
public class CurrentUserModelAdvice {

    private final JobSeekerService jobSeekerService;

    public CurrentUserModelAdvice(JobSeekerService jobSeekerService) {
        this.jobSeekerService = jobSeekerService;
    }

    @ModelAttribute("currentUser")
    public JobSeeker addCurrentUser(Principal principal) {
        if (principal == null) {
            return null;
        }
        return jobSeekerService.findByEmail(principal.getName()).orElse(null);
    }
}
