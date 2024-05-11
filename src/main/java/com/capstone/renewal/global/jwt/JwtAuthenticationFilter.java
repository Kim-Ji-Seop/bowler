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
        // 1. Request에서 토큰을 꺼낸다.
        String jwt = getJwtFromRequest(request);
        ErrorCode errorCode=null;
        if(jwt==null){
            // 2. 토큰이 빈값이면? -> 오류
            errorCode = ErrorCode.TOKEN_NOT_EXIST;
            sendErrorResponse(response,ErrorCode.TOKEN_NOT_EXIST);
        }else{
            // 3. 로그아웃된 유저의 요청인지 확인
            try{
                //Redis 에 해당 accessToken logout 여부 확인
                String isLogout = redisDao.getValues(jwt);
                if (ObjectUtils.isEmpty(isLogout)) {
                    String uid = jwtTokenProvider.getUserUidFromJWT(jwt); //jwt에서 사용자 id를 꺼낸다.

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(uid, null, null); //id를 인증한다.
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); //기본적으로 제공한 details 세팅
                    SecurityContextHolder.getContext().setAuthentication(authentication); //세션에서 계속 사용하기 위해 securityContext에 Authentication 등록
                }
                filterChain.doFilter(request, response);

                // jwt 토큰 유효성 검사
            }catch (IllegalArgumentException e) {
                log.error("an error occured during getting username from token", e);
                sendErrorResponse(response,ErrorCode.INVALID_TOKEN);
            } catch (ExpiredJwtException e) {
                log.warn("the token is expired and not valid anymore", e);
                sendErrorResponse(response,ErrorCode.ACCESS_TOKEN_EXPIRED);
            } catch(SignatureException e){
                log.error("Authentication Failed. Username or Password not valid.");
                sendErrorResponse(response,ErrorCode.FAIL_AUTHENTICATION);
            }catch(UnsupportedJwtException e){
                log.error("UnsupportedJwt");
                sendErrorResponse(response,ErrorCode.FAIL_AUTHENTICATION);
            }

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
