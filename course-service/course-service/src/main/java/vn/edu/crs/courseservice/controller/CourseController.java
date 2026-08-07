package vn.edu.crs.courseservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.crs.courseservice.dto.CourseDTO;
import vn.edu.crs.courseservice.service.CourseService;

import java.util.List;

@RestController
@RequestMapping("/courses") // Cấu hình URL theo đúng đề bài của thầy: http://localhost:8082/courses
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    // 1. Read All (GET)
    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAll() {
        return ResponseEntity.ok(courseService.getAll());
    }

    // 2. Read One (GET by ID)
    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getById(id));
    }

    // 3. Create (POST)
    @PostMapping
    public ResponseEntity<CourseDTO> create(@Valid @RequestBody CourseDTO courseDTO) {
        // HttpStatus.CREATED sẽ trả về mã 201 đúng như kỳ vọng trong ảnh Postman
        return new ResponseEntity<>(courseService.create(courseDTO), HttpStatus.CREATED);
    }

    // 4. Update (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<CourseDTO> update(@PathVariable Long id, @Valid @RequestBody CourseDTO courseDTO) {
        return ResponseEntity.ok(courseService.update(id, courseDTO));
    }

    // 5. Delete (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courseService.delete(id);
        // HttpStatus.NO_CONTENT sẽ trả về mã 204
        return ResponseEntity.noContent().build();
    }
}