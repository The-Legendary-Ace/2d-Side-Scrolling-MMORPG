package io.jyberion.mmorpg.common.security;

import io.jyberion.mmorpg.common.config.ConfigLoader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class TokenUtil {

    private static final String SECRET_KEY = ConfigLoader.get("jwt.secret");

    // Generate JWT token
    public static String generateToken(String username, long expirationMillis) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // Validate JWT token and return username if valid
    public static String validateToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            // Token is invalid
            return null;
        }
    }
}
