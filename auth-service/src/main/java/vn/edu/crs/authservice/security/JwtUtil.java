package vn.edu.crs.authservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HexFormat;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long expiration;

    /*
     * In fingerprint để kiểm tra auth-service
     * và course-service có thật sự dùng cùng secret hay không.
     *
     * KHÔNG in secret thật ra console.
     */
    @PostConstruct
    public void printJwtSecretFingerprint() {
        try {
            byte[] hash = MessageDigest
                    .getInstance("SHA-256")
                    .digest(jwtSecret.getBytes(StandardCharsets.UTF_8));

            String fingerprint = HexFormat.of().formatHex(hash);

            System.out.println("==========================================");
            System.out.println("AUTH SERVICE - JWT SECRET");
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
     * Tạo SecretKey từ jwt.secret.
     * Course-service phải sử dụng chính xác cùng cách này.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                jwtSecret.getBytes(StandardCharsets.UTF_8)
        );
    }

    /*
     * Tạo JWT sau khi login thành công.
     */
    public String generateToken(String username, String role) {

        Date now = new Date();

        Date expiryDate = new Date(
                now.getTime() + expiration
        );

        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /*
     * Verify JWT và lấy toàn bộ claims.
     */
    public Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /*
     * Lấy username từ subject.
     */
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    /*
     * Lấy role từ claim "role".
     */
    public String extractRole(String token) {
        return extractClaims(token)
                .get("role", String.class);
    }

    /*
     * Kiểm tra token đã hết hạn hay chưa.
     */
    public boolean isTokenExpired(String token) {

        Date expirationDate =
                extractClaims(token).getExpiration();

        return expirationDate.before(new Date());
    }

    /*
     * Token hợp lệ khi:
     * - chữ ký đúng
     * - format đúng
     * - chưa hết hạn
     */
    public boolean validateToken(String token) {

        try {
            Claims claims = extractClaims(token);

            return claims.getExpiration() != null
                    && claims.getExpiration().after(new Date());

        } catch (Exception e) {
            return false;
        }
    }
}