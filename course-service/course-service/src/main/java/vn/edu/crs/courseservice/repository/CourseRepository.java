package vn.edu.crs.courseservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.crs.courseservice.entity.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {

    // Hàm này được ghi thêm để kiểm tra trùng lặp tên môn học [cite: 98]
    boolean existsByTenMonHocIgnoreCase(String tenMonHoc);
}
