package vn.edu.crs.authservice.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ==============================
    // Sai username / password
    // ==============================
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials(
            InvalidCredentialsException ex,
            WebRequest request
    ) {

        Map<String, Object> body = new LinkedHashMap<>();

        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("error", "Unauthorized");
        body.put("message", ex.getMessage());
        body.put(
                "path",
                request.getDescription(false)
                        .replace("uri=", "")
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(body);
    }

    // ==============================
    // Các lỗi không mong muốn khác
    // ==============================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(
            Exception ex,
            WebRequest request
    ) {

        // Ghi lỗi thật vào console/log server
        logger.error("Unexpected error occurred", ex);

        Map<String, Object> body = new LinkedHashMap<>();

        body.put("timestamp", Instant.now().toString());
        body.put(
                "status",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        body.put(
                "error",
                "Internal Server Error"
        );

        // Không trả ex.getMessage() để tránh lộ thông tin nội bộ
        body.put(
                "message",
                "Đã xảy ra lỗi hệ thống"
        );

        body.put(
                "path",
                request.getDescription(false)
                        .replace("uri=", "")
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body);
    }
}