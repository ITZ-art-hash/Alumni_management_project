package com.alumni.platform.config;

import com.alumni.platform.entity.Alumni;
import com.alumni.platform.entity.Event;
import com.alumni.platform.entity.JobPosting;
import com.alumni.platform.entity.Role;
import com.alumni.platform.entity.User;
import com.alumni.platform.repository.AlumniRepository;
import com.alumni.platform.repository.EventRepository;
import com.alumni.platform.repository.JobPostingRepository;
import com.alumni.platform.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AlumniRepository alumniRepository;
    private final EventRepository eventRepository;
    private final JobPostingRepository jobPostingRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, AlumniRepository alumniRepository,
                            EventRepository eventRepository, JobPostingRepository jobPostingRepository,
                            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.alumniRepository = alumniRepository;
        this.eventRepository = eventRepository;
        this.jobPostingRepository = jobPostingRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User admin = User.builder()
                    .email("admin@alumni.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .approved(true)
                    .build();

            User user = User.builder()
                    .email("user@alumni.com")
                    .password(passwordEncoder.encode("user123"))
                    .role(Role.ALUMNI)
                    .approved(true)
                    .build();

            userRepository.saveAll(List.of(admin, user));
        }

        if (alumniRepository.count() == 0) {
            alumniRepository.saveAll(List.of(
                    Alumni.builder().name("Aarav Sharma").email("aarav.sharma@example.com").batch(2015).department("CSE").company("Google").skills("Java, Spring, Cloud").linkedin("linkedin.com/in/aaravsharma").phone("9876543210").build(),
                    Alumni.builder().name("Priya Patel").email("priya.patel@example.com").batch(2016).department("ECE").company("Microsoft").skills("C++, Embedded Systems, IoT").linkedin("linkedin.com/in/priyapatel").phone("9876543211").build(),
                    Alumni.builder().name("Rohan Mehta").email("rohan.mehta@example.com").batch(2017).department("ME").company("Tata Motors").skills("CAD, SolidWorks, Manufacturing").linkedin("linkedin.com/in/rohanmehta").phone("9876543212").build(),
                    Alumni.builder().name("Sneha Gupta").email("sneha.gupta@example.com").batch(2018).department("CE").company("L&T Construction").skills("AutoCAD, Structural Analysis, Project Management").linkedin("linkedin.com/in/snehagupta").phone("9876543213").build(),
                    Alumni.builder().name("Vikram Singh").email("vikram.singh@example.com").batch(2019).department("CSE").company("Amazon").skills("Python, Machine Learning, AWS").linkedin("linkedin.com/in/vikramsingh").phone("9876543214").build(),
                    Alumni.builder().name("Ananya Iyer").email("ananya.iyer@example.com").batch(2019).department("ECE").company("Intel").skills("VLSI, Verilog, Chip Design").linkedin("linkedin.com/in/ananyaiyer").phone("9876543215").build(),
                    Alumni.builder().name("Karan Malhotra").email("karan.malhotra@example.com").batch(2020).department("CSE").company("TCS").skills("Java, React, DevOps").linkedin("linkedin.com/in/karanmalhotra").phone("9876543216").build(),
                    Alumni.builder().name("Isha Reddy").email("isha.reddy@example.com").batch(2021).department("ME").company("Bosch").skills("Robotics, Automation, Python").linkedin("linkedin.com/in/ishareddy").phone("9876543217").build(),
                    Alumni.builder().name("Arjun Nair").email("arjun.nair@example.com").batch(2021).department("CE").company("DLF Ltd").skills("Revit, Project Management, AutoCAD").linkedin("linkedin.com/in/arjunnair").phone("9876543218").build(),
                    Alumni.builder().name("Meera Joshi").email("meera.joshi@example.com").batch(2022).department("CSE").company("Infosys").skills("Python, Data Science, Machine Learning").linkedin("linkedin.com/in/meerajoshi").phone("9876543219").build(),
                    Alumni.builder().name("Aditya Rao").email("aditya.rao@example.com").batch(2023).department("ECE").company("Qualcomm").skills("Signal Processing, Embedded C, IoT").linkedin("linkedin.com/in/adityarao").phone("9876543220").build(),
                    Alumni.builder().name("Kavya Menon").email("kavya.menon@example.com").batch(2024).department("CSE").company("Microsoft").skills("React, Node.js, Cloud, DevOps").linkedin("linkedin.com/in/kavyamenon").phone("9876543221").build()
            ));
        }

        if (eventRepository.count() == 0) {
            eventRepository.saveAll(List.of(
                    Event.builder().title("Annual Alumni Meet 2026").description("Campus reunion with networking, dinner, and a keynote from notable alumni.").eventDate(LocalDate.of(2026, 12, 20)).location("Main Campus Auditorium").build(),
                    Event.builder().title("Tech Alumni Meetup").description("An evening of tech talks and networking for CSE/ECE alumni.").eventDate(LocalDate.of(2026, 11, 5)).location("Bangalore").build()
            ));
        }

        if (jobPostingRepository.count() == 0) {
            jobPostingRepository.saveAll(List.of(
                    JobPosting.builder().title("Backend Engineer").company("Google").description("Referral available for backend roles on the Cloud team. Looking for strong Java and Spring Boot skills, plus hands-on cloud infrastructure experience.").contactInfo("aarav.sharma@example.com").postedByEmail("aarav.sharma@example.com").postedDate(LocalDate.now().minusDays(3)).build(),
                    JobPosting.builder().title("Data Scientist").company("Infosys").description("Openings for freshers and experienced candidates skilled in Python, data science, and machine learning model development.").contactInfo("meera.joshi@example.com").postedByEmail("meera.joshi@example.com").postedDate(LocalDate.now().minusDays(1)).build(),
                    JobPosting.builder().title("Embedded Systems Engineer").company("Microsoft").description("Building IoT devices; seeking C++, embedded systems, and firmware experience for connected hardware.").contactInfo("priya.patel@example.com").postedByEmail("priya.patel@example.com").postedDate(LocalDate.now().minusDays(5)).build(),
                    JobPosting.builder().title("Mechanical Design Engineer").company("Tata Motors").description("CAD and SolidWorks expertise needed for manufacturing and product design projects.").contactInfo("rohan.mehta@example.com").postedByEmail("rohan.mehta@example.com").postedDate(LocalDate.now().minusDays(6)).build(),
                    JobPosting.builder().title("Structural Site Engineer").company("L&T Construction").description("AutoCAD and structural analysis skills required; project management experience is a plus.").contactInfo("sneha.gupta@example.com").postedByEmail("sneha.gupta@example.com").postedDate(LocalDate.now().minusDays(4)).build(),
                    JobPosting.builder().title("Machine Learning Engineer").company("Amazon").description("Python and machine learning role on the AWS platform team, building cloud-scale ML pipelines.").contactInfo("vikram.singh@example.com").postedByEmail("vikram.singh@example.com").postedDate(LocalDate.now().minusDays(2)).build(),
                    JobPosting.builder().title("VLSI Design Engineer").company("Intel").description("Chip design role requiring VLSI and Verilog experience for next-generation processors.").contactInfo("ananya.iyer@example.com").postedByEmail("ananya.iyer@example.com").postedDate(LocalDate.now().minusDays(7)).build(),
                    JobPosting.builder().title("Full Stack Developer").company("TCS").description("Java backend with a React frontend; DevOps exposure for CI/CD pipelines preferred.").contactInfo("karan.malhotra@example.com").postedByEmail("karan.malhotra@example.com").postedDate(LocalDate.now().minusDays(8)).build(),
                    JobPosting.builder().title("Robotics Automation Engineer").company("Bosch").description("Robotics and automation engineer role; Python scripting for automated test rigs.").contactInfo("isha.reddy@example.com").postedByEmail("isha.reddy@example.com").postedDate(LocalDate.now().minusDays(9)).build(),
                    JobPosting.builder().title("Cloud DevOps Engineer").company("Microsoft").description("React and Node.js product team looking for cloud and DevOps skills to manage deployments.").contactInfo("kavya.menon@example.com").postedByEmail("kavya.menon@example.com").postedDate(LocalDate.now().minusDays(10)).build()
            ));
        }
    }
}
