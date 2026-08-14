package vn.edu.crs.courseservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.crs.courseservice.dto.CourseDTO;
import vn.edu.crs.courseservice.service.CourseService;

@RestController
@RequestMapping("/internal/courses") // Tiền tố /internal/ để dễ phân biệt và bảo mật sau này
@RequiredArgsConstructor
public class InternalCourseController {

    private final CourseService courseService;

    // API nội bộ: Giữ chỗ khi đăng ký môn học
    // Đường dẫn thực tế: POST /internal/courses/{id}/reserve
    @PostMapping ("/{id}/reserve-seat")
    public ResponseEntity<CourseDTO> reserveSeat(@PathVariable Long id) {
        CourseDTO updatedCourse = courseService.reserveSeat(id);
        return ResponseEntity.ok(updatedCourse);
    }

    // API nội bộ: Nhả chỗ khi hủy đăng ký môn học
    // Đường dẫn thực tế: POST /internal/courses/{id}/release
    @PostMapping    ("/{id}/release-seat")
    public ResponseEntity<CourseDTO> releaseSeat(@PathVariable Long id) {
        CourseDTO updatedCourse = courseService.releaseSeat(id);
        return ResponseEntity.ok(updatedCourse);
    }
}