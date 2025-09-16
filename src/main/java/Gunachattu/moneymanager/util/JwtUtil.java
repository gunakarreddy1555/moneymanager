package Gunachattu.moneymanager.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {


    private String secret="MzNhZTZmMzNhZTZmMzNhZTZmMzNhZTZmMzNhZTZmMzNhZTZmMzNhZTZmMzM=";


    @Value("${jwt.expiration}")
    private long JWT_EXPIRATION; // in ms
// milliseconds

    // ===================== Token Generation =====================
    public String generateToken(String username) {
        Date issuedAt = new Date();
        Date expiry = new Date(System.currentTimeMillis() + JWT_EXPIRATION);

        System.out.println("Token Created Time: " + issuedAt);
        System.out.println("Token Expiry Time : " + expiry);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(issuedAt)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

    }

    // This can be used for refresh-token logic in future
    public String refreshToken(String token) {
        String username = extractUsername(token);
        return generateToken(username); // issue a brand-new token
    }

    // ===================== Token Validation =====================
    public boolean validateToken(String token, UserDetails userDetails) {
        String extractedUsername = extractUsername(token);

        // Extract claims
        Claims claims = extractAllClaims(token);

        // Log token times
        System.out.println("Token Created Time: " + claims.getIssuedAt());
        System.out.println("Token Expiry Time : " + claims.getExpiration());

        return (extractedUsername.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // ===================== Claims Extraction =====================
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {

        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ===================== Signing Key =====================
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
