package tn.siga.Interfaces;

import org.springframework.web.multipart.MultipartFile;
import tn.siga.entities.Course;
import tn.siga.entities.User;
import tn.siga.services.DTO.CourseDTO;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ICourseService {
    // Create or Update a course
   CourseDTO saveCourse(Course course);

   // CourseDTO saveCourse(Course course, Set<String> categoryNames);

  //  CourseDTO createCourse(Course course, MultipartFile image);

   // CourseDTO createCourse2(Course course, MultipartFile image, MultipartFile pdfAttachment);

    CourseDTO createCourse2(Course course, MultipartFile image, MultipartFile pdfAttachment);

    // Get a course by ID
    Optional<Course> getCourseById(Long courseId);

    // Get all courses
    List<Course> getAllCourses();

    // Delete a course by ID
    void deleteCourse(Long courseId);
}
