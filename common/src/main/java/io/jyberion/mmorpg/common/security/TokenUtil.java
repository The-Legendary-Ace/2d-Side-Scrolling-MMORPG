package io.jyberion.mmorpg.common.security;

import io.jyberion.mmorpg.common.config.ConfigLoader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.nio.charset.StandardCharsets;
import java.util.Date;

public class TokenUtil {
    private static final String SECRET_KEY;

    static {
        // Load the secret key from configuration
        SECRET_KEY = ConfigLoader.get("jwt.secret");
        if (SECRET_KEY == null || SECRET_KEY.isEmpty()) {
            throw new IllegalStateException("JWT secret key is not configured");
        }
    }

    public static String generateToken(String userId, long expirationMillis) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                   .setSubject(userId)
                   .setIssuedAt(now)
                   .setExpiration(expiryDate)
                   .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                   .compact();
    }

    public static String getUserIdFromToken(String token) {
        return Jwts.parser()
                   .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                   .parseClaimsJws(token)
                   .getBody()
                   .getSubject();
    }

    public static boolean validateToken(String token) {
        try {
            Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // Log the exception if needed
            return false;
        }
    }
}
