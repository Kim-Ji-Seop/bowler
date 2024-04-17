package com.capstone.renewal.domain.user;

import com.capstone.renewal.domain.user.dto.request.DuplicationUidRequest;
import com.capstone.renewal.domain.user.dto.request.LoginRequest;
import com.capstone.renewal.domain.user.dto.request.SignUpRequest;
import com.capstone.renewal.domain.user.dto.response.DuplicationUidResponse;
import com.capstone.renewal.domain.user.dto.response.LoginResponse;
import com.capstone.renewal.domain.user.dto.response.SignUpResponse;
import com.capstone.renewal.global.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/app")
public class UserController {
    private final UserService userService;
    /*
     * 회원가입 - 중복확인
     */
    @PostMapping("/users/auth/duplication")
    public ResponseEntity<BaseResponse<DuplicationUidResponse>> checkDuplicationUid(@RequestBody DuplicationUidRequest duplicationUidRequest){
        boolean isDuplicated = userService.uidDuplicationCheck(duplicationUidRequest);
        DuplicationUidResponse response = new DuplicationUidResponse(isDuplicated);
        return ResponseEntity.ok(new BaseResponse<>(response));
    }
    /*
     * 회원가입
     */
    @PostMapping("/users/auth/registration")
    public ResponseEntity<BaseResponse<SignUpResponse>> signUp(@RequestBody SignUpRequest request){
        SignUpResponse response = userService.insertUserAndReturn(request);
        return ResponseEntity.ok(new BaseResponse<>(response));
    }
    /*
     * 로그인 - 아이디/패스워드
     */
    @PostMapping("/users/auth/login")
    public ResponseEntity<BaseResponse<LoginResponse>> signIn(@RequestBody LoginRequest request){
        LoginResponse response = userService.loginUser(request);
        return ResponseEntity.ok(new BaseResponse<>(response));
    }
}
