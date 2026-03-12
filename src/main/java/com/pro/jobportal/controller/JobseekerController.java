package com.pro.jobportal.controller;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.pro.jobportal.service.JobSeekerService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class JobseekerController {
	@Autowired
	private JobSeekerService service;

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
		if(service.mobileExists(dto.getMobile())){
		    result.rejectValue("mobile", null, "Mobile number already registered");
		}

		service.registerUser(dto);

		return "redirect:/login";
	}

	@GetMapping("/login")
	public String showLoginPage(Model model) {

		model.addAttribute("loginUser", new LoginDTO());

		return "login";
	}

	@PostMapping("/login")
	public String loginUser(@Valid @ModelAttribute("loginUser") LoginDTO dto, BindingResult result, Model model, HttpSession session) {

		if (result.hasErrors()) {
			return "login";
		}

		JobSeeker user = service.loginUser(dto.getEmail(), dto.getPassword());

		if (user == null) {
			model.addAttribute("error", "Invalid email or password");
			return "login";
		}
		session.setAttribute("loggedUser", user);

		return "redirect:/dashboard";
	}

	@GetMapping("/dashboard")
	public String dashboard(HttpSession session, Model model){

	    JobSeeker user = (JobSeeker) session.getAttribute("loggedUser");

	    if(user == null){
	        return "redirect:/login";
	    }

	    model.addAttribute("username", user.getUsername());

	    return "dashboard";
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/login";
	}
	
	@GetMapping("/profile")
	public String profile(HttpSession session, Model model){

	    JobSeeker user = (JobSeeker) session.getAttribute("loggedUser");

	    if(user == null){
	        return "redirect:/login";
	    }

	    model.addAttribute("user", user);

	    return "profile";
	}
	
	@GetMapping("/skillSuggestions")
	@ResponseBody
	public List<String> skillSuggestions(@RequestParam String keyword){

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
	public String editProfile(HttpSession session, Model model){

	    JobSeeker user = (JobSeeker) session.getAttribute("loggedUser");

	    if(user == null){
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
	                            HttpSession session){

	    JobSeeker user = (JobSeeker) session.getAttribute("loggedUser");

	    user.setUsername(username);
	    user.setMobile(mobile);
	    user.setSkills(skills);

	    if(!file.isEmpty()){

	        try{

	            String uploadDir = System.getProperty("user.dir") + "/uploads/";

	            File directory = new File(uploadDir);

	            if(!directory.exists()){
	                directory.mkdirs();
	            }

	            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

	            File saveFile = new File(uploadDir + fileName);

	            file.transferTo(saveFile);

	            user.setProfileImage(fileName);

	        }catch(Exception e){
	            e.printStackTrace();
	        }

	    }
	    service.updateUser(user);

	    session.setAttribute("loggedUser", user);

	    return "redirect:/profile";
	
	}
	
}
