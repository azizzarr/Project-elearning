package tn.siga.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.siga.entities.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {


}
