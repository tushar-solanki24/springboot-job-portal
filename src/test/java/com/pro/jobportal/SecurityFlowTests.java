package com.pro.jobportal;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.pro.jobportal.entity.Job;
import com.pro.jobportal.entity.JobApplication;
import com.pro.jobportal.entity.JobSeeker;
import com.pro.jobportal.service.JobApplicationService;
import com.pro.jobportal.service.JobSeekerService;
import com.pro.jobportal.service.JobService;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityFlowTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobSeekerService jobSeekerService;

    @MockBean
    private JobService jobService;

    @MockBean
    private JobApplicationService jobApplicationService;

    @Test
    void unauthenticatedUserCannotAccessDashboard() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "seeker@test.com", roles = "JOBSEEKER")
    void jobSeekerCannotAccessRecruiterOnlyEndpoint() throws Exception {
        mockMvc.perform(get("/addJob"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "recruiter@test.com", roles = "RECRUITER")
    void recruiterCannotUpdateStatusForAnotherRecruiterJob() throws Exception {
        JobSeeker recruiter = new JobSeeker();
        recruiter.setId(10L);
        recruiter.setEmail("recruiter@test.com");
        recruiter.setRole("RECRUITER");
        when(jobSeekerService.findByEmail("recruiter@test.com")).thenReturn(Optional.of(recruiter));

        Job job = new Job();
        job.setJobId(100L);
        JobApplication application = new JobApplication();
        application.setApplicationId(200L);
        application.setJob(job);
        when(jobApplicationService.getApplicationById(200L)).thenReturn(application);
        when(jobService.isJobOwnedByRecruiter(eq(100L), eq(10L))).thenReturn(false);

        mockMvc.perform(post("/updateStatus")
                        .with(csrf())
                        .param("applicationId", "200")
                        .param("status", "Shortlisted"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/myJobs?error=*"));

        verify(jobService).isJobOwnedByRecruiter(anyLong(), anyLong());
    }
}
