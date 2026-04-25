package com.kitadevelopers.pos.security;

import com.kitadevelopers.pos.modules.user.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class JwtUtil {
//    @Value("${jwt.secret}")
//    private String secret;
    private final JwtProperties props;

    private Key getKey(){
        return Keys.hmacShaKeyFor(props.getSecret().getBytes());
    }

    public String generateAccessToken(User user){
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .signWith(getKey())
                .compact();
    }

//     return Jwts.builder()
//             .subject(email)
//                .issuedAt(new Date())
//            .expiration(new Date(System.currentTimeMillis() + 86400000))
//            .signWith(getKey())
//            .compact();

    public String generateRefreshToken(User user){
        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60 *60 * 24 * 7))
                .signWith(getKey())
                .compact();
    }

    public String extractEmail(String token){
        return Jwts.parser()
                .verifyWith((SecretKey) getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

//    public String extractEmail(String token){
//        return Jwts.parser()
//                .verifyWith((SecretKey) getKey())
//                .build()
//                .parseSignedClaims(token)
//                .getPayload()
//                .getSubject();
//    }

    public boolean isTokenValid(String token){
        try{
            Jwts.parser()
                    .verifyWith((SecretKey) getKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
