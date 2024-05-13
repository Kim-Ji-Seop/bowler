package com.capstone.renewal.global.jwt;

import com.capstone.renewal.global.error.BaseException;
import com.capstone.renewal.global.error.ErrorCode;
import com.capstone.renewal.global.redis.RedisDao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.xml.bind.DatatypeConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.security.core.GrantedAuthority;

import javax.security.auth.Subject;
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

    public TokenDto generateToken(Authentication authentication) {
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
                .setIssuedAt(new Date()) // 현재 시간 기반으로 생성 30 * 60
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

    public TokenDto reissueAtk(String userUid,String rtkUid) throws JsonProcessingException {

        if(!rtkUid.equals(userUid)){
            throw new BaseException(ErrorCode.EXPIRED_AUTHENTICATION);
        }
        Authentication authentication=new UsernamePasswordAuthenticationToken(userUid,null,null);

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION_MS);

        String accessToken=Jwts.builder()
                .setSubject((String) authentication.getPrincipal()) // 사용자
                .setIssuedAt(new Date()) // 현재 시간 기반으로 생성
                .setExpiration(new Date(now.getTime()+30 * 60 * 1000L)) // 만료 시간 세팅 (1일)
                .signWith(key,SignatureAlgorithm.HS256) // 사용할 암호화 알고리즘, signature에 들어갈 secret 값 세팅
                .compact();

        String refreshToken=Jwts.builder()
                .setSubject((String) authentication.getPrincipal()) // 사용자
                .setIssuedAt(new Date()) // 현재 시간 기반으로 생성
                .setExpiration(expiryDate) // 만료 시간 세팅
                .signWith(key,SignatureAlgorithm.HS256) // 사용할 암호화 알고리즘, signature에 들어갈 secret 값 세팅
                .compact();

        // redis에 저장
        redisDao.setValues(authentication, refreshToken, JWT_EXPIRATION_MS + 5000L);

        return TokenDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // Jwt 토큰에서 아이디 추출
    public String getUserUidFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public Subject getSubject(String accessToken) throws JsonProcessingException {
        String subjectStr = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .getBody()
                .getSubject();
        return new ObjectMapper().readValue(subjectStr, Subject.class);
    }

    public Long getExpiration(String accessToken) {
        // accessToken 남은 유효시간
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .getBody()
                .getExpiration();
        // 현재 시간
        Long now = new Date().getTime();
        return (expiration.getTime() - now);
    }


}
