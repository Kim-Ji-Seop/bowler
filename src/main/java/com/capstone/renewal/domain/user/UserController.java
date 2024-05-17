package com.capstone.renewal.domain.user;

import com.capstone.renewal.domain.user.dto.request.DuplicationUidRequest;
import com.capstone.renewal.domain.user.dto.request.LoginRequest;
import com.capstone.renewal.domain.user.dto.request.SignUpRequest;
import com.capstone.renewal.domain.user.dto.response.*;
import com.capstone.renewal.domain.user.service.UserService;
import com.capstone.renewal.global.BaseResponse;
import com.capstone.renewal.global.jwt.JwtAuthenticationFilter;
import com.capstone.renewal.global.jwt.JwtTokenProvider;
import com.capstone.renewal.global.redis.RedisDao;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/app")
@Tag(name = "UserController", description = "User API")
public class UserController {
    private final UserService userService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtTokenProvider jwtTokenProvider;

    private final RedisDao redisDao;
    /*
     * 회원가입 - 중복확인
     */
    @PostMapping("/users/auth/duplication")
    @Operation(summary = "아이디 중복확인", description = "회원가입할 때 아이디 중복확인하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청에 성공하였습니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "4001", description = "중복된 아이디 입니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "4002", description = "아이디를 입력해주세요.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "4006", description = "아이디는 영문자로 시작하고 영문자 또는 숫자를 포함할 수 있으며 최대 16자입니다.", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<BaseResponse<DuplicationUidResponse>> checkDuplicationUid(@Valid @RequestBody DuplicationUidRequest duplicationUidRequest){
        boolean isDuplicated = userService.uidDuplicationCheck(duplicationUidRequest);
        DuplicationUidResponse response = new DuplicationUidResponse(isDuplicated);
        return ResponseEntity.ok(new BaseResponse<>(response));
    }
    /*
     * 회원가입
     */
    @PostMapping("/users/auth/registration")
    @Operation(summary = "회원가입", description = "회원가입 API")
    public ResponseEntity<BaseResponse<SignUpResponse>> signUp(@Valid @RequestBody SignUpRequest request){
        SignUpResponse response = userService.insertUserAndReturn(request);
        return ResponseEntity.ok(new BaseResponse<>(response));
    }
    /*
     * 로그인 - 아이디/패스워드
     */
    @PostMapping("/users/auth/login")
    @Operation(summary = "로그인", description = "로그인 API")
    public ResponseEntity<BaseResponse<LoginResponse>> signIn(@RequestBody LoginRequest request){
        LoginResponse response = userService.loginUser(request);
        return ResponseEntity.ok(new BaseResponse<>(response));
    }
    /*
     * 자동로그인
     * 여기까지 요청이 왔다는 것은, AT는 유효하다는 뜻.
     * FE 에서는 AccessToken을 보내줘야함
     */
    @GetMapping("/users/auth/auto-login")
    @Operation(summary = "자동 로그인", description = "자동 로그인 API")
    public ResponseEntity<BaseResponse<AutoLoginResponse>> autoLogin(HttpServletRequest request){
        // 1. Jwt 필터 클래스에서 헤더에 있는 Jwt를 가져온다.
        String jwtToken = jwtAuthenticationFilter.getJwtFromRequest(request);
        // 2. 해당 jwt에서 uid 정보를 가져온다.
        String userUid = jwtTokenProvider.getUserUidFromJWT(jwtToken);
        // 3. uid를 서비스 레이어에 전달한다.
        AutoLoginResponse autoLoginResponse = userService.autoLogin(userUid);
        // 4. 결괏값 Return (jwt토큰은 제외한 유저 정보를 반환)
        return ResponseEntity.ok(new BaseResponse<>(autoLoginResponse));
    }
    /*
     * 리프레쉬 토큰 재발급
     * FE는, 이미 AccessToken은 유효하지 않음이 자명하기에, RefreshToken을 요청으로 넣어줘야한다.
     */
    @GetMapping("/users/auth/reissue")
    ResponseEntity<BaseResponse<LoginResponse>> reissue(HttpServletRequest request) throws JsonProcessingException {
        String jwtToken = jwtAuthenticationFilter.getJwtFromRequest(request);
        String userUid=jwtTokenProvider.getUserUidFromJWT(jwtToken);
        LoginResponse postLoginRes = userService.reissue(userUid);

        return ResponseEntity.ok(new BaseResponse<>(postLoginRes));
    }
    /*
     * 로그아웃
     * 로그아웃을 한 유저가 누구인지는 알아야하기 때문에, 이 또한 토큰으로 구분한다.
     */
    @PostMapping("/users/auth/logout")
    public ResponseEntity<BaseResponse<LogoutResponse>> logout(HttpServletRequest request){
        String jwtToken=jwtAuthenticationFilter.getJwtFromRequest(request);
        String userUid=jwtTokenProvider.getUserUidFromJWT(jwtToken);

        LogoutResponse postLogoutRes = userService.logout(userUid,jwtToken);
        return  ResponseEntity.ok(new BaseResponse<>(postLogoutRes));
    }
}
