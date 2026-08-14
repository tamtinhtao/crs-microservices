package vn.edu.crs.courseservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // Đánh dấu đây là "Trạm gác" chặn mọi lỗi từ Controller ném ra
public class GlobalExceptionHandler {

    // 1. Xử lý các lỗi logic nghiệp vụ thông thường (ví dụ: không tìm thấy ID, trùng tên môn học)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 2. Xử lý lỗi Validation (từ @Valid trong Controller khi Client gửi thiếu dữ liệu)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        // Duyệt qua tất cả các trường bị lỗi và lấy tin nhắn lỗi đã cấu hình trong DTO
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    // Bổ sung handler cho lỗi hết chỗ (IllegalStateException) từ reserveSeat
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleConflict(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT) // HTTP Status 409
                .body(Map.of("message", ex.getMessage()));
    }
}