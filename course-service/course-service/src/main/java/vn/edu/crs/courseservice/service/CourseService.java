package vn.edu.crs.courseservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.crs.courseservice.dto.CourseDTO;
import vn.edu.crs.courseservice.entity.Course;
import vn.edu.crs.courseservice.repository.CourseRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
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
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy môn học với ID: " + id)
                );

        return convertToDTO(course);
    }

    // 3. Tạo mới khóa học
    public CourseDTO create(CourseDTO courseDTO) {

        if (courseRepository.existsByTenMonHocIgnoreCase(courseDTO.getTenMonHoc())) {
            throw new RuntimeException("Tên môn học đã tồn tại!");
        }

        Course course = new Course();

        course.setTenMonHoc(courseDTO.getTenMonHoc());
        course.setSoTinChi(courseDTO.getSoTinChi());
        course.setSoChoToiDa(courseDTO.getSoChoToiDa());

        // Khi vừa tạo môn học:
        // số chỗ còn lại = số chỗ tối đa
        course.setSoChoConLai(courseDTO.getSoChoToiDa());

        Course savedCourse = courseRepository.save(course);

        return convertToDTO(savedCourse);
    }

    // 4. Cập nhật khóa học
    public CourseDTO update(Long id, CourseDTO courseDTO) {

        Course course = courseRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy môn học với ID: " + id)
                );

        course.setTenMonHoc(courseDTO.getTenMonHoc());
        course.setSoTinChi(courseDTO.getSoTinChi());

        // Lưu số chỗ tối đa cũ để tính lại nếu cần
        Integer oldMaxSeats = course.getSoChoToiDa();
        Integer oldRemainingSeats = course.getSoChoConLai();

        course.setSoChoToiDa(courseDTO.getSoChoToiDa());

        /*
         * Nếu request có truyền soChoConLai thì dùng giá trị đó.
         * Nếu không truyền thì giữ lại số chỗ còn lại hiện tại.
         */
        if (courseDTO.getSoChoConLai() != null) {
            course.setSoChoConLai(courseDTO.getSoChoConLai());
        } else {
            course.setSoChoConLai(oldRemainingSeats);
        }

        Course updatedCourse = courseRepository.save(course);

        return convertToDTO(updatedCourse);
    }

    // 5. Xóa khóa học
    public void delete(Long id) {

        if (!courseRepository.existsById(id)) {
            throw new RuntimeException(
                    "Không tìm thấy môn học với ID: " + id
            );
        }

        courseRepository.deleteById(id);
    }

    // 6. Lấy danh sách có phân trang và tìm kiếm
    public Page<CourseDTO> search(String keyword, Pageable pageable) {

        Page<Course> coursePage;

        if (keyword == null || keyword.isBlank()) {
            coursePage = courseRepository.findAll(pageable);
        } else {
            coursePage =
                    courseRepository.findByTenMonHocContainingIgnoreCase(
                            keyword,
                            pageable
                    );
        }

        return coursePage.map(this::convertToDTO);
    }

    // API nội bộ: Giữ chỗ
    @Transactional
    public CourseDTO reserveSeat(Long courseId) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new java.util.NoSuchElementException(
                                "Không tìm thấy môn học id = " + courseId
                        )
                );

        if (course.getSoChoConLai() <= 0) {
            throw new IllegalStateException(
                    "Môn học đã hết chỗ, không thể đăng ký"
            );
        }

        course.setSoChoConLai(
                course.getSoChoConLai() - 1
        );

        return convertToDTO(
                courseRepository.save(course)
        );
    }

    // API nội bộ: Nhả chỗ
    @Transactional
    public CourseDTO releaseSeat(Long courseId) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new java.util.NoSuchElementException(
                                "Không tìm thấy môn học id = " + courseId
                        )
                );

        if (course.getSoChoConLai()
                < course.getSoChoToiDa()) {

            course.setSoChoConLai(
                    course.getSoChoConLai() + 1
            );
        }

        return convertToDTO(
                courseRepository.save(course)
        );
    }

    // Chuyển Entity -> DTO
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