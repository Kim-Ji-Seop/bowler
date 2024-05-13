package com.capstone.renewal.global.jwt;

import com.capstone.renewal.global.BaseResponse;
import com.capstone.renewal.global.error.ErrorCode;
import com.capstone.renewal.global.redis.RedisDao;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisDao redisDao;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        request.getMethod();
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return (
                pathMatcher.match("/api/app/users/auth/duplication", path) && request.getMethod().equals("POST") || // 회원가입 > 아이디중복확인
                pathMatcher.match("/api/app/users/auth/registration", path) && request.getMethod().equals("POST") || // 회원가입
                pathMatcher.match("/api/app/users/auth/login", path) && request.getMethod().equals("POST") // 로그인
                );
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String jwt = getJwtFromRequest(request);
        if (jwt == null) {
            sendErrorResponse(response, ErrorCode.TOKEN_NOT_EXIST);
            return; // 토큰이 없는 경우 여기서 처리 중단
        }
        try {
            // Redis에서 로그아웃 여부 확인
            String isLogout = redisDao.getValues(jwt);
            if (!ObjectUtils.isEmpty(isLogout)) {
                sendErrorResponse(response, ErrorCode.INVALID_TOKEN);
                return; // 로그아웃된 토큰 처리 중단
            }

            String uid = jwtTokenProvider.getUserUidFromJWT(jwt);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(uid, null, null);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            log.warn("the token is expired and not valid anymore", e);
            sendErrorResponse(response, ErrorCode.ACCESS_TOKEN_EXPIRED);
        } catch (IllegalArgumentException | SignatureException | UnsupportedJwtException e) {
            log.error("Error processing JWT", e);
            sendErrorResponse(response, ErrorCode.FAIL_AUTHENTICATION);
        }


    }

    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization"); // Authorization 이름의 헤더의 내용을 가져온다.
        if (bearerToken.startsWith("Bearer ")) { // 헤더의 내용이 Bearer 로 시작하는지 확인
            return bearerToken.substring("Bearer ".length()); // Bearer 이후의 내용이 토큰임. (AT or RT)
        }
        return null;
    }

    private void sendErrorResponse(HttpServletResponse httpServletResponse,ErrorCode errorCode) throws IOException{
        httpServletResponse.setCharacterEncoding("utf-8");
        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        httpServletResponse.setContentType(APPLICATION_JSON_VALUE);

        BaseResponse errorResponse = new BaseResponse(errorCode);
        //object를 텍스트 형태의 JSON으로 변환
        new ObjectMapper().writeValue(httpServletResponse.getWriter(), errorResponse);
    }
}
