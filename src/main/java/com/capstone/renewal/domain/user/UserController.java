package com.capstone.renewal.domain.user;

import com.capstone.renewal.domain.user.dto.request.DuplicationUidRequest;
import com.capstone.renewal.domain.user.dto.request.SignUpRequest;
import com.capstone.renewal.domain.user.dto.response.DuplicationUidResponse;
import com.capstone.renewal.domain.user.dto.response.SignUpResponse;
import com.capstone.renewal.global.BaseResponse;
import lombok.AllArgsConstructor;
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
}
