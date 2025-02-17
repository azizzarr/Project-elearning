package tn.siga.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.siga.entities.Course;
import tn.siga.services.CourseService;
import tn.siga.services.DTO.CourseDTO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @PreAuthorize("@roleService.hasRole('ROLE_INSTRUCTOR')")
    @PostMapping("/CreateCourse")
    public ResponseEntity<CourseDTO> createCourse(
            @RequestParam("course") String courseJson,
            @RequestParam("image") MultipartFile image,
            @RequestParam("pdfAttachment") MultipartFile pdfAttachment) throws IOException {


        Course course = new ObjectMapper().readValue(courseJson, Course.class);


        CourseDTO savedCourseDTO = courseService.createCourse2(course, image, pdfAttachment);

        return new ResponseEntity<>(savedCourseDTO, HttpStatus.CREATED);
    }




    @GetMapping("/{courseId}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long courseId) {
        Optional<Course> course = courseService.getCourseById(courseId);
        if (course.isPresent()) {
            return new ResponseEntity<>(course.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long courseId) {
        courseService.deleteCourse(courseId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }




}
