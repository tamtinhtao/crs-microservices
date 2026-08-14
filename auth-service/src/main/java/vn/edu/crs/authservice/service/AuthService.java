package vn.edu.crs.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.crs.authservice.dto.AuthResponse;
import vn.edu.crs.authservice.dto.LoginRequest;
import vn.edu.crs.authservice.entity.User;
import vn.edu.crs.authservice.exception.InvalidCredentialsException;
import vn.edu.crs.authservice.repository.UserRepository;
import vn.edu.crs.authservice.security.JwtUtil;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse login(LoginRequest request) {

        // Tìm user theo username
        User user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Sai username hoặc password"
                        )
                );

        // Kiểm tra password
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {

            throw new InvalidCredentialsException(
                    "Sai username hoặc password"
            );
        }

        // Lấy role
        String role = user.getRole();

        // Sinh JWT
        String token = jwtUtil.generateToken(
                user.getUsername(),
                role
        );

        // Trả response
        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(role)
                .build();
    }
}