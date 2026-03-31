package com.pro.jobportal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/register", "/login", "/css/**", "/js/**", "/Js/**", "/uploads/**").permitAll()
                        .requestMatchers("/addJob", "/saveJob", "/myJobs", "/viewApplicants/**", "/updateStatus")
                        .hasRole("RECRUITER")
                        .requestMatchers("/applyJob", "/applyJobPage/**", "/myApplications", "/savedJobs", "/saveJob/**",
                                "/unsaveJob/**", "/removeSavedJob/**")
                        .hasRole("JOBSEEKER")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .rememberMe(Customizer.withDefaults());

        return http.build();
    }
}
