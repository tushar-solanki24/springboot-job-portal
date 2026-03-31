package com.pro.jobportal.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.pro.jobportal.entity.Job;
import com.pro.jobportal.entity.JobApplication;
import com.pro.jobportal.entity.JobSeeker;
import com.pro.jobportal.entity.SavedJob;
import com.pro.jobportal.service.JobApplicationService;
import com.pro.jobportal.service.JobSeekerService;
import com.pro.jobportal.service.JobService;
import com.pro.jobportal.service.SavedJobService;

@Controller
public class JobController {

	@Autowired
	private JobService jobService;

	@Autowired
	private JobApplicationService applicationService;
	
	@Autowired
	private SavedJobService savedJobService;

	@Autowired
	private JobSeekerService jobSeekerService;

	// ADD_JOB
	@GetMapping("/addJob")
	public String addJobPage(Principal principal, Model model) {
	    JobSeeker user = requireUser(principal);
	    if (user == null) {
	        return "redirect:/login";
	    }

	    if (!user.getRole().equals("RECRUITER")) {
	        return "redirect:/dashboard";
	    }

	    model.addAttribute("job", new Job());

	    return "addJob";
	}

	// SAVE_JOB
	@PostMapping("/saveJob")
	public String saveJob(@ModelAttribute Job job, Principal principal) {
		JobSeeker user = requireUser(principal);
		if (user == null) {
			return "redirect:/login";
		}
		if (!"RECRUITER".equals(user.getRole())) {
			return "redirect:/dashboard";
		}

		job.setRecruiterId(user.getId());
	
		job.setPostedDate(LocalDate.now());
		job.setExpiryDate(LocalDate.now().plusDays(30));

		jobService.saveJob(job);
	
		return "redirect:/dashboard";
	}

	// VIEW_JOBS
	@GetMapping("/viewJobs")
	public String viewJobs(@RequestParam(required = false) String title,
	        @RequestParam(required = false) String location,
	        @RequestParam(defaultValue = "0") int page,
	        Principal principal,
	        Model model) {

	    JobSeeker user = requireUser(principal);
	    if (user == null) {
	        return "redirect:/login";
	    }

	    Page<Job> jobPage = jobService.searchJobs(title, location, page);

	    List<JobApplication> applications =
	            applicationService.getApplicationsByUser(user.getId());

	    List<Long> appliedJobIds =
	            applications.stream()
	                    .map(app -> app.getJob().getJobId())
	                    .toList();

	    List<SavedJob> savedJobs = savedJobService.getSavedJobs(user.getId());

	    List<Long> savedJobIds = savedJobs.stream()
	            .map(s -> s.getJob().getJobId())
	            .toList();

	    model.addAttribute("jobs", jobPage.getContent());
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", jobPage.getTotalPages());
	    model.addAttribute("appliedJobIds", appliedJobIds);
	    model.addAttribute("savedJobIds", savedJobIds);

	    return "viewJobs";
	}

	// APPLY_JOB
	@GetMapping("/applyJobPage/{id}")
	public String applyJobPage(@PathVariable Long id, Model model) {

		Job job = jobService.getJobById(id);

		model.addAttribute("job", job);

		return "applyJob";
	}

	// MY_APPLICATION
	@GetMapping("/myApplications")
	public String myApplications(Principal principal, Model model) {
		JobSeeker user = requireUser(principal);

		if (user == null) {
			return "redirect:/login";
		}

		List<JobApplication> applications = applicationService.getApplicationsByUser(user.getId());

		model.addAttribute("applications", applications);

		return "myApplications";
	}

	@GetMapping("/viewJob/{id}")
	public String viewJobDetails(@PathVariable Long id, Model model) {

		Job job = jobService.getJobById(id);

		
		model.addAttribute("job", job);

		return "jobDetails";
	}

	@PostMapping("/applyJob")
	public String applyJob(@RequestParam Long jobId, @RequestParam("resume") MultipartFile resume,
			Principal principal) {
		JobSeeker user = requireUser(principal);

		if (user == null) {
			return "redirect:/login";
		}

		boolean success = applicationService.applyJob(user.getId(), jobId, resume);
		if (!success) {
			return "redirect:/applyJobPage/" + jobId + "?error=upload";
		}

		return "redirect:/myApplications";
	}

