package vn.edu.crs.courseservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.crs.courseservice.dto.CourseDTO;
import vn.edu.crs.courseservice.entity.Course;
import vn.edu.crs.courseservice.repository.CourseRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Tự động tạo constructor cho các biến final (thay thế cho @Autowired)
public class CourseService {

    private final CourseRepository courseRepository;

    // 1. Lấy danh sách tất cả khóa học
    public List<CourseDTO> getAll() {
        return courseRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 2. Lấy 1 khóa học theo ID
    public CourseDTO getById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học với ID: " + id));
        return convertToDTO(course);
    }

    // 3. Tạo mới khóa học
    public CourseDTO create(CourseDTO courseDTO) {
        // Gọi hàm đã tạo ở Bước 2 để kiểm tra trùng tên môn học
        if (courseRepository.existsByTenMonHocIgnoreCase(courseDTO.getTenMonHoc())) {
            throw new RuntimeException("Tên môn học đã tồn tại!");
        }

        Course course = new Course();
        course.setTenMonHoc(courseDTO.getTenMonHoc());
        course.setSoTinChi(courseDTO.getSoTinChi());
        course.setSoChoToiDa(courseDTO.getSoChoToiDa());
        course.setSoChoConLai(courseDTO.getSoChoConLai()); // Có thể setup logic tự động tính số chỗ ở đây nếu cần

        Course savedCourse = courseRepository.save(course);
        return convertToDTO(savedCourse);
    }

    // 4. Cập nhật khóa học
    public CourseDTO update(Long id, CourseDTO courseDTO) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học với ID: " + id));

        course.setTenMonHoc(courseDTO.getTenMonHoc());
        course.setSoTinChi(courseDTO.getSoTinChi());
        course.setSoChoToiDa(courseDTO.getSoChoToiDa());
        course.setSoChoConLai(courseDTO.getSoChoConLai());

        Course updatedCourse = courseRepository.save(course);
        return convertToDTO(updatedCourse);
    }

    // 5. Xóa khóa học
    public void delete(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy môn học với ID: " + id);
        }
        courseRepository.deleteById(id);
    }

    // Hàm phụ trợ: Chuyển đổi từ Entity sang DTO
    private CourseDTO convertToDTO(Course course) {
        CourseDTO dto = new CourseDTO();
        dto.setId(course.getId());
        dto.setTenMonHoc(course.getTenMonHoc());
        dto.setSoTinChi(course.getSoTinChi());
        dto.setSoChoToiDa(course.getSoChoToiDa());
        dto.setSoChoConLai(course.getSoChoConLai());
        return dto;
    }
}