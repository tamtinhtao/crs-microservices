package vn.edu.crs.courseservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.crs.courseservice.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseRepository extends JpaRepository<Course, Long> {

    // Hàm này được ghi thêm để kiểm tra trùng lặp tên môn học [cite: 98]
    boolean existsByTenMonHocIgnoreCase(String tenMonHoc);
    // Bổ sung thêm hàm tìm kiếm/lọc phân trang này vào dưới
    Page<Course> findByTenMonHocContainingIgnoreCase(String tenMonHoc, Pageable pageable);
}
