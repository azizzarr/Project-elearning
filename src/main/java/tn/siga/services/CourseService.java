package tn.siga.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.siga.entities.Course;
import tn.siga.entities.User;
import tn.siga.repositories.CourseRepository;
import tn.siga.Interfaces.ICourseService;
import tn.siga.repositories.UserRepository;
import tn.siga.services.DTO.CourseDTO;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService implements ICourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserRepository userRepository;

   @Override
    public CourseDTO saveCourse(Course course) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        course.setUser(user);



        Course savedCourse = courseRepository.save(course);

        return modelMapper.map(savedCourse, CourseDTO.class);
    }



    @Override
    public CourseDTO createCourse2(Course course, MultipartFile image, MultipartFile pdfAttachment) {
        // Set the user for the course (current logged-in user)
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        course.setUser(user);

        // Handle the image (convert to byte array and set it in the course)
        try {
            if (image != null && !image.isEmpty()) {
                course.setImage(image.getBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to process the image.", e);
        }

        // Handle the PDF attachment (convert to byte array and set it in the course)
        try {
            if (pdfAttachment != null && !pdfAttachment.isEmpty()) {
                course.setPdfAttachment(pdfAttachment.getBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to process the PDF attachment.", e);
        }

        // Save the course
        Course savedCourse = courseRepository.save(course);

        // Return the saved course DTO
        return modelMapper.map(savedCourse, CourseDTO.class);
    }






    @Override
    public Optional<Course> getCourseById(Long courseId) {
        return courseRepository.findById(courseId);
    }

    // Get all courses
    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // Delete a course by ID
    @Override
    public void deleteCourse(Long courseId) {
        courseRepository.deleteById(courseId);
    }
}
