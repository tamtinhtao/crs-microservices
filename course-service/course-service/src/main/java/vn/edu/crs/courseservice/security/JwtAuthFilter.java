package vn.edu.crs.courseservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String jwtSecret;

    /*
     * In fingerprint để so sánh với auth-service.
     *
     * AUTH SHA256 và COURSE SHA256
     * bắt buộc phải giống nhau 100%.
     */
    @PostConstruct
    public void printJwtSecretFingerprint() {
        try {
            byte[] hash = MessageDigest
                    .getInstance("SHA-256")
                    .digest(jwtSecret.getBytes(StandardCharsets.UTF_8));

            String fingerprint = HexFormat.of().formatHex(hash);

            System.out.println("==========================================");
            System.out.println("COURSE SERVICE - JWT SECRET");
            System.out.println("Length : " + jwtSecret.length());
            System.out.println("SHA256 : " + fingerprint);
            System.out.println("==========================================");

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Không thể tạo fingerprint cho JWT secret",
                    e
            );
        }
    }

    /*
     * Tạo key giống hệt auth-service.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                jwtSecret.getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // ==========================================
        // 1. Đọc Authorization Header
        // ==========================================
        String authHeader =
                request.getHeader("Authorization");

        // Không có JWT -> cho đi tiếp.
        // SecurityConfig sẽ quyết định endpoint
        // đó permitAll hay authenticated.
        if (authHeader == null
                || !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        // ==========================================
        // 2. Tách JWT
        // ==========================================
        String token =
                authHeader.substring(7).trim();

        if (token.isBlank()) {
            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );
            return;
        }

        try {

            // ==========================================
            // 3. Verify chữ ký JWT
            // ==========================================
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // ==========================================
            // 4. Lấy thông tin trong JWT
            // ==========================================
            String username =
                    claims.getSubject();

            String role =
                    claims.get("role", String.class);

            // ==========================================
            // 5. Tạo Authentication
            // ==========================================
            if (username != null
                    && !username.isBlank()
                    && SecurityContextHolder
                    .getContext()
                    .getAuthentication() == null) {

                List<SimpleGrantedAuthority> authorities;

                if (role != null
                        && !role.isBlank()) {

                    String authority =
                            role.startsWith("ROLE_")
                                    ? role
                                    : "ROLE_" + role;

                    authorities = List.of(
                            new SimpleGrantedAuthority(authority)
                    );

                } else {

                    authorities = List.of();
                }

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                authorities
                        );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
            }

        } catch (Exception e) {

            System.out.println(
                    "===== COURSE JWT VERIFY ERROR ====="
            );

            System.out.println(
                    e.getClass().getName()
                            + ": "
                            + e.getMessage()
            );

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            return;
        }

        // ==========================================
        // 6. JWT hợp lệ -> tiếp tục
        // ==========================================
        filterChain.doFilter(request, response);
    }
}