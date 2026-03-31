package com.pro.jobportal.config;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.pro.jobportal.entity.JobSeeker;
import com.pro.jobportal.repository.JobSeekerRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final JobSeekerRepository jobSeekerRepository;

    public CustomUserDetailsService(JobSeekerRepository jobSeekerRepository) {
        this.jobSeekerRepository = jobSeekerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        JobSeeker user = jobSeekerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
                .build();
    }
}
