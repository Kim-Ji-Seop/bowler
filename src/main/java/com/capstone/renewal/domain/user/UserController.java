package com.capstone.renewal.domain.user;

import com.capstone.renewal.domain.user.dto.request.DuplicationUidRequest;
import com.capstone.renewal.domain.user.dto.request.LoginRequest;
import com.capstone.renewal.domain.user.dto.request.SignUpRequest;
import com.capstone.renewal.domain.user.dto.response.DuplicationUidResponse;
import com.capstone.renewal.domain.user.dto.response.LoginResponse;
import com.capstone.renewal.domain.user.dto.response.SignUpResponse;
import com.capstone.renewal.global.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/app")
@Tag(name = "UserController", description = "User API")
public class UserController {
    private final UserService userService;
    /*
     * 회원가입 - 중복확인
     */
    @PostMapping("/users/auth/duplication")
    @Operation(summary = "아이디 중복확인", description = "회원가입할 때 아이디 중복확인하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청에 성공하였습니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "4001", description = "중복된 아이디 입니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "4002", description = "아이디를 입력해주세요.", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<BaseResponse<DuplicationUidResponse>> checkDuplicationUid(@RequestBody DuplicationUidRequest duplicationUidRequest){
        boolean isDuplicated = userService.uidDuplicationCheck(duplicationUidRequest);
        DuplicationUidResponse response = new DuplicationUidResponse(isDuplicated);
        return ResponseEntity.ok(new BaseResponse<>(response));
    }
    /*
     * 회원가입
     */
    @PostMapping("/users/auth/registration")
    @Operation(summary = "회원가입", description = "회원가입 API")
    public ResponseEntity<BaseResponse<SignUpResponse>> signUp(@RequestBody SignUpRequest request){
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
}
