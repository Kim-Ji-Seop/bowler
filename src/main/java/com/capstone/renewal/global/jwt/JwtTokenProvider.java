package com.capstone.renewal.global.jwt;

import com.capstone.renewal.global.redis.RedisDao;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.xml.bind.DatatypeConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.security.core.GrantedAuthority;
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {
    private final Key key;
    private final RedisDao redisDao;

    private static final int JWT_EXPIRATION_MS = 604800000; // 유효시간 : 일주일
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, RedisDao redisDao){
        this.redisDao = redisDao;
        byte[] secretByteKey = DatatypeConverter.parseBase64Binary(secretKey);
        this.key = Keys.hmacShaKeyFor(secretByteKey);
    }

    public TokenDto generateToken(Authentication authentication, Long userIdx) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION_MS);
        String accessToken= Jwts.builder()
                .setSubject(username) // 사용자
                .claim("auth",authorities)
                .claim("userIdx",userIdx)
                .setIssuedAt(new Date()) // 현재 시간 기반으로 생성
                .setExpiration(new Date(now.getTime()+30 * 60 * 1000L)) // 만료 시간 세팅 (30분)
                .signWith(key, SignatureAlgorithm.HS256) // 사용할 암호화 알고리즘, signature에 들어갈 secret 값 세팅
                .compact();
        String refreshToken=Jwts.builder()
                .setSubject(username) // 사용자
                .setIssuedAt(new Date()) // 현재 시간 기반으로 생성
                .setExpiration(expiryDate) // 만료 시간 세팅
                .signWith(key, SignatureAlgorithm.HS256) // 사용할 암호화 알고리즘, signature에 들어갈 secret 값 세팅
                .compact();
        // redis에 저장
        redisDao.setValues(authentication, refreshToken, JWT_EXPIRATION_MS + 5000L);

        return TokenDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
