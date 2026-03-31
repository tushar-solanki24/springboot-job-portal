package com.pro.jobportal.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.pro.jobportal.dto.JobSeekerDTO;
import com.pro.jobportal.dto.LoginDTO;
import com.pro.jobportal.entity.JobSeeker;
import com.pro.jobportal.service.FileStorageService;
import com.pro.jobportal.service.JobSeekerService;

import jakarta.validation.Valid;

@Controller
public class JobseekerController {
	@Autowired
	private JobSeekerService service;

	@Autowired
	private FileStorageService fileStorageService;

	@GetMapping("/register")
	public String showRegisterUser(Model model) {
		model.addAttribute("user", new JobSeekerDTO());
		return "register";
	}

	@PostMapping("/register")
	public String registerUser(@Valid @ModelAttribute("user") JobSeekerDTO dto, BindingResult result, Model model) {

		if (!dto.getPassword().equals(dto.getConfirmPassword())) {
			result.rejectValue("confirmPassword", null, "Passwords do not match");
		}

		if (service.emailExists(dto.getEmail())) {
			result.rejectValue("email", null, "Email already registered");
		}

		if (result.hasErrors()) {
			return "register";
		}
		if (service.mobileExists(dto.getMobile())) {
		    result.rejectValue("mobile", null, "Mobile number already registered");
		    return "register";
		}

		service.registerUser(dto);

		return "redirect:/login?registered";
	}

	@GetMapping("/login")
	public String showLoginPage(Model model, Authentication authentication) {
		if (authentication != null && authentication.isAuthenticated()
				&& !(authentication instanceof AnonymousAuthenticationToken)) {
			return "redirect:/dashboard";
		}

		model.addAttribute("loginUser", new LoginDTO());

		return "login";
	}

	@GetMapping("/dashboard")
	public String dashboard(Principal principal, Model model) {
	    JobSeeker user = requireUser(principal);
	    if (user == null) {
	        return "redirect:/login";
	    }

	    model.addAttribute("username", user.getUsername());

	    return "dashboard";
	}

	@GetMapping("/profile")
	public String profile(Principal principal, Model model) {
	    JobSeeker user = requireUser(principal);
	    if (user == null) {
	        return "redirect:/login";
	    }

	    model.addAttribute("user", user);

	    return "profile";
	}

	@GetMapping("/skillSuggestions")
	@ResponseBody
	public List<String> skillSuggestions(@RequestParam String keyword) {

	    List<String> skills = List.of(
	        "Java",
	        "Spring Boot",
	        "Spring MVC",
	        "Hibernate",
	        "React",
	        "Angular",
	        "MySQL",
	        "Docker",
	        "AWS",
	        "Python"
	    );

	    return skills.stream()
	            .filter(skill -> skill.toLowerCase().contains(keyword.toLowerCase()))
	            .limit(5)
	            .toList();
	}

	@GetMapping("/editProfile")
	public String editProfile(Principal principal, Model model) {
	    JobSeeker user = requireUser(principal);
	    if (user == null) {
	        return "redirect:/login";
	    }

	    model.addAttribute("user", user);

	    return "editProfile";
	}

	@PostMapping("/updateProfile")
	public String updateProfile(@RequestParam String username,
	                            @RequestParam String mobile,
	                            @RequestParam String skills,
	                            @RequestParam("profileImage") MultipartFile file,
	                            Principal principal) {
	    JobSeeker user = requireUser(principal);
	    if (user == null) {
	        return "redirect:/login";
	    }

	    user.setUsername(username);
	    user.setMobile(mobile);
	    user.setSkills(skills);

	    if (!file.isEmpty()) {
	        try {
	            user.setProfileImage(fileStorageService.storeProfileImage(file));
	        } catch (Exception e) {
	            return "redirect:/editProfile?error=file";
	        }
	    }
	    service.updateUser(user);

	    return "redirect:/profile";
	}

	private JobSeeker requireUser(Principal principal) {
		if (principal == null) {
			return null;
		}
		Optional<JobSeeker> user = service.findByEmail(principal.getName());
		return user.orElse(null);
	}

}
