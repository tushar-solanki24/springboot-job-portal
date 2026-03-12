package com.pro.jobportal.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pro.jobportal.dto.JobSeekerDTO;
import com.pro.jobportal.entity.JobSeeker;
import com.pro.jobportal.repository.JobSeekerRepository;

@Service
public class JobSeekerService {
	@Autowired
	private JobSeekerRepository repository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	public void registerUser(JobSeekerDTO dto) {
		JobSeeker user = new JobSeeker();

		user.setUsername(dto.getUsername());
		user.setEmail(dto.getEmail());
		user.setPassword(passwordEncoder.encode(dto.getPassword()));
		user.setMobile(dto.getMobile());
		user.setRole(dto.getRole());

		repository.save(user);
	}

	public boolean emailExists(String email) {
		return repository.findByEmail(email).isPresent();
	}

	public boolean mobileExists(String mobile) {
		return repository.existsByMobile(mobile);
	}

	public JobSeeker loginUser(String email, String password) {

	    Optional<JobSeeker> userOptional = repository.findByEmail(email);

	    if (userOptional.isPresent()) {

	        JobSeeker user = userOptional.get();

	        if (passwordEncoder.matches(password, user.getPassword())) {
	            return user;
	        }
	    }

	    return null;
	}

	public void updateUser(JobSeeker user) {
		repository.save(user);
	}
	
}
