package com.fileupload.auth;

import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTHandler {

    @Value("${expiryMillis}")
    private long expiryMillis;

    @Value("${secret}")
    private String jwtSecret;

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(new JSONObject()
                        .fluentPut("email", email)
                        .toJSONString()
                )
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiryMillis * 1000))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    private Claims getAllClaimsFromToken(String token, String secret) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    public JSONObject validAndGetDetailFromToken(String token) {
        return JSONObject.parseObject(
                Jwts.parser()
                        .setSigningKey(jwtSecret)
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject(),
                JSONObject.class);
    }


}