	@GetMapping("/jobSuggestions")
	@ResponseBody
	public List<Job> jobSuggestions(@RequestParam String keyword) {
		return jobService.getJobSuggestions(keyword);
	}
	
	@GetMapping("/myJobs")
	public String myJobs(Principal principal, Model model) {
		JobSeeker user = requireUser(principal);
		if (user == null) {
			return "redirect:/login";
		}
		if (!"RECRUITER".equals(user.getRole())) {
			return "redirect:/dashboard";
		}

		List<Job> jobs = jobService.getJobsByRecruiter(user.getId());
		Map<Long, Long> applicantCounts = new HashMap<>();
		for (Job job : jobs) {
			long count = applicationService.getApplicantCount(job.getJobId());
			applicantCounts.put(job.getJobId(), count);
		}

		model.addAttribute("jobs", jobs);
		model.addAttribute("counts", applicantCounts);
		return "myJobs";
	}
	
	@GetMapping("/viewApplicants/{jobId}")
	public String viewApplicants(@PathVariable Long jobId, Principal principal, Model model) {
		JobSeeker user = requireUser(principal);
		if (user == null) {
			return "redirect:/login";
		}
		if (!jobService.isJobOwnedByRecruiter(jobId, user.getId())) {
			return "redirect:/myJobs?error=unauthorized";
		}

		List<JobApplication> applications = applicationService.getApplicantsByJob(jobId);
		model.addAttribute("applications", applications);
		return "viewApplicants";
	}
	
	@PostMapping("/updateStatus")
	public String updateStatus(@RequestParam Long applicationId,
	                           @RequestParam String status,
	                           Principal principal) {
		JobSeeker user = requireUser(principal);
		if (user == null) {
			return "redirect:/login";
		}

		JobApplication application = applicationService.getApplicationById(applicationId);
		if (application == null || application.getJob() == null
				|| !jobService.isJobOwnedByRecruiter(application.getJob().getJobId(), user.getId())) {
			return "redirect:/myJobs?error=unauthorized";
		}

		application.setStatus(status);
		applicationService.save(application);
		return "redirect:/viewApplicants/" + application.getJob().getJobId();
	}
	
	@GetMapping("/saveJob/{jobId}")
	public String saveJob(@PathVariable Long jobId, Principal principal) {
	    JobSeeker user = requireUser(principal);

	    if (user == null) {
	        return "redirect:/login";
	    }

	    savedJobService.saveJob(user.getId(), jobId);

	    return "redirect:/viewJobs";
	}
	
	@GetMapping("/savedJobs")
	public String savedJobs(Principal principal, Model model) {
	    JobSeeker user = requireUser(principal);

	    if (user == null) {
	        return "redirect:/login";
	    }

	    model.addAttribute("savedJobs",
	            savedJobService.getSavedJobs(user.getId()));

	    return "savedJobs";
	}
	
	@GetMapping("/removeSavedJob/{jobId}")
	public String removeSavedJob(@PathVariable Long jobId, Principal principal) {
	    JobSeeker user = requireUser(principal);

	    if (user == null) {
	        return "redirect:/login";
	    }

	    savedJobService.removeSavedJob(user.getId(), jobId);

	    return "redirect:/savedJobs";
	}
	
	@GetMapping("/unsaveJob/{id}")
	public String unsaveJob(@PathVariable Long id, Principal principal) {
	    JobSeeker user = requireUser(principal);
	    if (user == null) {
	        return "redirect:/login";
	    }

	    savedJobService.removeSavedJob(user.getId(), id);

	    return "redirect:/viewJobs";
	}

	private JobSeeker requireUser(Principal principal) {
		if (principal == null) {
			return null;
		}
		Optional<JobSeeker> user = jobSeekerService.findByEmail(principal.getName());
		return user.orElse(null);
	}
}